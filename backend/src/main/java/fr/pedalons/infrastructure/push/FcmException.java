package fr.pedalons.infrastructure.push;

/**
 * An FCM send that did not succeed.
 *
 * <p>{@link #tokenInvalid()} is the distinction that matters to the caller: a dead token must be
 * purged and never retried, anything else is worth another attempt.
 */
public class FcmException extends Exception {

  private final boolean tokenInvalid;

  public FcmException(String message, boolean tokenInvalid) {
    super(message);
    this.tokenInvalid = tokenInvalid;
  }

  public FcmException(String message, Throwable cause) {
    super(message, cause);
    this.tokenInvalid = false;
  }

  public boolean tokenInvalid() {
    return tokenInvalid;
  }
}
