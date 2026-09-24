package fr.pedalons.service.migration.live;

/**
 * Biketeam's export API could not serve the job — the snapshot, or a file for a reason that is not
 * the file itself. Unchecked, so that it crosses the per-element error boundaries of the mapping and
 * ends the attempt: {@code BiketeamLiveMigrationWorker} retries it when UNAVAILABLE.
 */
public class BiketeamExportException extends RuntimeException {

  public enum Kind {
    /**
     * A 4xx with biketeam's JSON error code: biketeam refused — no point retrying. Job error {@code
     * EXPORT_REFUSED}.
     */
    REFUSED("EXPORT_REFUSED"),
    /**
     * A network failure, a 3xx, a 5xx, or a 4xx that is not biketeam's (no JSON {@code code}: a
     * proxy, a restart, the export switched off): retried, then {@code EXPORT_UNAVAILABLE}.
     */
    UNAVAILABLE("EXPORT_UNAVAILABLE"),
    /** Unreadable snapshot, wrong schema version, wrong team. Job error {@code EXPORT_INVALID}. */
    INVALID("EXPORT_INVALID");

    private final String errorCode;

    Kind(String errorCode) {
      this.errorCode = errorCode;
    }

    public String errorCode() {
      return errorCode;
    }
  }

  private final Kind kind;

  public BiketeamExportException(Kind kind, String message) {
    super(message);
    this.kind = kind;
  }

  public BiketeamExportException(Kind kind, String message, Throwable cause) {
    super(message, cause);
    this.kind = kind;
  }

  public Kind kind() {
    return kind;
  }
}
