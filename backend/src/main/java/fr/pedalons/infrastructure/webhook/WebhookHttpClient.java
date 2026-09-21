package fr.pedalons.infrastructure.webhook;

import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Posts a JSON document to a URL a team administrator typed in — which makes it a server-side
 * request forgery surface, and everything here is about narrowing it.
 *
 * <ul>
 *   <li>{@code https} only;
 *   <li>the host is resolved first, and refused if <em>any</em> of its addresses is loopback,
 *       private, link-local, multicast or unspecified — the metadata endpoints of cloud hosts and
 *       the services on the Docker network alike;
 *   <li>redirects are not followed, so an allowed host cannot bounce the request inward;
 *   <li>short timeouts, and the response body is never read back to the caller.
 * </ul>
 *
 * <p>A host that resolves to a public address when checked and to a private one when connected (DNS
 * rebinding) is not covered: {@code HttpClient} resolves again. Closing that gap would mean pinning
 * the address, which {@code HttpClient} does not allow without giving up TLS name checks.
 */
@ApplicationScoped
public class WebhookHttpClient {

  private static final Duration TIMEOUT = Duration.ofSeconds(10);

  /** Tests post to a local server; nothing else should ever set this. */
  @ConfigProperty(
      name = "pedalons.notifications.webhook.allow-private-addresses",
      defaultValue = "false")
  boolean allowPrivateAddresses;

  private final HttpClient client =
      HttpClient.newBuilder()
          .followRedirects(HttpClient.Redirect.NEVER)
          .connectTimeout(TIMEOUT)
          .build();

  /** Thrown for a URL that must not be called at all — never retried. */
  public static class ForbiddenTargetException extends Exception {
    public ForbiddenTargetException(String message) {
      super(message);
    }
  }

  /** Posts {@code json} and returns the HTTP status. */
  public int post(URI url, String json)
      throws ForbiddenTargetException, IOException, InterruptedException {
    checkTarget(url, allowPrivateAddresses);
    HttpRequest request =
        HttpRequest.newBuilder(url)
            .timeout(TIMEOUT)
            .header("Content-Type", "application/json")
            .header("User-Agent", "Pedalons-Webhook/1")
            .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
            .build();
    return client.send(request, HttpResponse.BodyHandlers.discarding()).statusCode();
  }

  /**
   * The syntactic half of the check, cheap enough to run when the URL is saved: https, a host, no
   * credentials in the URL, and no literal local address or {@code localhost}.
   */
  public static boolean isAcceptable(URI url) {
    if (!"https".equalsIgnoreCase(url.getScheme())
        || url.getHost() == null
        || url.getRawUserInfo() != null) {
      return false;
    }
    String host = url.getHost().toLowerCase(Locale.ROOT);
    if (host.equals("localhost") || host.endsWith(".localhost") || host.endsWith(".local")) {
      return false;
    }
    if (isIpLiteral(host)) {
      try {
        return !isInternal(InetAddress.getByName(host));
      } catch (UnknownHostException e) {
        return false;
      }
    }
    return true;
  }

  static void checkTarget(URI url, boolean allowPrivate) throws ForbiddenTargetException {
    if (!"https".equalsIgnoreCase(url.getScheme()) && !allowPrivate) {
      throw new ForbiddenTargetException("Only https webhooks are allowed");
    }
    if (url.getHost() == null) {
      throw new ForbiddenTargetException("No host");
    }
    if (allowPrivate) {
      return;
    }
    InetAddress[] addresses;
    try {
      addresses = InetAddress.getAllByName(url.getHost());
    } catch (UnknownHostException e) {
      throw new ForbiddenTargetException("Unknown host " + url.getHost());
    }
    for (InetAddress address : addresses) {
      if (isInternal(address)) {
        throw new ForbiddenTargetException("Host resolves to a non-public address");
      }
    }
  }

  static boolean isInternal(InetAddress address) {
    if (address.isAnyLocalAddress()
        || address.isLoopbackAddress()
        || address.isLinkLocalAddress()
        || address.isSiteLocalAddress()
        || address.isMulticastAddress()) {
      return true;
    }
    byte[] bytes = address.getAddress();
    if (bytes.length == 4) {
      int first = bytes[0] & 0xff;
      int second = bytes[1] & 0xff;
      // 100.64.0.0/10 (carrier-grade NAT) and 0.0.0.0/8, which the JDK predicates do not cover.
      return (first == 100 && second >= 64 && second <= 127) || first == 0;
    }
    // fc00::/7, unique local — the IPv6 private range, which isSiteLocalAddress (fec0::/10) misses.
    return (bytes[0] & 0xfe) == 0xfc;
  }

  private static boolean isIpLiteral(String host) {
    return host.startsWith("[") || host.contains(":") || host.matches("[0-9.]+");
  }
}
