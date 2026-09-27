import 'module.dart';

final class Auth extends Module {
  Auth(super.$);

  Future<void> logInWithPassword(String email, String password) async {
    await $(keys.login.emailField).enterText(email);
    await $(keys.login.passwordField).enterText(password);
    await $(keys.login.submitButton).tap();
  }

  Future<void> waitUntilLoginPageIsVisible() async {
    await $(keys.login.submitButton).waitUntilVisible();
  }
}
