import 'common.dart';

/// Web counterpart: `flow-team.e2e.ts` › « joining a team », the non-joinable case (audit P0 #2),
/// a `test.fail` on the web — `TeamLayout.tsx` ignores `joinable` there. The app reads it.
void main() {
  testApp(
    'A public team that takes no join request says « Sur invitation », and nothing joins it',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Invite owner');
      final rider = await backend.newUser('Invite rider');
      final team = await backend.newTeam(
        owner,
        'Equipe sur invitation',
        visibility: 'PUBLIC',
        joinable: false,
      );
      final slug = team['slug'] as String;

      await openAppSignedIn($, rider);
      await modules.teams.openFromDiscovery(
        slug: slug,
        name: team['name'] as String,
      );
      expect(modules.teams.saysInviteOnly, isTrue);
      expect(modules.teams.offersJoin, isFalse);

      await modules.teams.tapInviteOnly();
      expect(modules.teams.offersLeave, isFalse);
      expect((await backend.team(rider, slug))['role'], isNull);
    },
  );
}
