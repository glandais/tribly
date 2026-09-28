import 'api/account_seed.dart';
import 'common.dart';

/// Web counterpart: `flow-account.e2e.ts` › sign up, « sign-up needs the terms accepted: nothing
/// is sent ».
///
/// Every other test gets its accounts from the API: this one is the app's own sign-up form. What
/// follows the mailed link is `sign_up_verify_test`.
void main() {
  testApp(
    'A rider signs up in the app: the terms must be accepted, then the form goes back to the '
    'login with the address filled in and the verification mail leaves',
    ($, modules, apiClients) async {
      final backend = apiClients.backend;
      final mailpit = apiClients.mailpit;
      final email =
          'mobile-signup-${DateTime.now().microsecondsSinceEpoch}@e2e.test';
      final displayName = unique('Inscrite app');
      const password = 'e2e-signup-password';

      await openApp($);
      await modules.auth.waitUntilLoginPageIsVisible();
      await modules.auth.showRegisterForm();
      await modules.auth.fillRegisterForm(
        email: email,
        displayName: displayName,
        password: password,
      );

      // Terms unticked: refused on the form, and nothing leaves for that address.
      final seen = await mailpit.mailbox(email);
      await modules.auth.submitRegistration();
      await modules.auth.waitUntilTermsRequiredIsShown();
      expect(modules.auth.showsRegisterForm, isTrue);
      await Future<void>.delayed(const Duration(seconds: 3));
      expect((await mailpit.mailbox(email)).length, 0);

      // Ticked: the form goes back to the login, the address already in it.
      await modules.auth.acceptTerms();
      await modules.auth.submitRegistration();
      await modules.auth.waitUntilLoginPageIsVisible();
      expect(modules.auth.loginEmail, email);
      expect(
        mailpit.linkTokenIn(await mailpit.waitForNewMail(email, seen)),
        isNotNull,
      );
      expect(
        await backend.loginStatus(email, password),
        isNot(200),
        reason: 'no sign-in before the address is verified',
      );
    },
  );
}
