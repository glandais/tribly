import 'api/device_seed.dart';
import 'common.dart';

/// Web counterpart: `flow-device.e2e.ts` › « the profile lists each paired device ».
///
/// « Appareils appairés » (`paired_devices_section.dart`, docs/LEDGER_*.md API-64) lists each Karoo
/// or Garmin paired with the account, and unpairs one at a time: its refresh fails from then on,
/// while the other device and this app stay signed in — unlike « Déconnecter tous les appareils ».
void main() {
  testApp(
    'the profile lists each paired device, and unpairing one ends its session only',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final user = await backend.newUser('Paired devices');
      final karoo = await backend.pairDevice(user, 'karoo');
      final garmin = await backend.pairDevice(user, 'garmin');
      final devices = await backend.pairedDevices(user);
      String idOf(String type) =>
          devices.firstWhere((d) => d['type'] == type)['id'] as String;

      await openAppSignedIn($, user);
      await modules.navigation.goToProfile();
      await modules.profile.waitUntilShown();
      await modules.profileSettings.waitUntilDeviceIsListed(idOf('KAROO'));
      await modules.profileSettings.waitUntilDeviceIsListed(idOf('GARMIN'));

      await modules.profileSettings.unpairDevice(idOf('GARMIN'));
      await modules.profileSettings.waitUntilDeviceIsGone(idOf('GARMIN'));

      expect(
        await backend.deviceRefreshStatus(garmin['refreshToken'] as String),
        isNot(200),
      );
      expect(
        await backend.deviceRefreshStatus(karoo['refreshToken'] as String),
        200,
      );
      expect((await backend.pairedDevices(user)).map((d) => d['type']), [
        'KAROO',
      ]);
      // This app's own session is not a device: still signed in.
      await modules.profileSettings.waitUntilDeviceIsListed(idOf('KAROO'));
    },
  );
}
