import 'api/account_seed.dart';
import 'common.dart';

/// Web counterpart: `flow-account.e2e.ts` › « log out of every device ».
///
/// « Déconnecter tous les appareils » (`data_and_account_section.dart`) is the only way to take
/// back an account whose session lingers on a device one no longer has: every session goes, this
/// one included.
void main() {
  testApp(
    '« Déconnecter tous les appareils » signs this device out and ends the sessions of the others',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final user = await backend.newUser('Logout everywhere');
      // Another device's session: a second login.
      final elsewhere = await backend.login(user.email, user.password);
      final otherRefreshToken = elsewhere['refreshToken'] as String;

      await openAppSignedIn($, user);
      await modules.navigation.goToProfile();
      await modules.profile.waitUntilShown();
      await modules.profile.openSecurity();
      await modules.profileSettings.logOutEverywhere();
      await modules.auth.waitUntilLoginPageIsVisible();

      expect(await backend.refreshStatus(otherRefreshToken), isNot(200));
      expect(await backend.refreshStatus(user.refreshToken), isNot(200));
      // The password still works: signing out is not a lockout.
      expect(await backend.loginStatus(user.email, user.password), 200);
    },
  );
}
