import 'common.dart';

/// Web counterpart: « un 500 n'est pas un introuvable ; 404 lu une seule fois » (audit P1), met
/// on the way to P0 #1: a denied link must land on its error, and promptly.
///
/// A 4xx is an answer, not an outage: `providerRetry` (lib/core/utils/provider_retry.dart), set on
/// the app's `ProviderScope`, never replays it, so the outsider's 403 shows its error as soon as it
/// arrives — well within 10 s — instead of the ~38 s of loading skeleton Riverpod's default retry
/// used to produce. Only transient failures (network, timeouts, 5xx) are retried, briefly.
void main() {
  testApp(
    'An outsider’s link to a members-only ride shows its error within 10 s',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final owner = await backend.newUser('Delay owner');
      final outsider = await backend.newUser('Delay outsider');
      final team = await backend.newTeam(owner, 'Equipe lente');
      final teamSlug = team['slug'] as String;
      final ride = await backend.newRide(owner, teamSlug, 'Sortie refusee');

      await openAppSignedIn($, outsider);
      await modules.navigation.waitUntilTabBarIsVisible();
      await openLink($, Paths.ride(teamSlug, ride['slug'] as String));
      await modules.ride.waitUntilLoadErrorIsShown(
        timeout: const Duration(seconds: 10),
      );
    },
  );
}
