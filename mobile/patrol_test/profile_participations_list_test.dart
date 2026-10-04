import 'api/account_seed.dart';
import 'common.dart';

/// No dedicated web counterpart; the nearest is `calendar.e2e.ts` (« Inscrit · Groupe A »).
///
/// Kept apart from `profile_participations_count_test`, which pins a known defect of the badge:
/// inside `testAppKnownDefect`, a broken step of this path would pass unnoticed.
void main() {
  testApp(
    'A ride joined in the app is listed in « Mes sorties » › « À venir » and opens from there',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Listed owner');
      final member = await backend.newUser('Listed member');
      final team = await backend.newTeam(owner, 'Equipe liste');
      final teamSlug = team['slug'] as String;
      await backend.addMember(teamSlug, member);
      final ride = await backend.newRide(owner, teamSlug, 'Sortie listee');
      final rideSlug = ride['slug'] as String;
      final groupId =
          (ride['groups'] as List).cast<Json>().single['id'] as String;

      await openAppSignedIn($, member);
      await openLink($, Paths.ride(teamSlug, rideSlug));
      await modules.ride.waitUntilShown();
      await modules.ride.join(groupId);
      expect(await backend.upcomingParticipationCount(member), 1);

      // The profile not shown before the registration, the badge of « Mes sorties » reads it at once;
      // the row opens /profil/sorties on « À venir ».
      await openLink($, Paths.profile());
      await modules.profile.waitUntilShown();
      expect(await modules.profile.upcomingCount(), 1);

      await modules.profile.openUpcomingParticipations();
      await modules.profile.waitUntilParticipationIsListed(rideSlug);
      await modules.profile.openParticipation(rideSlug);
      await modules.ride.waitUntilShown();
      expect(modules.ride.title, ride['name']);
    },
  );
}
