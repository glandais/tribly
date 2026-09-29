import 'common.dart';

/// Web counterpart: `routes-render.e2e.ts` › terms and privacy.
///
/// The sign-up form asks to accept the terms of use: « Lire les conditions » and
/// « Confidentialité », under the box, open them in the app (`legal_page.dart`, the markdown bundled
/// under `privacy/`), signed out.
void main() {
  testApp(
    'From the sign-up form, signed out: the terms of use and the privacy policy open and read',
    ($, modules, apiClients) async {
      await openApp($);
      await modules.auth.waitUntilLoginPageIsVisible();
      await modules.auth.showRegisterForm();

      await modules.peripheral.openPrivacyFromSignUp();
      await modules.peripheral.waitUntilLegalPageSays(
        'Politique de confidentialité',
      );
      await modules.peripheral.leaveLegalPage();

      await modules.peripheral.openTermsFromSignUp();
      await modules.peripheral.waitUntilLegalPageSays(
        "Conditions d'utilisation",
      );
      await modules.peripheral.leaveLegalPage();

      expect(modules.auth.showsRegisterForm, isTrue);
    },
  );
}
