import 'package:flutter/material.dart';

import '../../config/locale_context.dart';
import '../../config/paths.generated.dart';

/// Abstract navigation destination used by both NavigationBar and NavigationRail.
class AppDestination {
  final Map<String, String> paths;
  final IconData icon;
  final IconData selectedIcon;
  final String label;

  const AppDestination({
    required this.paths,
    required this.icon,
    required this.selectedIcon,
    required this.label,
  });

  /// URL in the current locale — use for `context.go(...)`.
  String get currentPath => paths[getCurrentLocale()] ?? paths.values.first;
}

/// App-wide navigation destinations. Index 0 (home, path `/`) is the fallback
/// destination and must be listed first — see [getDestinationIndex].
final List<AppDestination> kAppDestinations = [
  AppDestination(
    paths: PathVariants.home(),
    icon: Icons.home_outlined,
    selectedIcon: Icons.home,
    label: 'nav.home',
  ),
  AppDestination(
    paths: PathVariants.teams(),
    icon: Icons.group_outlined,
    selectedIcon: Icons.group,
    label: 'nav.teams',
  ),
  AppDestination(
    paths: PathVariants.calendar(),
    icon: Icons.calendar_today_outlined,
    selectedIcon: Icons.calendar_today,
    label: 'nav.calendar',
  ),
  AppDestination(
    paths: PathVariants.profile(),
    icon: Icons.person_outlined,
    selectedIcon: Icons.person,
    label: 'nav.profile',
  ),
];

/// Finds the destination index for a given location path.
///
/// Matches against every locale variant so that deep links in one language
/// highlight the right tab even when the user's current locale differs.
/// Home (`/`) is the fallback — skipped during matching since it would
/// prefix-match everything.
int getDestinationIndex(String location) {
  for (int i = 1; i < kAppDestinations.length; i++) {
    for (final path in kAppDestinations[i].paths.values) {
      if (location == path ||
          location.startsWith('$path/') ||
          location.startsWith('$path?')) {
        return i;
      }
    }
  }
  return 0;
}
