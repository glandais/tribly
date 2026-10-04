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
   * The profile's notification settings, a page of their own: {@code profileNotifications} in
   * routes.yaml (a deeplink, so the app opens it when installed).
   */
  static final String PREFERENCES_PATH = "/profile/notifications";

  private NotificationLinks() {}

  /**
   * An invitation opens the team list, not the team: that is where pending invitations are shown
   * and accepted, and the team page itself may not be readable before joining.
   */
  static final String TEAMS_PATH = "/teams";

  static String subjectPath(NotificationSubjectType type, String teamSlug, String slug) {
    if (type == NotificationSubjectType.REPORT) {
      // The team's moderation queue: {@code teamAdminReports} in routes.yaml. A platform admin
      // reaches it too.
      return "/teams/" + teamSlug + "/admin/reports";
    }
    String segment =
        switch (type) {
          case RIDE -> "rides";
          case TRIP -> "trips";
          case POST -> "posts";
          case ROUTE -> "routes";
          case TEAM, REPORT -> null;
        };
    return segment == null ? TEAMS_PATH : "/teams/" + teamSlug + "/" + segment + "/" + slug;
  }
}
