import 'backend_client.dart';

/// Seeding for the rides, home and trips tests: rides at a chosen time, registrations made behind
/// the app's back, trips with their stages.
///
/// Mirrors `frontend/e2e/support/rides.ts` and `support/routes.ts` (`newTrip`): same endpoints,
/// same request shapes.
extension RidesHomeTripsSeed on BackendClient {
  // ── Rides ───────────────────────────────────────────────────────────────

  /// A published, members-only ride at [dateTime], with [groups] (one « Groupe A » by default).
  /// A group is `{'name': …}`, plus `'maxParticipants'` for a capacity or `'leaderId'`.
  Future<Json> newRideAt(
    TestUser by,
    String teamSlug,
    String label,
    DateTime dateTime, {
    List<Json>? groups,
  }) => post(by, '/api/teams/$teamSlug/rides', {
    'name': unique(label),
    'media': markdownMedia(),
    'dateTime': dateTime.toUtc().toIso8601String(),
    'status': 'PUBLISHED',
    'visibility': 'TEAM',
    'groups':
        groups ??
        [
          {'name': 'Groupe A'},
        ],
  });

  /// Registers [who] in the ride's group, as the app's « Rejoindre » does.
  Future<void> joinGroup(
    TestUser who,
    String teamSlug,
    String rideSlug,
    String groupId,
  ) => post(who, '/api/teams/$teamSlug/rides/$rideSlug/groups/$groupId/join');

  // ── Trips ───────────────────────────────────────────────────────────────

  /// A published, members-only trip starting at [dateTime], with one stage per entry of
  /// [stages] — its name and its start. What the web suite's `newTrip` sends.
  Future<Json> newTrip(
    TestUser by,
    String teamSlug,
    String label, {
    required DateTime dateTime,
    required List<({String name, DateTime at})> stages,
  }) => post(by, '/api/teams/$teamSlug/trips', {
    'name': unique(label),
    'media': markdownMedia(),
    'dateTime': dateTime.toUtc().toIso8601String(),
    'status': 'PUBLISHED',
    'visibility': 'TEAM',
    'stages': [
      for (final stage in stages)
        {
          'name': stage.name,
          'dateTime': stage.at.toUtc().toIso8601String(),
          'media': markdownMedia(),
        },
    ],
  });

  /// The top-level comments of the stage of [stageSlug] — its own thread, addressed by the stage's
  /// slug alone (docs/LEDGER_*.md API-11).
  Future<List<Json>> stageComments(
    TestUser who,
    String teamSlug,
    String stageSlug,
  ) async =>
      ((await get(
                who,
                '/api/teams/$teamSlug/stages/$stageSlug/comments',
              ))['items']
              as List)
          .cast<Json>();

  /// The top-level comments of the trip itself.
  Future<List<Json>> tripComments(
    TestUser who,
    String teamSlug,
    String tripSlug,
  ) async =>
      ((await get(
                who,
                '/api/teams/$teamSlug/trips/$tripSlug/comments',
              ))['items']
              as List)
          .cast<Json>();

  /// The trip as [who] reads it — `registered` is the caller's own participation.
  Future<Json> trip(TestUser who, String teamSlug, String tripSlug) =>
      get(who, '/api/teams/$teamSlug/trips/$tripSlug');

  /// Cancels the trip as [by], its organiser: a PUT of the whole `TripRequest` with the status
  /// changed and every stage kept by its id — as the web suite's `requestOf` builds it.
  Future<Json> cancelTrip(TestUser by, String teamSlug, String tripSlug) async {
    final current = await trip(by, teamSlug, tripSlug);
    return put(by, '/api/teams/$teamSlug/trips/$tripSlug', {
      'name': current['name'],
      'media': current['media'],
      'dateTime': current['dateTime'],
      'status': 'CANCELLED',
      'visibility': current['visibility'],
      'routeSlug': current['routeSlug'],
      'stages': [
        for (final stage in (current['stages'] as List).cast<Json>())
          {
            'id': stage['id'],
            'name': stage['name'],
            'dateTime': stage['dateTime'],
            'routeSlug': (stage['route'] as Json?)?['slug'],
            'media': stage['media'],
          },
      ],
    });
  }
}

/// The id of the ride's group named [name], from the ride as the API returned it.
String groupIdNamed(Json ride, String name) =>
    (ride['groups'] as List).cast<Json>().firstWhere(
          (group) => group['name'] == name,
        )['id']
        as String;
