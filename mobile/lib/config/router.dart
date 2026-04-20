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

/// Provider for the app router
final routerProvider = Provider<GoRouter>((ref) {
  final isAuthenticated = ref.watch(authProvider.select((s) => s.isAuthenticated));
  final isInitialized = ref.watch(authProvider.select((s) => s.isInitialized));
  final initialDeepLink = ref.read(initialDeepLinkProvider);

  return GoRouter(
    navigatorKey: _rootNavigatorKey,
    initialLocation: initialDeepLink ?? '/home',
    debugLogDiagnostics: true,
    redirect: (context, state) {

      // Wait for initialization
      if (!isInitialized) {
        return null;
      }

      final location = state.matchedLocation;

      // Auth-related routes that don't require login
      final isAuthRoute = location.startsWith('/login') ||
          location.startsWith('/verify-email') ||
          location.startsWith('/forgot-password') ||
          location.startsWith('/reset-password') ||
          location.startsWith('/privacy') ||
          location.startsWith('/terms');

      // If not authenticated and not on an auth route, redirect to login
      if (!isAuthenticated && !isAuthRoute) {
        return Paths.login();
      }

      // If authenticated and on login page, redirect to home
      if (isAuthenticated && location == '/login') {
        return '/home';
      }

      return null;
    },
    routes: [
      // Auth routes (outside shell)
      GoRoute(
        path: '/login',
        builder: (context, state) => const LoginPage(),
      ),
      GoRoute(
        path: '/register',
        builder: (context, state) => const LoginPage(initialRegister: true),
      ),
      GoRoute(
        path: '/verify-email',
        builder: (context, state) => VerifyEmailPage(
          token: state.uri.queryParameters['token'],
        ),
      ),
      GoRoute(
        path: '/forgot-password',
        builder: (context, state) => const ForgotPasswordPage(),
      ),
      GoRoute(
        path: '/reset-password',
        builder: (context, state) => ResetPasswordPage(
          token: state.uri.queryParameters['token'] ?? '',
        ),
      ),

      // Device verification routes
      GoRoute(
        path: '/garmin',
        builder: (context, state) => DeviceVerifyPage(
          code: state.uri.queryParameters['code'],
        ),
      ),
      GoRoute(
        path: '/karoo',
        builder: (context, state) => DeviceVerifyPage(
          code: state.uri.queryParameters['code'],
        ),
      ),

      // Main shell with bottom navigation
      ShellRoute(
        navigatorKey: _shellNavigatorKey,
        builder: (context, state, child) => MainShell(
          state: state,
          child: child,
        ),
        routes: [
          // Home tab
          GoRoute(
            path: '/home',
            pageBuilder: (context, state) => const NoTransitionPage(
              child: HomePage(),
            ),
          ),

          // Teams tab
          GoRoute(
            path: '/teams',
            pageBuilder: (context, state) => const NoTransitionPage(
              child: TeamsPage(),
            ),
          ),

          // Calendar tab
          GoRoute(
            path: '/calendar',
            pageBuilder: (context, state) => const NoTransitionPage(
              child: CalendarPage(),
            ),
          ),

          // Profile tab
          GoRoute(
            path: '/profile',
            pageBuilder: (context, state) => const NoTransitionPage(
              child: ProfilePage(),
            ),
          ),
        ],
      ),

      // Team shell with bottom navigation
      ShellRoute(
        navigatorKey: _teamShellNavigatorKey,
        builder: (context, state, child) => TeamShell(
          state: state,
          child: child,
        ),
        routes: [
          // Team root shows feed (default tab)
          GoRoute(
            path: '/teams/:teamSlug',
            pageBuilder: (context, state) {
              final teamSlug = state.pathParameters['teamSlug']!;
              return NoTransitionPage(
                child: _TeamTabPageWrapper(
                  teamSlug: teamSlug,
                  builder: (team) => TeamFeedPage(
                    teamSlug: teamSlug,
                    team: team,
                  ),
                ),
              );
            },
            routes: [
              // Calendar tab
              GoRoute(
                path: 'calendar',
                pageBuilder: (context, state) {
                  final teamSlug = state.pathParameters['teamSlug']!;
                  return NoTransitionPage(
                    child: _TeamTabPageWrapper(
                      teamSlug: teamSlug,
                      builder: (team) => _TeamCalendarTab(
                        teamSlug: teamSlug,
                        team: team,
                      ),
                    ),
                  );
                },
              ),
              // Routes tab
              GoRoute(
                path: 'routes',
                pageBuilder: (context, state) {
                  final teamSlug = state.pathParameters['teamSlug']!;
                  return NoTransitionPage(
                    child: _TeamTabPageWrapper(
                      teamSlug: teamSlug,
                      builder: (team) => _TeamRoutesTab(
                        teamSlug: teamSlug,
                        team: team,
                      ),
                    ),
                  );
                },
              ),
              // Ads tab
              GoRoute(
                path: 'classifieds',
                pageBuilder: (context, state) {
                  final teamSlug = state.pathParameters['teamSlug']!;
                  return NoTransitionPage(
                    child: _TeamTabPageWrapper(
                      teamSlug: teamSlug,
                      builder: (team) => AdsPage(
                        teamSlug: teamSlug,
                        team: team,
                      ),
                    ),
                  );
                },
              ),
              // About tab
              GoRoute(
                path: 'about',
                pageBuilder: (context, state) {
                  final teamSlug = state.pathParameters['teamSlug']!;
                  return NoTransitionPage(
                    child: _TeamTabPageWrapper(
                      teamSlug: teamSlug,
                      builder: (team) => TeamAboutPage(
                        teamSlug: teamSlug,
                        team: team,
                      ),
                    ),
                  );
                },
              ),

              // Detail pages (full screen, outside team shell)
              GoRoute(
                path: 'routes/:routeSlug',
                parentNavigatorKey: _rootNavigatorKey,
                builder: (context, state) => RouteDetailPage(
                  teamSlug: state.pathParameters['teamSlug']!,
                  routeSlug: state.pathParameters['routeSlug']!,
                ),
              ),
              GoRoute(
                path: 'rides/:rideSlug',
                parentNavigatorKey: _rootNavigatorKey,
                builder: (context, state) => RideDetailPage(
                  teamSlug: state.pathParameters['teamSlug']!,
                  rideSlug: state.pathParameters['rideSlug']!,
                ),
              ),
              GoRoute(
                path: 'posts/:postSlug',
                parentNavigatorKey: _rootNavigatorKey,
                builder: (context, state) => PostDetailPage(
                  teamSlug: state.pathParameters['teamSlug']!,
                  postSlug: state.pathParameters['postSlug']!,
                ),
              ),
              GoRoute(
                path: 'classifieds/:adSlug',
                parentNavigatorKey: _rootNavigatorKey,
                builder: (context, state) => AdDetailPage(
                  teamSlug: state.pathParameters['teamSlug']!,
                  adSlug: state.pathParameters['adSlug']!,
                ),
              ),
              GoRoute(
                path: 'trips/:tripSlug',
                parentNavigatorKey: _rootNavigatorKey,
                builder: (context, state) => TripDetailPage(
                  teamSlug: state.pathParameters['teamSlug']!,
                  tripSlug: state.pathParameters['tripSlug']!,
                ),
                routes: [
                  GoRoute(
                    path: 'stages/:stageSlug',
                    parentNavigatorKey: _rootNavigatorKey,
                    builder: (context, state) => StageDetailPage(
                      teamSlug: state.pathParameters['teamSlug']!,
                      tripSlug: state.pathParameters['tripSlug']!,
                      stageSlug: state.pathParameters['stageSlug']!,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ],
      ),

      // Legal pages (outside shell, accessible without auth)
      GoRoute(
        path: '/privacy',
        builder: (context, state) =>
            const LegalPage(type: LegalPageType.privacy),
      ),
      GoRoute(
        path: '/terms',
        builder: (context, state) =>
            const LegalPage(type: LegalPageType.terms),
      ),

      // Legacy root path redirect
      GoRoute(
        path: '/',
        redirect: (context, state) => '/home',
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
              onPressed: () => context.go('/home'),
              child: const Text('Retour à l\'accueil'),
            ),
          ],
        ),
      ),
    ),
  );
});

/// Wrapper that provides [TeamDetailDto] to team tab pages.
///
/// Watches [teamDetailProvider] and passes the team to the builder.
/// The shell already handles loading/error states, so by the time
/// tab pages render, the team data should be cached.
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

    return teamAsync.when(
      data: (team) => builder(team),
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (_, _) => const SizedBox.shrink(),
    );
  }
}

/// Wraps CalendarPage with TeamSliverAppBar when used as a team tab.
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

/// Wraps RoutesPage with a back-button AppBar when used as a team tab.
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
