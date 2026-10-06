import 'package:flutter/material.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/locale_context.dart';
import '../../../../config/paths.generated.dart';
import '../../../../core/theme/pdl_icons.dart';

/// The sections a team can show. The router names one per route, which is what
/// lets the chip row highlight the right one without re-parsing the URL.
enum TeamSectionKind { dashboard, feed, calendar, routes, ads, members, about }

/// Le paramètre de requête qui, sous l'adresse d'une équipe, demande le fil
/// plutôt que le tableau de bord.
///
/// Le tableau de bord est la section **par défaut d'un membre**, à l'adresse
/// même de l'équipe : c'est l'écran qu'ouvrent la carte de « Mes équipes » et
/// un lien partagé. Le fil n'a pas d'adresse à lui au contrat des routes
/// (`contracts/routes.yaml`) ; il garde donc celle de l'équipe, suivie de
/// `?tab=publications` — la même adresse que l'onglet « Publications » du site
/// (`TEAM_FEED_TAB`, `frontend/src/pages/team/teamHomeData.ts`), pour qu'un lien
/// web ouvert dans l'app par lien profond tombe sur le fil. Un visiteur, qui
/// n'a pas de tableau de bord, voit le fil sous les deux formes.
const String kTeamTabParam = 'tab';
const String kTeamTabFeed = 'publications';

/// La section à rendre sous l'adresse nue d'une équipe : le tableau de bord
/// pour un membre, le fil pour tout autre visiteur — et le fil pour tous quand
/// l'adresse le demande.
TeamSectionKind resolveTeamRootSection({
  required TeamSectionKind requested,
  required String? role,
}) {
  if (requested != TeamSectionKind.dashboard) return requested;
  return role == null ? TeamSectionKind.feed : TeamSectionKind.dashboard;
}

/// One section of a team — Dashboard, Feed, Calendar, Routes, Ads, Members,
/// About.
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
/// Order is the reading order of the chip row. Dashboard, Members and Ads are
/// members-only; Feed and About are always there, so the row is never empty.
///
/// For a member the dashboard takes the team's own address, and the feed moves
/// to `?tab=publications` — see [kTeamTabParam].
List<TeamSection> buildTeamSections(TeamDetailDto team) {
  final isMember = team.role != null;
  // Organisers and admins read the roster whatever the team decided; everyone
  // else needs the team to have opened it. Mirrors UserTeamAccessChecker: the
  // chip has to disappear when the server would answer 403, because an entry
  // that always leads to an error is worse than no entry at all.
  final canSeeMembers =
      isMember &&
      (team.role == 'ORGANIZER' ||
          team.role == 'ADMIN' ||
          team.enableMemberDirectory);
  final slug = team.slug;

  final Map<String, String> teamPaths = PathVariants.team(slug);

  return [
    // Dashboard — members only, at the team's own address.
    if (isMember)
      TeamSection(
        kind: TeamSectionKind.dashboard,
        paths: teamPaths,
        icon: PdlIcons.dashboard,
        label: 'teams.tabs.dashboard',
      ),
    // Feed — always visible. Behind `?tab=publications` once the dashboard holds the
    // team's address.
    TeamSection(
      kind: TeamSectionKind.feed,
      paths: isMember
          ? <String, String>{
              for (final MapEntry<String, String> e in teamPaths.entries)
                e.key: '${e.value}?$kTeamTabParam=$kTeamTabFeed',
            }
          : teamPaths,
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
    // Members — never public, and not even every member: see [canSeeMembers].
    if (canSeeMembers)
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
