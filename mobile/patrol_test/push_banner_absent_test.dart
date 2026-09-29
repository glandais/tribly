import 'api/peripheral_seed.dart';
import 'common.dart';

/// No web counterpart: push is the app's.
///
/// « No control without effect »: the inbox offers to turn push on (`push_activation_banner.dart`)
/// only when `PUSH` is among the channels the server can deliver on, i.e. when it has an FCM
/// service account. The e2e stack has none (`PEDALONS_PUSH_ENABLED=false`): the inbox offers
/// nothing, and never asks the system for the permission.
void main() {
  testApp(
    'A server that cannot push: the inbox shows no push activation banner',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final user = await backend.newUser('Push banner');

      await openAppSignedIn($, user);
      await modules.peripheral.openInboxFromBell();

      expect(await backend.notificationChannels(user), isNot(contains('PUSH')));
      expect(
        await modules.peripheral.pushActivationBannerStaysHidden(),
        isTrue,
      );
    },
  );
}
