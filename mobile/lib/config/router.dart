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
import '../features/profile/presentation/pages/profile_page.dart';
import '../api/generated/export.dart';
import '../features/ads/presentation/pages/ad_detail_page.dart';
import '../features/ads/presentation/pages/ads_page.dart';
import '../features/posts/presentation/pages/post_detail_page.dart';
import '../features/rides/presentation/pages/ride_detail_page.dart';
import '../features/trips/presentation/pages/stage_detail_page.dart';
import '../features/trips/presentation/pages/trip_detail_page.dart';
import '../features/routes/presentation/pages/route_detail_page.dart';
import '../features/routes/presentation/pages/routes_page.dart';
import '../features/teams/presentation/pages/team_about_page.dart';
import '../features/teams/presentation/pages/team_detail_page.dart';
import '../features/teams/presentation/pages/team_feed_page.dart';
import '../features/teams/presentation/pages/teams_page.dart';
import '../features/teams/presentation/shell/team_shell.dart';
import 'paths.dart';

final _rootNavigatorKey = GlobalKey<NavigatorState>();
final _shellNavigatorKey = GlobalKey<NavigatorState>();
final _teamShellNavigatorKey = GlobalKey<NavigatorState>();

/// Provider for the initial deep link path (set in main.dart)
final initialDeepLinkProvider = Provider<String?>((ref) => null);

typedef _AncestorBuilder = String Function(
    Map<String, String> params, String locale);

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
String _teamAdsAncestor(Map<String, String> p, String locale) =>
    PathVariants.teamAds(p['teamSlug']!)[locale]!;
