import 'package:pedalons/core/adaptive/navigation_destination.dart';

import 'module.dart';

final class Navigation extends Module {
  Navigation(super.$);

  Future<void> goToProfile() async {
    await $(keys.navigation.tab(_destination('nav.profile'))).tap();
  }

  Future<void> waitUntilTabBarIsVisible() async {
    await $(keys.navigation.tab(kAppDestinations.first)).waitUntilVisible();
  }

  AppDestination _destination(String label) =>
      kAppDestinations.firstWhere((d) => d.label == label);
}
