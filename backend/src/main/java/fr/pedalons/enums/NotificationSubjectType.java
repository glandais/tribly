package fr.pedalons.enums;

/**
 * What a notification points at — the page a client opens when it is tapped.
 *
 * <p>Its own enum rather than {@link EntityType}: the API contract should list the things a
 * notification can be about, not the nineteen entity types the access checker knows.
 */
public enum NotificationSubjectType {
  RIDE,
  TRIP,
  POST,
  ROUTE,
  /**
   * The team itself — an invitation to it. Opens the team list, where pending invitations are
   * accepted; the subject slug is the team's.
   */
  TEAM,
  /**
   * A report waiting in a team's moderation queue. Opens that queue; the subject slug is the
   * team's. Deliberately carries no excerpt: a push shows on the lock screen.
   */
  REPORT
}
