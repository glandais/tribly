import 'common.dart';

void main() {
  testApp('A member logs in with their password', (
    $,
    modules,
    apiClients,
  ) async {
    final user = await apiClients.backend.newUser('Password login');
    await openApp($);
    await modules.auth.logInWithPassword(user.email, user.password);
    await modules.navigation.waitUntilTabBarIsVisible();
  });
}
