import 'common.dart';

void main() {
  testApp('A fresh install opens on the login page', (
    $,
    modules,
    apiClients,
  ) async {
    await openApp($);
    await modules.auth.waitUntilLoginPageIsVisible();
  });
}
