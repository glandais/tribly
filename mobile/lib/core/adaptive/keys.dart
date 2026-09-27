import 'package:flutter/widgets.dart';

import 'navigation_destination.dart';

class _NavigationKey extends ValueKey<String> {
  const _NavigationKey(String value) : super('navigation_$value');
}

class NavigationKeys {
  ValueKey<String> tab(AppDestination destination) =>
      _NavigationKey('tab_${destination.label}');
}
