import 'api/rides_home_trips_seed.dart';
import 'api/routes_seed.dart';
import 'common.dart';

/// Web counterpart: `flow-moderation.e2e.ts` — a report reaches the team's queue, where the
/// moderator finds it. Beyond the posts and comments (`report_post_test`, `block_user_test`), the
/// `⋯` of the other detail pages: a ride, a trip, a route, an ad — each page closes behind its
/// report, the content leaving the reader's lists.
///
/// The moderator never sees reports about their own content: an organizer writes everything.
void main() {
  testApp(
    'A member reports a ride, a trip, a route and an ad from their pages; each report reaches the team’s queue',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final moderator = await backend.newUser('Details moderator');
      final author = await backend.newUser('Details author');
      final reporter = await backend.newUser('Details reporter');
      final team = await backend.newTeam(moderator, 'Equipe signalements');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, author, role: 'ORGANIZER');
      await backend.addMember(teamSlug, reporter);
      final ride = await backend.newRide(author, teamSlug, 'Sortie signalee');
      final trip = await backend.newTrip(
        author,
        teamSlug,
        'Voyage signale',
        dateTime: DateTime.now().add(const Duration(days: 3)),
        stages: [
          (name: 'Etape 1', at: DateTime.now().add(const Duration(days: 3))),
        ],
      );
      final route = await backend.newRoute(
        author,
        teamSlug,
        unique('Parcours signale'),
      );
      final ad = await backend.newAd(author, teamSlug, 'Annonce signalee');

      await openAppSignedIn($, reporter);

      // A ride.
      await openLink($, Paths.ride(teamSlug, ride['slug'] as String));
      await modules.ride.waitUntilShown();
      await modules.detailMenus.openRideMenu();
      await modules.moderation.waitUntilMenuIsShown();
      await modules.moderation.report();
      await modules.detailMenus.waitUntilRideClosed();
      expect(
        await backend.queueItem(moderator, teamSlug, ride['id'] as String),
        isNotNull,
      );

      // A trip.
      await openLink($, Paths.trip(teamSlug, trip['slug'] as String));
      await modules.trip.waitUntilShown();
      await modules.detailMenus.openTripMenu();
      await modules.moderation.waitUntilMenuIsShown();
      await modules.moderation.report();
      await modules.detailMenus.waitUntilTripClosed();
      expect(
        await backend.queueItem(moderator, teamSlug, trip['id'] as String),
        isNotNull,
      );

      // A route.
      await openLink($, Paths.route(teamSlug, route['slug'] as String));
      await modules.detailMenus.openRouteMenu();
      await modules.moderation.waitUntilMenuIsShown();
      await modules.moderation.report();
      await modules.detailMenus.waitUntilRouteClosed();
      expect(
        await backend.queueItem(moderator, teamSlug, route['id'] as String),
        isNotNull,
      );

      // An ad.
      await openLink($, Paths.ad(teamSlug, ad['slug'] as String));
      await modules.ad.waitUntilShown();
      await modules.ad.openMoreMenu();
      await modules.moderation.waitUntilMenuIsShown();
      await modules.moderation.report();
      await modules.detailMenus.waitUntilAdClosed();
      expect(
        await backend.queueItem(moderator, teamSlug, ad['id'] as String),
        isNotNull,
      );
    },
  );
}
