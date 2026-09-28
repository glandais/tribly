import 'api/rides_home_trips_seed.dart';
import 'common.dart';

/// Web counterpart: none — the web home has no « À venir » row; nearest is `rides.e2e.ts` ›
/// registration.
///
/// Pins two fixes. The row lists the nearest outings first: `upcomingProvider` asks
/// `GET /api/publications` for the next 30 days with `sortDir=ASC` (the endpoint's default is
/// newest first, so with more than ten outings ahead the nearest used to drop out of the row). And
/// a registration through « Rejoindre » refreshes the row, so the joined card says « Inscrit ».
///
/// The team's rides are asserted relative to each other only: `/api/publications` also lists the
/// public teams of the shared e2e database, whose rides may sit among ours.
void main() {
  testApp(
    'The « À venir » carousel shows the nearest outings first, and « Rejoindre » on a single-group ride registers through autoJoin',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Upcoming owner');
      final member = await backend.newUser('Upcoming member');
      final team = await backend.newTeam(owner, 'Equipe a venir');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, member);

      final now = DateTime.now();
      final soonest = await backend.newRideAt(
        owner,
        teamSlug,
        'Sortie dans deux heures',
        now.add(const Duration(hours: 2)),
      );
      final twoGroups = await backend.newRideAt(
        owner,
        teamSlug,
        'Sortie a deux groupes',
        now.add(const Duration(hours: 3)),
        groups: [
          {'name': 'Groupe rapide'},
          {'name': 'Groupe cool'},
        ],
      );
      final later = <Json>[];
      for (var day = 1; day <= 10; day++) {
        final at = DateTime(now.year, now.month, now.day + day, 10);
        later.add(
          await backend.newRideAt(owner, teamSlug, 'Sortie J+$day', at),
        );
      }
      final soonestSlug = soonest['slug'] as String;
      final twoGroupsSlug = twoGroups['slug'] as String;
      final dayOneSlug = later.first['slug'] as String;
      final soonestGroupId = groupIdNamed(soonest, 'Groupe A');

      await openAppSignedIn($, member);
      // Twelve outings ahead, ten slots: the nearest ones fill the row, in date order.
      await modules.home.waitUntilUpcomingHas(soonestSlug);
      expect(
        await modules.home.upcomingInOrder([
          soonestSlug,
          twoGroupsSlug,
          dayOneSlug,
        ]),
        isTrue,
      );
      expect(modules.home.upcomingOffersJoin(soonestSlug), isTrue);
      expect(modules.home.upcomingOffersChooseGroup(twoGroupsSlug), isTrue);

      // One group: « Rejoindre » opens the ride, which registers on its own.
      await modules.home.joinFromUpcoming(soonestSlug);
      await modules.ride.waitUntilShown();
      await modules.ride.waitUntilRegisteredIn(soonestGroupId);
      expect(await backend.registeredGroupIds(member, teamSlug, soonestSlug), [
        soonestGroupId,
      ]);

      // Two groups: « Choisir un groupe » opens the ride and registers nothing. The ride is
      // pushed above the tab shell, so a link to the home brings the tabs back.
      await openLink($, Paths.home());
      await modules.home.waitUntilShown();
      await modules.home.waitUntilUpcomingHas(twoGroupsSlug);
      await modules.home.chooseGroupFromUpcoming(twoGroupsSlug);
      await modules.ride.waitUntilShown();
      await modules.ride.settle();
      expect(
        await backend.registeredGroupIds(member, teamSlug, twoGroupsSlug),
        <String>[],
      );

      // Back on the home, the joined ride says so.
      await openLink($, Paths.home());
      await modules.home.waitUntilShown();
      await modules.home.rewindUpcoming();
      await modules.home.waitUntilUpcomingHas(soonestSlug);
      await modules.home.waitUntilUpcomingShowsRegistered(soonestSlug);
      expect(modules.home.upcomingShowsRegistered(soonestSlug), isTrue);
      expect(modules.home.upcomingOffersJoin(soonestSlug), isFalse);
    },
  );
}
