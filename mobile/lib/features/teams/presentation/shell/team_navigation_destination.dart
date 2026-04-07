import 'package:flutter/material.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/adaptive/navigation_destination.dart';

/// Builds the list of team navigation destinations based on team config
/// and the current user's membership.
List<AppDestination> buildTeamDestinations(TeamDetailDto team) {
  final isMember = team.role != null;
  final slug = team.slug;

  return [
    // Feed — always visible
    AppDestination(
      path: Paths.team(slug),
      icon: Icons.dynamic_feed_outlined,
      selectedIcon: Icons.dynamic_feed,
      label: 'teams.tabs.feed',
    ),
    // Calendar — members only + rides or trips enabled
    if (isMember && (team.enableRides || team.enableTrips))
      AppDestination(
        path: Paths.teamCalendar(slug),
        icon: Icons.calendar_today_outlined,
        selectedIcon: Icons.calendar_today,
        label: 'teams.tabs.calendar',
      ),
    // Routes — if enabled
    if (team.enableRoutes)
      AppDestination(
        path: Paths.routes(slug),
        icon: Icons.route_outlined,
        selectedIcon: Icons.route,
        label: 'teams.tabs.routes',
      ),
    // Ads — members only + ads enabled
    if (isMember && team.enableAds)
      AppDestination(
        path: Paths.teamAds(slug),
        icon: Icons.sell_outlined,
        selectedIcon: Icons.sell,
        label: 'teams.tabs.ads',
      ),
    // About — always visible
    AppDestination(
      path: Paths.teamAbout(slug),
      icon: Icons.info_outlined,
      selectedIcon: Icons.info,
      label: 'teams.tabs.about',
    ),
  ];
}

/// Finds the destination index for a given location within team destinations.
///
/// Checks more specific paths first (longer paths) to avoid prefix conflicts.
/// For example, `/teams/x/calendar` should match calendar, not feed (`/teams/x`).
int getTeamDestinationIndex(
    String location, List<AppDestination> destinations) {
  // Check non-feed destinations first (they have more specific paths)
  for (int i = 1; i < destinations.length; i++) {
    if (location == destinations[i].path ||
        location.startsWith('${destinations[i].path}/')) {
      return i;
    }
  }
  // Default to feed (index 0 = team root)
  return 0;
}
