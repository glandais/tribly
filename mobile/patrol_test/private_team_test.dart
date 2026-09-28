import 'common.dart';

/// Web counterparts: the denied roles of `routes-render.e2e.ts` (audit P0 #1) and the private team
/// of `ssr-session.e2e.ts` (P0 #9). The app has no server rendering to leak through; what it must
/// not do is show a members-only team, or its rides, to someone outside it — whether they search
/// for it or follow a link a member shared.
void main() {
  testApp(
    'A members-only team stays out of discovery, and its links show an error to an outsider',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Private owner');
      final outsider = await backend.newUser('Private outsider');
      final team = await backend.newTeam(owner, 'Equipe privee');
      final teamSlug = team['slug'] as String;
      final ride = await backend.newRide(owner, teamSlug, 'Sortie privee');

      await openAppSignedIn($, outsider);
      expect(
        await modules.teams.discoveryFinds(
          slug: teamSlug,
          name: team['name'] as String,
        ),
        isFalse,
      );

      // A 403 is not retried: the error shows at once — load_error_delay_test.dart bounds it.
      await openLink($, Paths.ride(teamSlug, ride['slug'] as String));
      await modules.ride.waitUntilLoadErrorIsShown();
      expect(modules.ride.title, isNull);

      await openLink($, Paths.team(teamSlug));
      await modules.teams.waitUntilLoadErrorIsShown();
    },
  );
}
