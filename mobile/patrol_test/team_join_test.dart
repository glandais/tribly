import 'common.dart';

/// Web counterpart: `flow-team.e2e.ts` › « joining a team » (audit P0 #2).
void main() {
  testApp(
    'A rider finds a public, joinable team, joins it, and can leave it again',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Join owner');
      final rider = await backend.newUser('Join rider');
      final team = await backend.newTeam(
        owner,
        'Equipe ouverte',
        visibility: 'PUBLIC',
        joinable: true,
      );
      final slug = team['slug'] as String;

      await openAppSignedIn($, rider);
      await modules.teams.openFromDiscovery(
        slug: slug,
        name: team['name'] as String,
      );
      expect(modules.teams.offersJoin, isTrue);

      await modules.teams.join();
      expect((await backend.team(rider, slug))['role'], 'MEMBER');

      await modules.teams.leave();
      expect((await backend.team(rider, slug))['role'], isNull);
    },
  );
}
