import 'package:flutter/material.dart';

/// Abstract navigation destination used by both NavigationBar and NavigationRail.
class AppDestination {
  final String path;
  final IconData icon;
  final IconData selectedIcon;
  final String label;

  const AppDestination({
    required this.path,
    required this.icon,
    required this.selectedIcon,
    required this.label,
  });
}

/// App-wide navigation destinations.
const kAppDestinations = [
  AppDestination(
    path: '/home',
    icon: Icons.home_outlined,
    selectedIcon: Icons.home,
    label: 'Accueil',
  ),
  AppDestination(
    path: '/teams',
    icon: Icons.group_outlined,
    selectedIcon: Icons.group,
    label: 'Équipes',
  ),
  AppDestination(
    path: '/calendar',
    icon: Icons.calendar_today_outlined,
    selectedIcon: Icons.calendar_today,
    label: 'Calendrier',
  ),
  AppDestination(
    path: '/profile',
    icon: Icons.person_outlined,
    selectedIcon: Icons.person,
    label: 'Profil',
  ),
];

/// Finds the destination index for a given location path.
int getDestinationIndex(String location) {
  for (int i = 0; i < kAppDestinations.length; i++) {
    if (location.startsWith(kAppDestinations[i].path)) {
      return i;
    }
  }
  return 0; // Default to home
}
