package fr.pedalons.common.exception;

import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.error.ErrorDetails;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

/**
 * A request the caller is allowed to make, just not yet — HTTP 429.
 *
 * <p>{@code retryAfterSeconds} is surfaced by {@code GlobalExceptionMapper} as a {@code Retry-After}
 * header, so a client can show "try again in N minutes" rather than guessing.
 */
@Getter
public class TooManyRequestsException extends PedalonsException {

  private final long retryAfterSeconds;

  public TooManyRequestsException(
      ErrorCode errorCode,
      long retryAfterSeconds,
      @Nullable ErrorDetails errorDetails,
      @Nullable Throwable cause) {
    super(errorCode, errorDetails, cause);
    this.retryAfterSeconds = Math.max(0, retryAfterSeconds);
  }

  public TooManyRequestsException(ErrorCode errorCode, long retryAfterSeconds) {
    this(errorCode, retryAfterSeconds, null, null);
  }

  @Override
  public Response.Status getStatus() {
    return Response.Status.TOO_MANY_REQUESTS;
  }
}
