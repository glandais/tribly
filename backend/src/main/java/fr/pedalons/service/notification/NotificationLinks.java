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

  static final String PREFERENCES_PATH = "/profile";

  private NotificationLinks() {}

  static String subjectPath(NotificationSubjectType type, String teamSlug, String slug) {
    String segment =
        switch (type) {
          case RIDE -> "rides";
          case TRIP -> "trips";
          case POST -> "posts";
          case ROUTE -> "routes";
        };
    return "/teams/" + teamSlug + "/" + segment + "/" + slug;
  }
}
