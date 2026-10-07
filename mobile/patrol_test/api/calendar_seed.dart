import 'backend_client.dart';

/// Seeding and reads for the calendar tests: a public ride, the ICS feed's token.
///
/// Mirrors `frontend/e2e/support/rides.ts` (`newRide` with `visibility: 'PUBLIC'`) and
/// `support/flow-account.ts` (`calendarTokenOf`).
extension CalendarSeed on BackendClient {
  /// A published, **public** ride at [dateTime], with one « Groupe A »: nothing but membership
  /// keeps it out of a calendar.
  Future<Json> newPublicRideAt(
    TestUser by,
    String teamSlug,
    String label,
    DateTime dateTime,
  ) => post(by, '/api/teams/$teamSlug/rides', {
    'name': unique(label),
    'media': markdownMedia(),
    'dateTime': wallTimeOf(dateTime),
    'status': 'PUBLISHED',
    'visibility': 'PUBLIC',
    'groups': [
      {'name': 'Groupe A'},
    ],
  });

  /// `GET /api/calendar/token`: `globalFeedUrl` and `teamFeedUrlTemplate` (`{teamSlug}` in it).
  Future<Json> calendarToken(TestUser who) => get(who, '/api/calendar/token');
}
