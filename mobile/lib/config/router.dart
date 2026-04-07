import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../features/auth/presentation/pages/forgot_password_page.dart';
import '../features/auth/presentation/pages/login_page.dart';
import '../features/auth/presentation/pages/reset_password_page.dart';
import '../features/auth/presentation/pages/verify_email_page.dart';
import '../features/auth/providers/auth_provider.dart';
import '../features/calendar/presentation/pages/calendar_page.dart';
import '../features/home/presentation/pages/home_page.dart';
import '../features/legal/presentation/pages/legal_page.dart';
import '../features/navigation/presentation/shell/main_shell.dart';
import '../features/profile/presentation/pages/profile_page.dart';
import '../features/rides/presentation/pages/ride_detail_page.dart';
import '../features/routes/presentation/pages/route_detail_page.dart';
import '../features/routes/presentation/pages/routes_page.dart';
import '../features/teams/presentation/pages/team_detail_page.dart';
import '../features/teams/presentation/pages/teams_page.dart';
import 'paths.dart';

final _rootNavigatorKey = GlobalKey<NavigatorState>();
final _shellNavigatorKey = GlobalKey<NavigatorState>();

/// Provider for the app router
final routerProvider = Provider<GoRouter>((ref) {
  final isAuthenticated = ref.watch(authProvider.select((s) => s.isAuthenticated));
  final isInitialized = ref.watch(authProvider.select((s) => s.isInitialized));

  return GoRouter(
    navigatorKey: _rootNavigatorKey,
    initialLocation: '/home',
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
          email: state.uri.queryParameters['email'] ?? '',
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

      // Detail pages (outside shell for full screen)
      GoRoute(
        path: '/teams/:teamSlug',
        builder: (context, state) => TeamDetailPage(
          teamSlug: state.pathParameters['teamSlug']!,
        ),
        routes: [
          // Team's routes list
          GoRoute(
            path: 'routes',
            builder: (context, state) => RoutesPage(
              teamSlug: state.pathParameters['teamSlug']!,
            ),
            routes: [
              // Route detail
              GoRoute(
                path: ':routeSlug',
                builder: (context, state) => RouteDetailPage(
                  teamSlug: state.pathParameters['teamSlug']!,
                  routeSlug: state.pathParameters['routeSlug']!,
                ),
              ),
            ],
          ),
          // Ride detail
          GoRoute(
            path: 'rides/:rideSlug',
            builder: (context, state) => RideDetailPage(
              teamSlug: state.pathParameters['teamSlug']!,
              rideSlug: state.pathParameters['rideSlug']!,
            ),
          ),
          // Team calendar
          GoRoute(
            path: 'calendar',
            builder: (context, state) => CalendarPage(
              teamSlug: state.pathParameters['teamSlug']!,
            ),
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
