import 'api/device_seed.dart';
import 'common.dart';

/// Web counterpart: `flow-device.e2e.ts` › « a signed-in rider opening the Karoo link is asked
/// first… » (audit P0 #8). The device shows a QR code of `/karoo?code=…`; the phone opens it in the
/// app, and the rider's « Autoriser » pairs the device (SEC-2: the link alone pairs nothing); the
/// page then goes straight on to Hammerhead, which the account lacks (docs/LEDGER_*.md API-63).
void main() {
  testApp(
    'A signed-in rider opening the Karoo link pairs the device, then is asked for Hammerhead',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      await backend.ensureHammerheadOffered();
      final rider = await backend.newUser('Device rider');
      final flow = await backend.startDeviceFlow('karoo');
      expect(
        (await backend.pollDeviceToken(flow['deviceCode'] as String))['code'],
        'AUTHORIZATION_PENDING',
      );

      await openAppSignedIn($, rider);
      await openLink(
        $,
        '${Paths.deviceVerifyKaroo()}?code=${flow['userCode']}',
      );
      await modules.device.authorize();
      await modules.device.waitUntilHammerheadStep();
      expect(modules.device.showsPaired, isFalse);

      final tokens = await backend.pollDeviceToken(
        flow['deviceCode'] as String,
      );
      expect(tokens['accessToken'], isNotNull);
      expect(
        (await backend.me(tokens['accessToken'] as String))['id'],
        rider.id,
      );
    },
  );
}
