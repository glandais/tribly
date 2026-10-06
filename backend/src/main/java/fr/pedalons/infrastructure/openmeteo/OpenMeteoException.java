package fr.pedalons.infrastructure.openmeteo;

/**
 * A failed forecast request. The message is safe to log and to store: it never contains the request
 * URL, which may carry the API key.
 */
public class OpenMeteoException extends RuntimeException {

  private final boolean rateLimited;

  public OpenMeteoException(String message, boolean rateLimited) {
    super(message);
    this.rateLimited = rateLimited;
  }

  /** The provider answered 429: retry at the next full hour, and open the breaker. */
  public boolean isRateLimited() {
    return rateLimited;
  }
}