String _teamRoutesAncestor(Map<String, String> p, String locale) =>
    PathVariants.routes(p['teamSlug']!)[locale]!;

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

  // Team shell pages: the team bottom navigation only moves between team tabs.
  // The chain starts at the teams tab rather than home — pushing one main shell
  // tab over another merges them into a single shell match whose state stays on
  // the first one, which would light up the wrong tab. The teams tab still has
  // the bottom navigation to reach home.
  _DeepLinkHierarchy(
    patterns: PathVariants.team(':teamSlug'),
    ancestors: [_teamsAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.teamAbout(':teamSlug'),
    ancestors: [_teamsAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.teamCalendar(':teamSlug'),
    ancestors: [_teamsAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.routes(':teamSlug'),
    ancestors: [_teamsAncestor],
  ),
  _DeepLinkHierarchy(
    patterns: PathVariants.teamAds(':teamSlug'),
    ancestors: [_teamsAncestor],
  ),

  // Detail pages.
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
    if (location == p || location.startsWith('$p?') || location.startsWith('$p/')) {
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

String _underTeam(Map<String, String> variants, String locale, String teamBase) {
  final full = variants[locale]!;
  assert(
    full.startsWith('$teamBase/'),
    'Expected "$full" to start with "$teamBase/"',
  );
  return full.substring(teamBase.length + 1);
}

/// Build the team shell subtree for a single locale. Segments are derived from
/// [PathVariants] so no hand-maintained segment map is needed.
GoRoute _teamShellTree(String locale) {
  final teamBase = PathVariants.team(':teamSlug')[locale]!;

  return GoRoute(
    path: teamBase,
    pageBuilder: (context, state) {
      final teamSlug = state.pathParameters['teamSlug']!;
      return NoTransitionPage(
        child: _TeamTabPageWrapper(
          teamSlug: teamSlug,
          builder: (team) => TeamFeedPage(teamSlug: teamSlug, team: team),
        ),
      );
    },
    routes: [
      GoRoute(
        path: _underTeam(PathVariants.teamCalendar(':teamSlug'), locale, teamBase),
        pageBuilder: (context, state) {
          final teamSlug = state.pathParameters['teamSlug']!;
          return NoTransitionPage(
            child: _TeamTabPageWrapper(
              teamSlug: teamSlug,
              builder: (team) => _TeamCalendarTab(teamSlug: teamSlug, team: team),
            ),
          );
        },
      ),
      GoRoute(
        path: _underTeam(PathVariants.routes(':teamSlug'), locale, teamBase),
        pageBuilder: (context, state) {
          final teamSlug = state.pathParameters['teamSlug']!;
          return NoTransitionPage(
            child: _TeamTabPageWrapper(
              teamSlug: teamSlug,
              builder: (team) => _TeamRoutesTab(teamSlug: teamSlug, team: team),
            ),
          );
        },
      ),
      GoRoute(
        path: _underTeam(PathVariants.teamAds(':teamSlug'), locale, teamBase),
        pageBuilder: (context, state) {
          final teamSlug = state.pathParameters['teamSlug']!;
          return NoTransitionPage(
            child: _TeamTabPageWrapper(
              teamSlug: teamSlug,
              builder: (team) => AdsPage(teamSlug: teamSlug, team: team),
            ),
          );
        },
      ),
      GoRoute(
        path: _underTeam(PathVariants.teamAbout(':teamSlug'), locale, teamBase),
        pageBuilder: (context, state) {
          final teamSlug = state.pathParameters['teamSlug']!;
          return NoTransitionPage(
            child: _TeamTabPageWrapper(
              teamSlug: teamSlug,
              builder: (team) => TeamAboutPage(teamSlug: teamSlug, team: team),
            ),
          );
        },
      ),
      GoRoute(
        path: _underTeam(PathVariants.route(':teamSlug', ':routeSlug'), locale, teamBase),
        parentNavigatorKey: _rootNavigatorKey,
        builder: (context, state) => RouteDetailPage(
          teamSlug: state.pathParameters['teamSlug']!,
          routeSlug: state.pathParameters['routeSlug']!,
        ),
      ),
      GoRoute(
        path: _underTeam(PathVariants.ride(':teamSlug', ':rideSlug'), locale, teamBase),
        parentNavigatorKey: _rootNavigatorKey,
        builder: (context, state) => RideDetailPage(
          teamSlug: state.pathParameters['teamSlug']!,
          rideSlug: state.pathParameters['rideSlug']!,
        ),
      ),
      GoRoute(
        path: _underTeam(PathVariants.post(':teamSlug', ':postSlug'), locale, teamBase),
        parentNavigatorKey: _rootNavigatorKey,
        builder: (context, state) => PostDetailPage(
          teamSlug: state.pathParameters['teamSlug']!,
          postSlug: state.pathParameters['postSlug']!,
        ),
      ),
      GoRoute(
        path: _underTeam(PathVariants.ad(':teamSlug', ':adSlug'), locale, teamBase),
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
    path: _underTeam(PathVariants.trip(':teamSlug', ':tripSlug'), locale, teamBase),
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
final List<GoRoute> _teamShellTreesCache = _buildTeamShellTrees();

List<GoRoute> _buildTeamShellTrees() {
  final seenBases = <String>{};
  final trees = <GoRoute>[];
  final teamBases = PathVariants.team(':teamSlug');
  for (final locale in teamBases.keys) {
    if (seenBases.add(teamBases[locale]!)) {
      trees.add(_teamShellTree(locale));
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
      ..._perLocale(PathVariants.register(), (ctx, st) => const LoginPage(initialRegister: true)),
      ..._perLocale(
        PathVariants.verifyEmail(),
        (ctx, st) => VerifyEmailPage(token: st.uri.queryParameters['token']),
      ),
      ..._perLocale(PathVariants.forgotPassword(), (ctx, st) => const ForgotPasswordPage()),
      ..._perLocale(
        PathVariants.resetPassword(),
        (ctx, st) => ResetPasswordPage(token: st.uri.queryParameters['token'] ?? ''),
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

      // Main shell with bottom navigation
      ShellRoute(
        navigatorKey: _shellNavigatorKey,
        builder: (context, state, child) => MainShell(state: state, child: child),
        routes: [
          ..._perLocale(PathVariants.home(), (ctx, st) => const HomePage(), asPage: true),
          ..._perLocale(PathVariants.teams(), (ctx, st) => const TeamsPage(), asPage: true),
          ..._perLocale(PathVariants.calendar(), (ctx, st) => const CalendarPage(), asPage: true),
          ..._perLocale(PathVariants.profile(), (ctx, st) => const ProfilePage(), asPage: true),
        ],
      ),

      // Team shell with bottom navigation — one subtree per locale.
      ShellRoute(
        navigatorKey: _teamShellNavigatorKey,
        builder: (context, state, child) => TeamShell(state: state, child: child),
        routes: _teamShellTreesCache,
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
    errorBuilder: (context, state) => Scaffold(
      appBar: AppBar(title: const Text('Erreur')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error_outline, size: 64),
            const SizedBox(height: 16),
            Text('Page non trouvée: ${state.matchedLocation}'),
            const SizedBox(height: 24),
            FilledButton(
              onPressed: () => context.go(Paths.home()),
              child: const Text('Retour à l\'accueil'),
            ),
          ],
        ),
      ),
    ),
  );
});

/// Wraps every team tab, which all render a bare scrollable.
///
/// The [Scaffold] is what makes the iOS status bar tap scroll back to the top:
/// it looks up the [PrimaryScrollController] of its own route. [TeamShell]'s
/// Scaffold lives in the root navigator's route, while the tabs live in the
/// shell navigator's route — two different controllers — so each tab needs a
/// Scaffold on its own side of that boundary.
class _TeamTabPageWrapper extends ConsumerWidget {
  final String teamSlug;
  final Widget Function(TeamDetailDto team) builder;

  const _TeamTabPageWrapper({
    required this.teamSlug,
    required this.builder,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final teamAsync = ref.watch(teamDetailProvider(teamSlug));

    return Scaffold(
      body: teamAsync.when(
        data: (team) => builder(team),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (_, _) => const SizedBox.shrink(),
      ),
    );
  }
}

class _TeamCalendarTab extends StatelessWidget {
  final String teamSlug;
  final TeamDetailDto team;

  const _TeamCalendarTab({required this.teamSlug, required this.team});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        AppBar(
          title: Text(team.name),
          leading: IconButton(
            icon: const Icon(Icons.arrow_back),
            onPressed: () => context.go(Paths.teams()),
          ),
        ),
        Expanded(
          child: CalendarPage(teamSlug: teamSlug, embedded: true),
        ),
      ],
    );
  }
}

class _TeamRoutesTab extends StatelessWidget {
  final String teamSlug;
  final TeamDetailDto team;

  const _TeamRoutesTab({required this.teamSlug, required this.team});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        AppBar(
          title: Text(team.name),
          leading: IconButton(
            icon: const Icon(Icons.arrow_back),
            onPressed: () => context.go(Paths.teams()),
          ),
        ),
        Expanded(
          child: RoutesPage(teamSlug: teamSlug, embedded: true),
        ),
      ],
    );
  }
}
