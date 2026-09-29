import 'common.dart';
import 'server_errors.dart';

/// Web counterpart: `error-states.e2e.ts` › « a 500 on the ride/team shows « Chargement
/// impossible », and « Réessayer » loads it » (audit P1). The 4xx half is `load_error_delay_test`.
///
/// A 5xx may pass on its own: `providerRetry` replays it three times (0.5 s, 1 s, 2 s) before
/// the page shows its error — a generic one, not the « introuvable » of a 404. Once the server
/// answers again, « Réessayer » loads the page.
///
/// The 500s are made by [ServerErrors] on the app's side of the wire — the e2e stack cannot fail
/// on demand (see its doc comment).
void main() {
  testApp(
    'A 500 on a ride or a team is retried briefly, then shows its error, and « Réessayer » loads the page',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Error owner');
      final team = await backend.newTeam(owner, 'Equipe en panne');
      final teamSlug = team['slug'] as String;
      final ride = await backend.newRide(owner, teamSlug, 'Sortie en panne');
      final rideSlug = ride['slug'] as String;

      await openAppSignedIn($, owner);
      await modules.navigation.waitUntilTabBarIsVisible();

      // ── The team ── first, on a fresh app: a ride page reads its team too, and a team already
      // in the cache would not be asked for again.
      final teamErrors = ServerErrors.on($, '/api/teams/$teamSlug');
      await openLink($, Paths.team(teamSlug));
      final teamDelay = await modules.teams.waitUntilLoadErrorIsShown(
        timeout: const Duration(seconds: 20),
      );
      expect(teamErrors.failed >= 4, isTrue);
      expect(teamDelay >= const Duration(seconds: 3), isTrue);

      teamErrors.stop();
      await modules.teams.retryLoad();
      await modules.teams.waitUntilTeamIsShown();

      // ── The ride ──
      final rideErrors = ServerErrors.on(
        $,
        '/api/teams/$teamSlug/rides/$rideSlug',
      );
      await openLink($, Paths.ride(teamSlug, rideSlug));
      final rideDelay = await modules.ride.waitUntilLoadErrorIsShown(
        timeout: const Duration(seconds: 20),
      );
      // One request, then three retries spread over 3.5 s.
      expect(rideErrors.failed, 4);
      expect(rideDelay >= const Duration(seconds: 3), isTrue);
      expect(modules.ride.loadErrorShows('Erreur de chargement'), isTrue);

      rideErrors.stop();
      await modules.ride.retryLoad();
      await modules.ride.waitUntilShown();
      expect(modules.ride.title, ride['name']);
    },
  );
}
