package fr.pedalons.enums;

/**
 * What a notification points at — the page a client opens when it is tapped.
 *
 * <p>Its own enum rather than {@link EntityType}: the API contract should list the four things a
 * notification can be about, not the nineteen entity types the access checker knows.
 */
public enum NotificationSubjectType {
  RIDE,
  TRIP,
  POST,
  ROUTE
}
