package fr.pedalons.enums;

/**
 * Lifecycle of a {@code biketeam_migrations} row: first a grant, then — once biketeam redeems it —
 * a job. Only the last four are ever reported to biketeam as a job status.
 */
public enum BiketeamMigrationStatus {
  /** Confirmed by a Pédalons user; the grant is waiting for biketeam's trigger. */
  GRANTED,
  /** The grant lapsed before biketeam redeemed it. Not a job. */
  EXPIRED,
  /** Redeemed; waiting for the worker (first run or retry after {@code next_attempt_at}). */
  QUEUED,
  /** Claimed by the worker. */
  RUNNING,
  /** Done — possibly with per-item failures, which are counted, not fatal. */
  SUCCEEDED,
  /** Gave up; {@code error_code} says why. */
  FAILED;

  /** Whether the row is a job, i.e. was triggered by biketeam. */
  public boolean isJob() {
    return this != GRANTED && this != EXPIRED;
  }

  public boolean isActive() {
    return this == QUEUED || this == RUNNING;
  }
}
