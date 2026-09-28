import 'common.dart';

/// Web counterpart: `flow-device.e2e.ts` › « an anonymous rider scanning the Garmin code signs
/// in… » (audit P0 #8), where the web keeps `/garmin?code=` across the login.
void main() {
  testApp(
    'A signed-out rider opening the Garmin link signs in, then the device is paired',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final rider = await backend.newUser('Device late rider');
      final flow = await backend.startDeviceFlow('garmin');

      await openApp($);
      await openLink(
        $,
        '${Paths.deviceVerifyGarmin()}?code=${flow['userCode']}',
      );
      await modules.auth.waitUntilLoginPageIsVisible();
      await modules.auth.logInWithPassword(rider.email, rider.password);

      await modules.device.waitUntilPaired();
      final tokens = await backend.pollDeviceToken(
        flow['deviceCode'] as String,
      );
      expect(
        (await backend.me(tokens['accessToken'] as String))['id'],
        rider.id,
      );
    },
  );
}
