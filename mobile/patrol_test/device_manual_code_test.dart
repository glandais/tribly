import 'common.dart';

/// Web counterpart: `flow-device.e2e.ts` › « an unknown code shows the error, « Réessayer »…
/// lowercase… » (audit P0 #8). The rider types the code shown on the device, in whatever case.
void main() {
  testApp(
    'An unknown code is refused, « Réessayer » clears it, and a lowercase code pairs the device',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final rider = await backend.newUser('Device typist');
      final flow = await backend.startDeviceFlow('garmin');
      final userCode = flow['userCode'] as String;

      await openAppSignedIn($, rider);
      await openLink($, Paths.deviceVerifyGarmin());

      // 0, 1, I and O are left out of the backend's alphabet: this code is never issued.
      await modules.device.enterCode('0i1o0i');
      await modules.device.waitUntilCodeIsRefused();
      await modules.device.tryAgain();

      await modules.device.enterCode(userCode.toLowerCase());
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
