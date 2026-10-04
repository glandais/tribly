import 'api/profile_seed.dart';
import 'common.dart';

/// Web counterpart: `flow-account.e2e.ts` › the profile's timezone picker.
///
/// « Préférences » › « Fuseau horaire » writes `UserDto.timezone`, the field the web picker writes
/// (`PATCH /api/users/me/preferences`): a member who never chose one reads « Fuseau de
/// l'appareil », searches « auckland » in the sheet, picks `Pacific/Auckland`, and the server keeps
/// it. How the app then renders dates in it is `user_timezone_test`.
void main() {
  testApp(
    'The time zone chosen in « Préférences » reaches the server and is shown on its row',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final member = await backend.newUser('Timezone picker');

      await openAppSignedIn($, member);
      await modules.navigation.goToProfile();
      await modules.profile.waitUntilShown();
      await modules.profile.openPreferences();
      await modules.profileSettings.waitUntilTimezoneRowSays(
        'Fuseau de l\'appareil',
      );

      await modules.profileSettings.chooseTimezone(
        'Pacific/Auckland',
        search: 'auckland',
      );
      await modules.profileSettings.waitUntilTimezoneRowSays(
        'Pacific/Auckland',
      );

      final saved = await eventually(
        () => backend.profile(member),
        until: (me) => me['timezone'] == 'Pacific/Auckland',
        description: 'the time zone on the server',
        timeout: const Duration(seconds: 15),
      );
      expect(saved['timezone'], 'Pacific/Auckland');
    },
  );
}
