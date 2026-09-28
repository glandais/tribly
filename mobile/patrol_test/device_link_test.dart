import 'common.dart';

/// Web counterpart: `flow-device.e2e.ts` › « a signed-in rider opening the Karoo link authorizes
/// it… » (audit P0 #8). The device shows a QR code of `/karoo?code=…`; the phone opens it in the
/// app, which pairs the device for the signed-in rider.
void main() {
  testApp(
    'A signed-in rider opening the Karoo link pairs the device with their account',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
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
      await modules.device.waitUntilPaired();

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
