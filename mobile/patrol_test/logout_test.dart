import 'common.dart';

void main() {
  testApp('A signed-in member logs out from their profile', (
    $,
    modules,
    apiClients,
  ) async {
    final user = await apiClients.backend.newUser('Logout');
    await openAppSignedIn($, user);
    await modules.navigation.goToProfile();
    await modules.profile.logOut();
    await modules.auth.waitUntilLoginPageIsVisible();
  });
}
