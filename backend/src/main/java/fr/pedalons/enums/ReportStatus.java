package fr.pedalons.enums;

/** Where a report stands. Stored by name in {@code content_reports.status}. */
public enum ReportStatus {
  /** Waiting for a moderator. */
  OPEN,
  /** A moderator removed the content. */
  REMOVED,
  /** A moderator kept the content. */
  DISMISSED
}
