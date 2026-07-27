import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../features/auth/presentation/pages/forgot_password_page.dart';
import '../features/auth/presentation/pages/login_page.dart';
import '../features/auth/presentation/pages/reset_password_page.dart';
import '../features/auth/presentation/pages/verify_email_page.dart';
import '../features/auth/providers/auth_provider.dart';
import '../features/device/presentation/pages/device_verify_page.dart';
import '../features/calendar/presentation/pages/calendar_page.dart';
import '../features/home/presentation/pages/home_page.dart';
import '../features/legal/presentation/pages/legal_page.dart';
import '../features/navigation/presentation/shell/main_shell.dart';
import '../features/profile/presentation/pages/my_participations_page.dart';
import '../features/profile/presentation/pages/profile_page.dart';
import '../core/pdl/pdl.dart';
import '../features/ads/presentation/pages/ad_detail_page.dart';
import '../features/posts/domain/post_neighbours.dart';
import '../features/posts/presentation/pages/post_detail_page.dart';
import '../features/rides/presentation/pages/ride_detail_page.dart';
import '../features/trips/presentation/pages/stage_detail_page.dart';
import '../features/trips/presentation/pages/trip_detail_page.dart';
import '../features/routes/presentation/pages/all_routes_map_page.dart';
import '../features/routes/presentation/pages/all_routes_page.dart';
import '../features/routes/presentation/pages/route_detail_page.dart';
import '../features/teams/presentation/pages/team_custom_page.dart';
import '../features/teams/presentation/pages/team_home_page.dart';
import '../features/teams/presentation/pages/teams_discover_page.dart';
import '../features/teams/presentation/widgets/team_sections.dart';
import '../features/teams/presentation/pages/teams_page.dart';
import 'paths.dart';

final _rootNavigatorKey = GlobalKey<NavigatorState>();

/// One navigator per branch of the shell — that is what gives each tab its own
/// back stack. Order matches [kAppDestinations] and the branch list below.
final _homeNavigatorKey = GlobalKey<NavigatorState>(debugLabel: 'home');
final _teamsNavigatorKey = GlobalKey<NavigatorState>(debugLabel: 'teams');
final _calendarNavigatorKey = GlobalKey<NavigatorState>(debugLabel: 'calendar');
final _routesNavigatorKey = GlobalKey<NavigatorState>(debugLabel: 'routes');
final _profileNavigatorKey = GlobalKey<NavigatorState>(debugLabel: 'profile');

/// Provider for the initial deep link path (set in main.dart)
final initialDeepLinkProvider = Provider<String?>((ref) => null);

typedef _AncestorBuilder =
    String Function(Map<String, String> params, String locale);

/// Hierarchies for deep-linked pages. Each entry pairs a route's locale
/// variants with the list of ancestor URL builders (root → parent), used by
/// [ancestorsForDeepLink] to reconstruct a back stack on cold start.
class _DeepLinkHierarchy {
  final Map<String, String> patterns;
  final List<_AncestorBuilder> ancestors;
  const _DeepLinkHierarchy({required this.patterns, required this.ancestors});
}

String _homeAncestor(Map<String, String> p, String locale) =>
    PathVariants.home()[locale]!;
String _teamsAncestor(Map<String, String> p, String locale) =>
    PathVariants.teams()[locale]!;
String _teamAncestor(Map<String, String> p, String locale) =>
    PathVariants.team(p['teamSlug']!)[locale]!;
String _tripAncestor(Map<String, String> p, String locale) =>
    PathVariants.trip(p['teamSlug']!, p['tripSlug']!)[locale]!;
String _profileAncestor(Map<String, String> p, String locale) =>
    PathVariants.profile()[locale]!;
String _teamAdsAncestor(Map<String, String> p, String locale) =>
    PathVariants.teamAds(p['teamSlug']!)[locale]!;
String _teamRoutesAncestor(Map<String, String> p, String locale) =>
    PathVariants.routes(p['teamSlug']!)[locale]!;
String _allRoutesAncestor(Map<String, String> p, String locale) =>
    PathVariants.allRoutes()[locale]!;

