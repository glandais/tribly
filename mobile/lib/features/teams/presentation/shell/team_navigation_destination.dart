import 'package:flutter/material.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.generated.dart';
import '../../../../core/adaptive/navigation_destination.dart';

/// Builds the list of team navigation destinations based on team config
/// and the current user's membership.
List<AppDestination> buildTeamDestinations(TeamDetailDto team) {
  final isMember = team.role != null;
  final slug = team.slug;

  return [
    // Feed — always visible
    AppDestination(
      paths: PathVariants.team(slug),
      icon: Icons.dynamic_feed_outlined,
      selectedIcon: Icons.dynamic_feed,
      label: 'teams.tabs.feed',
    ),
    // Calendar — members only + rides or trips enabled
    if (isMember && (team.enableRides || team.enableTrips))
      AppDestination(
        paths: PathVariants.teamCalendar(slug),
        icon: Icons.calendar_today_outlined,
        selectedIcon: Icons.calendar_today,
        label: 'teams.tabs.calendar',
      ),
    // Routes — if enabled
    if (team.enableRoutes)
      AppDestination(
        paths: PathVariants.routes(slug),
        icon: Icons.route_outlined,
        selectedIcon: Icons.route,
        label: 'teams.tabs.routes',
      ),
    // Ads — members only + ads enabled
    if (isMember && team.enableAds)
      AppDestination(
        paths: PathVariants.teamAds(slug),
        icon: Icons.sell_outlined,
        selectedIcon: Icons.sell,
        label: 'teams.tabs.ads',
      ),
    // About — always visible
    AppDestination(
      paths: PathVariants.teamAbout(slug),
      icon: Icons.info_outlined,
      selectedIcon: Icons.info,
      label: 'teams.tabs.about',
    ),
  ];
}

/// Finds the destination index for a given location within team destinations.
///
/// Iterates from index 1 so the team feed (index 0, path `/teams/:slug`) acts
/// as the fallback — otherwise its short prefix would shadow every deeper tab.
/// Matches all locale variants so cross-locale deep links still land on the
/// right tab.
int getTeamDestinationIndex(
    String location, List<AppDestination> destinations) {
  for (int i = 1; i < destinations.length; i++) {
    for (final path in destinations[i].paths.values) {
      if (location == path || location.startsWith('$path/')) {
        return i;
      }
    }
  }
  return 0;
}
