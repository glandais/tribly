package fr.pedalons.service.migration.live;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.Nullable;

/**
 * A response body read as an {@link InputStream} that cannot block for ever: {@code
 * HttpRequest.timeout} only bounds the wait for the response headers, and {@code
 * BodyHandlers.ofInputStream()} then blocks as long as the peer keeps the connection open without
 * sending anything — the worker's thread with it, and the one-job-at-a-time scheduler behind it.
 *
 * <p>Each read waits at most {@code idleTimeout} for the next bytes, and the whole body at most
 * {@code totalTimeout} from the headers. Past either, the exchange is cancelled and the read fails
 * with an {@link HttpTimeoutException} — an {@link IOException}, which {@link BiketeamExportClient}
 * turns into UNAVAILABLE: retried.
 *
 * <p>Every chunk received also calls {@code onData}, on the reading thread — the job's heartbeat
 * ({@link JobProgressTracker#heartbeat()}), which rate-limits itself: a long transfer is proof of
 * life, not silence. Whatever it throws goes to the reader.
 *
 * <p>Unlike {@code BodySubscribers.fromSubscriber}, the body is handed out as soon as the headers
 * are in ({@link #getBody()} is already complete): {@code HttpClient.send} does not wait for the end
 * of the body, which is exactly what the reads bound.
 */
final class TimedBodySubscriber extends InputStream
    implements HttpResponse.BodySubscriber<InputStream> {

  /** End of the body, and the peer's failure — queued after the data, in order. */
  private static final Object COMPLETE = new Object();

  private record Failure(Throwable error) {}

  private final BlockingQueue<Object> queue = new LinkedBlockingQueue<>();
  private final long idleNanos;
  private final long deadlineNanos;
  private final String what;
  private final Runnable onData;

  private volatile Flow.@Nullable Subscription subscription;
  private Iterator<ByteBuffer> buffers = Collections.emptyIterator();
  private @Nullable ByteBuffer current;
  private boolean done;
  private boolean closed;
  private boolean failed;

  /**
   * @param what the transfer, for the error message
   * @param onData called on the reading thread for every chunk received
   */
  TimedBodySubscriber(Duration idleTimeout, Duration totalTimeout, String what, Runnable onData) {
    this.idleNanos = idleTimeout.toNanos();
    this.deadlineNanos = System.nanoTime() + totalTimeout.toNanos();
    this.what = what;
    this.onData = onData;
  }

  static HttpResponse.BodyHandler<InputStream> handler(
      Duration idleTimeout, Duration totalTimeout, String what, Runnable onData) {
    return info -> new TimedBodySubscriber(idleTimeout, totalTimeout, what, onData);
  }

  /** Whether a read failed on the transfer itself — timeout, connection lost — not the content. */
  boolean failed() {
    return failed;
  }

  // ─── Flow.Subscriber, on the client's threads ─────────────────────────────

  @Override
  public void onSubscribe(Flow.Subscription s) {
    subscription = s;
    s.request(1);
  }

  @Override
  public void onNext(List<ByteBuffer> item) {
    queue.add(item);
  }

  @Override
  public void onError(Throwable throwable) {
    queue.add(new Failure(throwable));
  }

  @Override
  public void onComplete() {
    queue.add(COMPLETE);
  }

  @Override
  public CompletionStage<InputStream> getBody() {
    return CompletableFuture.completedFuture(this);
  }

  // ─── InputStream, on the reading thread ───────────────────────────────────

  @Override
  public int read() throws IOException {
    byte[] one = new byte[1];
    int n = read(one, 0, 1);
    return n < 0 ? -1 : one[0] & 0xff;
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    Objects.checkFromIndexSize(off, len, b.length);
    if (closed) {
      throw new IOException("Stream closed");
    }
    if (failed) {
      // Never an end of stream after a failure: a truncated body must not pass for a whole one.
      throw new IOException("Reading " + what + " failed earlier");
    }
    if (len == 0) {
      return 0;
    }
    ByteBuffer buffer = nextBuffer();
    if (buffer == null) {
      return -1;
    }
    int n = Math.min(len, buffer.remaining());
    buffer.get(b, off, n);
    return n;
  }

  @Override
  public int available() {
    ByteBuffer buffer = current;
    return buffer == null ? 0 : buffer.remaining();
  }

  private @Nullable ByteBuffer nextBuffer() throws IOException {
    while (true) {
      ByteBuffer buffer = current;
      if (buffer != null && buffer.hasRemaining()) {
        return buffer;
      }
      if (buffers.hasNext()) {
        current = buffers.next();
        continue;
      }
      if (done) {
        return null;
      }
      Object item = take();
      if (item == COMPLETE) {
        done = true;
        return null;
      }
      if (item instanceof Failure f) {
        failed = true;
        throw f.error() instanceof IOException io ? io : new IOException(f.error());
      }
      @SuppressWarnings("unchecked")
      List<ByteBuffer> list = (List<ByteBuffer>) item;
      buffers = list.iterator();
      Flow.Subscription s = subscription;
      if (s != null) {
        s.request(1);
      }
      onData.run();
    }
  }

  private Object take() throws IOException {
    long remaining = deadlineNanos - System.nanoTime();
    if (remaining <= 0) {
      throw timeout("did not complete in time");
    }
    Object item;
    try {
      item = queue.poll(Math.min(idleNanos, remaining), TimeUnit.NANOSECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      cancel();
      failed = true;
      throw new InterruptedIOException("Interrupted while reading " + what);
    }
    if (item == null) {
      throw timeout(
          deadlineNanos - System.nanoTime() <= 0
              ? "did not complete in time"
              : "stalled: no data for " + Duration.ofNanos(idleNanos).toSeconds() + "s");
    }
    return item;
  }

  private HttpTimeoutException timeout(String reason) {
    cancel();
    failed = true;
    return new HttpTimeoutException("Reading " + what + " " + reason);
  }

  private void cancel() {
    Flow.Subscription s = subscription;
    subscription = null;
    if (s != null) {
      s.cancel();
    }
  }

  @Override
  public void close() {
    if (!closed) {
      closed = true;
      cancel();
    }
  }
}
