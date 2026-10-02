import 'api/device_seed.dart';
import 'common.dart';

/// Web counterpart: `flow-device.e2e.ts` › « « Associer Hammerhead » leaves for Hammerhead… » and
/// « the Karoo's fallback QR opens the Hammerhead step alone… » (docs/LEDGER_*.md API-63). The
/// OAuth itself happens in the system browser, out of the app's reach: what is played here is what
/// comes back — the callback's `/karoo?gps_error=…` — and the Karoo's fallback QR.
void main() {
  testApp(
    'The Karoo fallback link and a refused Hammerhead return both land on the Hammerhead step',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      await backend.ensureHammerheadOffered();
      final rider = await backend.newUser('Device hammerhead rider');

      await openAppSignedIn($, rider);

      // What the Karoo's second QR holds: the step alone, nothing to authorize.
      await openLink($, Paths.deviceHammerhead());
      await modules.device.waitUntilHammerheadStep();
      expect(modules.device.showsHammerheadError, isFalse);

      // What the OAuth callback sends back after a refusal at Hammerhead: the same step, saying so.
      await openLink($, '${Paths.deviceVerifyKaroo()}?gps_error=access_denied');
      await modules.device.waitUntilHammerheadStep();
      expect(modules.device.showsHammerheadError, isTrue);
      expect(modules.device.showsPaired, isFalse);
    },
  );
}
