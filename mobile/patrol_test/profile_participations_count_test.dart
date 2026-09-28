import 'api/account_seed.dart';
import 'common.dart';

/// No dedicated web counterpart; the nearest is `calendar.e2e.ts` (« Inscrit · Groupe A »). The
/// list behind the counter is covered by `profile_participations_list_test`.
///
/// Guarantee: the « Mes sorties à venir » badge counts a registration made in the app itself.
/// `participationCountProvider` lives as long as the app (the profile is a tab), so a successful
/// join invalidates it through `notifyParticipationChanged`
/// (`rides/providers/participation_changes.dart`), the one place every view derived from my
/// participations is refreshed from.
void main() {
  testApp(
    'The « Mes sorties à venir » badge counts a registration made in the app',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Count owner');
      final member = await backend.newUser('Count member');
      final team = await backend.newTeam(owner, 'Equipe compteur');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, member);
      final ride = await backend.newRide(owner, teamSlug, 'Sortie comptee');
      final rideSlug = ride['slug'] as String;
      final groupId =
          (ride['groups'] as List).cast<Json>().single['id'] as String;

      await openAppSignedIn($, member);
      await modules.navigation.goToProfile();
      await modules.profile.waitUntilShown();
      expect(await modules.profile.upcomingCount(), 0);

      await openLink($, Paths.ride(teamSlug, rideSlug));
      await modules.ride.waitUntilShown();
      await modules.ride.join(groupId);
      expect(await backend.registeredGroupIds(member, teamSlug, rideSlug), [
        groupId,
      ]);
      expect(await backend.upcomingParticipationCount(member), 1);

      await openLink($, Paths.profile());
      await modules.profile.waitUntilShown();
      await modules.profile.waitUntilUpcomingCountIs(1);
    },
  );
}
