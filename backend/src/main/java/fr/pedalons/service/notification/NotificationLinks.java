package fr.pedalons.service.notification;

import fr.pedalons.enums.NotificationSubjectType;

/**
 * Web paths for links sent outside the app.
 *
 * <p>The canonical English form of each route in {@code contracts/routes.yaml}: both routers
 * register every locale variant, so one form opens in whatever language the reader uses — the same
 * choice {@code AdContactEmailService} made. A route renamed there must be renamed here.
 */
final class NotificationLinks {

  /**
   * The profile's notification section, by its anchor: {@code NotificationPreferences} renders with
   * {@code id="notifications"}. The fragment is dropped harmlessly by a client that has no such
   * section, so it costs nothing on a page that hasn't caught up.
   */
  static final String PREFERENCES_PATH = "/profile#notifications";

  private NotificationLinks() {}

  /**
   * An invitation opens the team list, not the team: that is where pending invitations are shown
   * and accepted, and the team page itself may not be readable before joining.
   */
  static final String TEAMS_PATH = "/teams";

  static String subjectPath(NotificationSubjectType type, String teamSlug, String slug) {
    String segment =
        switch (type) {
          case RIDE -> "rides";
          case TRIP -> "trips";
          case POST -> "posts";
          case ROUTE -> "routes";
          case TEAM -> null;
        };
    return segment == null ? TEAMS_PATH : "/teams/" + teamSlug + "/" + segment + "/" + slug;
  }
}