final List<_DeepLinkHierarchy> _deepLinkHierarchies = [
  // Standalone pages outside any shell: without an ancestor they would be the
  // only entry in the stack, leaving no way back into the app.
  _DeepLinkHierarchy(
    patterns: PathVariants.privacy(),
    ancestors: [_homeAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.terms(),
    ancestors: [_homeAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.deviceVerifyGarmin(),
    ancestors: [_homeAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.deviceVerifyKaroo(),
    ancestors: [_homeAncestor],
  ),

  // La page des participations vit sous le profil : ouverte par un lien
  // froid, elle doit trouver le profil dessous.
  _DeepLinkHierarchy(
    patterns: PathVariants.myParticipations(),
    ancestors: [_profileAncestor],
  ),

  // Pages of the Routes branch. Its root carries the tab bar, the map does not.
  _DeepLinkHierarchy(
    patterns: PathVariants.allRoutesMap(),
    ancestors: [_allRoutesAncestor],
  ),

  // Team pages. The chain starts at the teams tab rather than home — pushing
  // one branch root over another merges them into a single shell match whose
  // state stays on the first one, which would light up the wrong tab. The teams
  // tab still has the bottom navigation to reach home.
  _DeepLinkHierarchy(
    patterns: PathVariants.teamsDiscover(),
    ancestors: [_teamsAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.team(':teamSlug'),
    ancestors: [_teamsAncestor],
  ),

  // Team sections. They used to stop at the teams list because `TeamShell`
  // provided the navigation between them; with the shell gone they are ordinary
  // pages of the Teams branch, so a cold start on one of them must find the
  // team itself underneath — otherwise going back would skip straight past the
  // team the section belongs to.
  _DeepLinkHierarchy(
    patterns: PathVariants.teamAbout(':teamSlug'),
    ancestors: [_teamsAncestor, _teamAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.teamCalendar(':teamSlug'),
    ancestors: [_teamsAncestor, _teamAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.routes(':teamSlug'),
    ancestors: [_teamsAncestor, _teamAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.teamAds(':teamSlug'),
    ancestors: [_teamsAncestor, _teamAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.teamMembers(':teamSlug'),
    ancestors: [_teamsAncestor, _teamAncestor],
  ),

  // Detail pages.
  _DeepLinkHierarchy(
    patterns: PathVariants.teamPage(':teamSlug', ':pageSlug'),
    ancestors: [_teamsAncestor, _teamAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.ride(':teamSlug', ':rideSlug'),
    ancestors: [_teamsAncestor, _teamAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.post(':teamSlug', ':postSlug'),
    ancestors: [_teamsAncestor, _teamAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.trip(':teamSlug', ':tripSlug'),
    ancestors: [_teamsAncestor, _teamAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.stage(':teamSlug', ':tripSlug', ':stageSlug'),
    ancestors: [_teamsAncestor, _teamAncestor, _tripAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.ad(':teamSlug', ':adSlug'),
    ancestors: [_teamsAncestor, _teamAncestor, _teamAdsAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.route(':teamSlug', ':routeSlug'),
    ancestors: [_teamsAncestor, _teamAncestor, _teamRoutesAncestor],
  ),
];

Map<String, String>? _matchPattern(String pattern, String actualPath) {
  final patternSegs = pattern.split('/');
  final actualSegs = actualPath.split('/');
  if (patternSegs.length != actualSegs.length) return null;
  final params = <String, String>{};
  for (int i = 0; i < patternSegs.length; i++) {
    if (patternSegs[i].startsWith(':')) {
      params[patternSegs[i].substring(1)] = actualSegs[i];
    } else if (patternSegs[i] != actualSegs[i]) {
      return null;
    }
  }
  return params;
}

/// Returns the ancestor URLs (root → immediate parent) for a deep-linked path,
/// in the same locale the path was authored in. Empty when the target already
/// carries its own navigation (home, main shell tabs, auth pages) — the deep
/// link handler then just [GoRouter.go]es to it.
List<String> ancestorsForDeepLink(String path) {
  final barePath = path.split('?').first;
  for (final hierarchy in _deepLinkHierarchies) {
    for (final entry in hierarchy.patterns.entries) {
      final params = _matchPattern(entry.value, barePath);
      if (params != null) {
        return hierarchy.ancestors.map((fn) => fn(params, entry.key)).toList();
      }
    }
  }
  return const [];
}

/// Paths considered "auth-adjacent" — visits to these skip the
/// `not authenticated → /login` redirect. Includes every locale variant.
final Set<String> _authAdjacentPaths = <String>{
  ...PathVariants.login().values,
  ...PathVariants.register().values,
  ...PathVariants.verifyEmail().values,
  ...PathVariants.forgotPassword().values,
  ...PathVariants.resetPassword().values,
  ...PathVariants.privacy().values,
  ...PathVariants.terms().values,
};

final Set<String> _loginPaths = PathVariants.login().values.toSet();

bool _isAuthAdjacent(String location) {
  for (final p in _authAdjacentPaths) {
    if (location == p ||
        location.startsWith('$p?') ||
        location.startsWith('$p/')) {
      return true;
    }
  }
  return false;
}

/// Register one [GoRoute] per unique locale variant of [variants].
/// [asPage] wraps the child in a [NoTransitionPage] (required for shell children
/// so tab switches don't animate).
List<GoRoute> _perLocale(
  Map<String, String> variants,
  Widget Function(BuildContext, GoRouterState) builder, {
  bool asPage = false,
}) {
  final seen = <String>{};
  final routes = <GoRoute>[];
  for (final path in variants.values) {
    if (!seen.add(path)) continue;
    routes.add(
      GoRoute(
        path: path,
        builder: asPage ? null : builder,
        pageBuilder: asPage
            ? (ctx, st) => NoTransitionPage(child: builder(ctx, st))
            : null,
      ),
    );
  }
  return routes;
}

String _underTeam(
  Map<String, String> variants,
  String locale,
  String teamBase,
) {
  final full = variants[locale]!;
  assert(
    full.startsWith('$teamBase/'),
    'Expected "$full" to start with "$teamBase/"',
  );
  return full.substring(teamBase.length + 1);
}

/// Build the team subtree for a single locale, grafted under the Teams branch.
/// Segments are derived from [PathVariants] so no hand-maintained segment map
/// is needed — which is why moving the tree from its own shell to a branch did
/// not change a single URL.
GoRoute _teamTree(String locale) {
  final teamBase = PathVariants.team(':teamSlug')[locale]!;

  /// Every section of a team is the **same page** with another section: one
  /// owner of the team's loading and error state, and a row of sections that
  /// is content rather than a second navigation bar.
  GoRoute section(Map<String, String> variants, TeamSectionKind kind) =>
      GoRoute(
        path: _underTeam(variants, locale, teamBase),
        pageBuilder: (context, state) => NoTransitionPage(
          child: TeamHomePage(
            teamSlug: state.pathParameters['teamSlug']!,
            section: kind,
          ),
        ),
      );

  return GoRoute(
    path: teamBase,
    pageBuilder: (context, state) => NoTransitionPage(
      child: TeamHomePage(
        teamSlug: state.pathParameters['teamSlug']!,
        section: TeamSectionKind.feed,
      ),
    ),
    routes: [
      section(PathVariants.teamCalendar(':teamSlug'), TeamSectionKind.calendar),
      section(PathVariants.routes(':teamSlug'), TeamSectionKind.routes),
      section(PathVariants.teamAds(':teamSlug'), TeamSectionKind.ads),
      section(PathVariants.teamAbout(':teamSlug'), TeamSectionKind.about),
      section(PathVariants.teamMembers(':teamSlug'), TeamSectionKind.members),
      GoRoute(
        path: _underTeam(
          PathVariants.teamPage(':teamSlug', ':pageSlug'),
          locale,
          teamBase,
        ),
        builder: (context, state) => TeamCustomPage(
          teamSlug: state.pathParameters['teamSlug']!,
          pageSlug: state.pathParameters['pageSlug']!,
        ),
      ),
      GoRoute(
        path: _underTeam(
          PathVariants.route(':teamSlug', ':routeSlug'),
          locale,
          teamBase,
        ),
        parentNavigatorKey: _rootNavigatorKey,
        builder: (context, state) => RouteDetailPage(
          teamSlug: state.pathParameters['teamSlug']!,
          routeSlug: state.pathParameters['routeSlug']!,
        ),
      ),
      GoRoute(
        path: _underTeam(
          PathVariants.ride(':teamSlug', ':rideSlug'),
          locale,
          teamBase,
        ),
        parentNavigatorKey: _rootNavigatorKey,
        builder: (context, state) => RideDetailPage(
          teamSlug: state.pathParameters['teamSlug']!,
          rideSlug: state.pathParameters['rideSlug']!,
          // Poussé par le carrousel de l'accueil, jamais par un deeplink : la
          // valeur voyage dans `extra`, hors de l'URL, pour qu'un lien partagé
          // n'inscrive personne à son insu.
          autoJoin: state.extra is RideDetailExtra
              ? (state.extra! as RideDetailExtra).autoJoin
              : false,
        ),
      ),
      GoRoute(
        path: _underTeam(
          PathVariants.post(':teamSlug', ':postSlug'),
          locale,
          teamBase,
        ),
        parentNavigatorKey: _rootNavigatorKey,
        builder: (context, state) => PostDetailPage(
          teamSlug: state.pathParameters['teamSlug']!,
          postSlug: state.pathParameters['postSlug']!,
          // Les voisins viennent du fil déjà chargé et voyagent dans `extra`,
          // hors de l'URL : ils n'appartiennent pas à l'identité de la
          // publication, et un lien partagé n'en a donc aucun.
          neighbours: state.extra is PostNeighbours
              ? state.extra! as PostNeighbours
              : null,
        ),
      ),
      GoRoute(
        path: _underTeam(
          PathVariants.ad(':teamSlug', ':adSlug'),
          locale,
          teamBase,
        ),
        parentNavigatorKey: _rootNavigatorKey,
        builder: (context, state) => AdDetailPage(
          teamSlug: state.pathParameters['teamSlug']!,
          adSlug: state.pathParameters['adSlug']!,
        ),
      ),
      _tripShellRoute(locale, teamBase),
    ],
  );
}

GoRoute _tripShellRoute(String locale, String teamBase) {
  final tripBase = PathVariants.trip(':teamSlug', ':tripSlug')[locale]!;
  return GoRoute(
    path: _underTeam(
      PathVariants.trip(':teamSlug', ':tripSlug'),
      locale,
      teamBase,
    ),
    parentNavigatorKey: _rootNavigatorKey,
    builder: (context, state) => TripDetailPage(
      teamSlug: state.pathParameters['teamSlug']!,
      tripSlug: state.pathParameters['tripSlug']!,
    ),
    routes: [
      GoRoute(
        path: _underTeam(
          PathVariants.stage(':teamSlug', ':tripSlug', ':stageSlug'),
          locale,
          tripBase,
        ),
        parentNavigatorKey: _rootNavigatorKey,
        builder: (context, state) => StageDetailPage(
          teamSlug: state.pathParameters['teamSlug']!,
          tripSlug: state.pathParameters['tripSlug']!,
          stageSlug: state.pathParameters['stageSlug']!,
        ),
      ),
    ],
  );
}

/// Built once at module load. The router provider can rebuild on auth changes;
/// caching avoids re-walking [PathVariants] and re-allocating the closures.
final List<GoRoute> _teamTreesCache = _buildTeamTrees();

List<GoRoute> _buildTeamTrees() {
  final seenBases = <String>{};
  final trees = <GoRoute>[];
  final teamBases = PathVariants.team(':teamSlug');
  for (final locale in teamBases.keys) {
    if (seenBases.add(teamBases[locale]!)) {
      trees.add(_teamTree(locale));
    }
  }
  return trees;
}

/// Provider for the app router.
///
/// The [GoRouter] is built **once**: watching auth state here would recreate it
/// on every auth change and restart the navigator from [GoRouter.initialLocation],
/// wiping the back stack the deep link handler builds at startup (see main.dart).
/// Auth changes only re-run [GoRouter.redirect], through [refreshListenable].
final routerProvider = Provider<GoRouter>((ref) {
  final authRefresh = ValueNotifier<int>(0);
  ref.listen(
    authProvider.select((s) => (s.isInitialized, s.isAuthenticated)),
    (_, _) => authRefresh.value++,
  );
  ref.onDispose(authRefresh.dispose);

  return GoRouter(
    navigatorKey: _rootNavigatorKey,
    // Deep links are always replayed by the handler in main.dart, which also
    // rebuilds the ancestor stack. Overriding the platform default keeps that
    // the single entry point, whatever the OS passes as initial route.
    initialLocation: Paths.home(),
    overridePlatformDefaultLocation: true,
    refreshListenable: authRefresh,
    debugLogDiagnostics: true,
    redirect: (context, state) {
      final auth = ref.read(authProvider);
      if (!auth.isInitialized) return null;

      final location = state.matchedLocation;

      if (!auth.isAuthenticated && !_isAuthAdjacent(location)) {
        return Paths.login();
      }

      if (auth.isAuthenticated && _loginPaths.contains(location)) {
        return Paths.home();
      }

      return null;
    },
    routes: [
      // Auth routes (outside shell)
      ..._perLocale(PathVariants.login(), (ctx, st) => const LoginPage()),
      ..._perLocale(
        PathVariants.register(),
        (ctx, st) => const LoginPage(initialRegister: true),
      ),
      ..._perLocale(
        PathVariants.verifyEmail(),
        (ctx, st) => VerifyEmailPage(token: st.uri.queryParameters['token']),
      ),
      ..._perLocale(
        PathVariants.forgotPassword(),
        (ctx, st) => const ForgotPasswordPage(),
      ),
      ..._perLocale(
        PathVariants.resetPassword(),
        (ctx, st) =>
            ResetPasswordPage(token: st.uri.queryParameters['token'] ?? ''),
      ),

      // Device verification routes (brand names, no fr variants)
      ..._perLocale(
        PathVariants.deviceVerifyGarmin(),
        (ctx, st) => DeviceVerifyPage(code: st.uri.queryParameters['code']),
      ),
      ..._perLocale(
        PathVariants.deviceVerifyKaroo(),
        (ctx, st) => DeviceVerifyPage(code: st.uri.queryParameters['code']),
      ),

      // The single navigation shell: five fixed tabs, one branch each, one
      // navigator per branch so every tab keeps its own back stack. A team is
      // content of the Teams branch, not a shell of its own — the tab bar never
      // disappears. Full-screen detail pages opt out through
      // `parentNavigatorKey: _rootNavigatorKey`.
      StatefulShellRoute.indexedStack(
        builder: (context, state, navigationShell) =>
            MainShell(state: state, navigationShell: navigationShell),
        branches: [
          // 0 · Home
          StatefulShellBranch(
            navigatorKey: _homeNavigatorKey,
            routes: _perLocale(
              PathVariants.home(),
              (ctx, st) => const HomePage(),
              asPage: true,
            ),
          ),
          // 1 · Teams, and the whole team tree under it — same URLs as before,
          // only the graft point changed. `/teams/discover` is declared before
          // `/teams/:teamSlug` so the literal segment wins over the parameter.
          StatefulShellBranch(
            navigatorKey: _teamsNavigatorKey,
            routes: [
              ..._perLocale(
                PathVariants.teams(),
                (ctx, st) => const TeamsPage(),
                asPage: true,
              ),
              ..._perLocale(
                PathVariants.teamsDiscover(),
                (ctx, st) => const TeamsDiscoverPage(),
              ),
              ..._teamTreesCache,
            ],
          ),
          // 2 · Calendar
          StatefulShellBranch(
            navigatorKey: _calendarNavigatorKey,
            routes: _perLocale(
              PathVariants.calendar(),
              (ctx, st) => const CalendarPage(),
              asPage: true,
            ),
          ),
          // 3 · Routes, across every team.
          StatefulShellBranch(
            navigatorKey: _routesNavigatorKey,
            routes: [
              ..._perLocale(
                PathVariants.allRoutes(),
                (ctx, st) => const AllRoutesPage(),
                asPage: true,
              ),
              ..._perLocale(
                PathVariants.allRoutesMap(),
                (ctx, st) => const AllRoutesMapPage(),
              ),
            ],
          ),
          // 4 · Profile
          StatefulShellBranch(
            navigatorKey: _profileNavigatorKey,
            routes: [
              ..._perLocale(
                PathVariants.profile(),
                (ctx, st) => const ProfilePage(),
                asPage: true,
              ),
              ..._perLocale(
                PathVariants.myParticipations(),
                (ctx, st) => MyParticipationsPage(
                  // L'onglet d'ouverture vient de la ligne touchée sur le
                  // profil : il voyage dans `extra`, hors de l'URL, parce
                  // qu'il n'identifie pas la page.
                  initialUpcoming: st.extra is bool ? st.extra! as bool : true,
                ),
              ),
            ],
          ),
        ],
      ),

      // Legal pages (outside shell, accessible without auth)
      ..._perLocale(
        PathVariants.privacy(),
        (ctx, st) => const LegalPage(type: LegalPageType.privacy),
      ),
      ..._perLocale(
        PathVariants.terms(),
        (ctx, st) => const LegalPage(type: LegalPageType.terms),
      ),
    ],
    // The only screen that used to carry hard-coded French. It is also a
    // screen a user reaches by accident, so it says what happened, quotes the
    // address, and offers the one way out.
    errorBuilder: (context, state) => Scaffold(
      appBar: PdlAppBar(title: 'errors.notFound.title'.tr()),
      body: Center(
        child: SingleChildScrollView(
          child: PdlEmptyState(
            variant: PdlEmptyVariant.notFound,
            title: 'errors.notFound.title'.tr(),
            message: 'errors.notFound.message'.tr(
              namedArgs: {'path': state.matchedLocation},
            ),
            actions: [
              PdlButton(
                label: 'errors.notFound.action'.tr(),
                onPressed: () => context.go(Paths.home()),
              ),
            ],
          ),
        ),
      ),
    ),
  );
});
