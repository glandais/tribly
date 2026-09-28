import 'common.dart';

/// Web counterpart: `slug-change.e2e.ts` (audit P0 #7). A slug is renamed on the web; every link
/// shared before — a message, a calendar event, a notification — keeps the old one, and the app
/// must still open what it points to.
void main() {
  testApp(
    'Links under a team’s and a ride’s old slugs still open the team and the ride',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Slug owner');
      final member = await backend.newUser('Slug member');
      final team = await backend.newTeam(owner, 'Equipe renommee');
      final oldTeamSlug = team['slug'] as String;
      await backend.addMember(oldTeamSlug, member);
      final ride = await backend.newRide(owner, oldTeamSlug, 'Sortie renommee');
      final oldRideSlug = ride['slug'] as String;

      await backend.renameSlug(
        owner,
        '/api/teams/$oldTeamSlug/rides/$oldRideSlug',
        'sortie-nouvelle',
      );
      await backend.renameSlug(owner, '/api/teams/$oldTeamSlug', 'equipe');

      await openAppSignedIn($, member);
      await openLink($, Paths.ride(oldTeamSlug, oldRideSlug));
      await modules.ride.waitUntilShown();
      expect(modules.ride.title, ride['name']);

      await openLink($, Paths.team(oldTeamSlug));
      await modules.teams.waitUntilTeamIsShown();
      expect(modules.teams.offersLeave, isTrue);
    },
  );
}
