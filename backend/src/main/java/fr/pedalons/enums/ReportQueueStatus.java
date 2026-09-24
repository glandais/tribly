package fr.pedalons.enums;

/** Which half of a moderation queue to read. */
public enum ReportQueueStatus {
  /** Targets with reports waiting for a decision. */
  OPEN,
  /** Targets already decided, most recent first. */
  RESOLVED
}
