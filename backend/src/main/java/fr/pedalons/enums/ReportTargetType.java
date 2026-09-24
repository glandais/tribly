package fr.pedalons.enums;

/**
 * What a member can report. Stored by name in {@code content_reports.target_type}: renaming a
 * constant is a data migration.
 *
 * <p>An attachment or an avatar is not a target of its own: the member reports the content that
 * carries it, or the member.
 */
public enum ReportTargetType {
  COMMENT,
  POST,
  AD,
  RIDE,
  TRIP,
  ROUTE,
  /** Another member of the team; the target id is the user's. */
  MEMBER
}
