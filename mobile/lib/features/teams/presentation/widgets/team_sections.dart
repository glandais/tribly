import 'package:flutter/material.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/locale_context.dart';
import '../../../../config/paths.generated.dart';

/// The sections a team can show. The router names one per route, which is what
/// lets the chip row highlight the right one without re-parsing the URL.
enum TeamSectionKind { feed, calendar, routes, ads, members, about }

/// One section of a team — Feed, Calendar, Routes, Ads, Members, About.
///
/// Sections used to be the destinations of a second `NavigationBar` stacked
/// under the app one (`TeamShell`). They are now **content**: a chip row
/// pinned under the team header, so the five global tabs stay visible inside a
/// team. Hence a model of its own rather than the app-level `AppDestination` —
/// nothing here drives a branch of the shell.
class TeamSection {
  final TeamSectionKind kind;

  /// URL variants per locale, straight from [PathVariants].
  final Map<String, String> paths;

  final IconData icon;

  /// Translation key.
  final String label;

  const TeamSection({
    required this.kind,
    required this.paths,
    required this.icon,
    required this.label,
  });

  /// URL in the current locale — use for `context.go(...)`.
  String get currentPath => paths[getCurrentLocale()] ?? paths.values.first;
}

/// The sections visible for [team], given its feature switches and the current
/// user's membership.
///
/// Order is the reading order of the chip row. Members and Ads are members-only;
/// Feed and About are always there, so the row is never empty.
List<TeamSection> buildTeamSections(TeamDetailDto team) {
  final isMember = team.role != null;
  final slug = team.slug;

  return [
    // Feed — always visible.
    TeamSection(
      kind: TeamSectionKind.feed,
      paths: PathVariants.team(slug),
      icon: Icons.dynamic_feed_outlined,
      label: 'teams.tabs.feed',
    ),
    // Calendar — members only, and only if rides or trips are enabled.
    if (isMember && (team.enableRides || team.enableTrips))
      TeamSection(
        kind: TeamSectionKind.calendar,
        paths: PathVariants.teamCalendar(slug),
        icon: Icons.calendar_today_outlined,
        label: 'teams.tabs.calendar',
      ),
    // Routes — if enabled.
    if (team.enableRoutes)
      TeamSection(
        kind: TeamSectionKind.routes,
        paths: PathVariants.routes(slug),
        icon: Icons.route_outlined,
        label: 'teams.tabs.routes',
      ),
    // Ads — members only, and only if classifieds are enabled.
    if (isMember && team.enableAds)
      TeamSection(
        kind: TeamSectionKind.ads,
        paths: PathVariants.teamAds(slug),
        icon: Icons.sell_outlined,
        label: 'teams.tabs.ads',
      ),
    // Members — members only: the list is not public.
    if (isMember)
      TeamSection(
        kind: TeamSectionKind.members,
        paths: PathVariants.teamMembers(slug),
        icon: Icons.people_outline,
        label: 'teams.tabs.members',
      ),
    // About — always visible.
    TeamSection(
      kind: TeamSectionKind.about,
      paths: PathVariants.teamAbout(slug),
      icon: Icons.info_outlined,
      label: 'teams.tabs.about',
    ),
  ];
}

/// Index of [kind] in [sections], or `-1` when that section is not visible for
/// this team — the members section reached by a link while not a member, say.
/// No chip is then highlighted, which is truer than highlighting the feed.
///
/// The active section follows the URL because the router names the section of
/// every team route: no second parsing of the location, and therefore no way
/// for the row and the page below it to disagree.
int indexOfSection(TeamSectionKind kind, List<TeamSection> sections) {
  for (int i = 0; i < sections.length; i++) {
    if (sections[i].kind == kind) return i;
  }
  return -1;
}
