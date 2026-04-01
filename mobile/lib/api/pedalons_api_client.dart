import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/legacy.dart';

import '../config/app_config.dart';
import 'generated/export.dart';
import 'interceptors/auth_interceptor.dart';

/// Simple token holder to avoid circular dependency
/// This provider doesn't depend on any other provider that uses Dio
final accessTokenHolderProvider = StateProvider<String?>((ref) => null);

/// Provider for a basic Dio client without auth interceptor (for auth endpoints)
final baseDioProvider = Provider<Dio>((ref) {
  return Dio(
    BaseOptions(
      baseUrl: AppConfig.apiBaseUrl,
      connectTimeout: const Duration(seconds: 30),
      receiveTimeout: const Duration(seconds: 30),
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Accept-Language': 'fr',
      },
    ),
  );
});

/// Provider for the Dio HTTP client with auth interceptor (for protected endpoints)
final dioProvider = Provider<Dio>((ref) {
  final dio = Dio(
    BaseOptions(
      baseUrl: AppConfig.apiBaseUrl,
      connectTimeout: const Duration(seconds: 30),
      receiveTimeout: const Duration(seconds: 30),
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
    ),
  );

  // Add auth interceptor that reads token from the simple holder
  dio.interceptors.add(AuthInterceptor(ref, dio));

  return dio;
});

// ============================================================================
// Generated API Client Providers
// ============================================================================

/// Provider for the generated PedalonsApiClient (unauthenticated, for login/register)
final baseApiClientProvider = Provider<PedalonsApiClient>((ref) {
  final dio = ref.watch(baseDioProvider);
  return PedalonsApiClient(dio, baseUrl: AppConfig.apiBaseUrl);
});

/// Provider for the generated PedalonsApiClient (authenticated, for protected endpoints)
final apiClientProvider = Provider<PedalonsApiClient>((ref) {
  final dio = ref.watch(dioProvider);
  return PedalonsApiClient(dio, baseUrl: AppConfig.apiBaseUrl);
});

// ============================================================================
// Individual Client Providers (Unauthenticated)
// ============================================================================

final authenticationClientProvider = Provider<AuthenticationClient>((ref) {
  return ref.watch(baseApiClientProvider).authentication;
});

// ============================================================================
// Individual Client Providers (Authenticated)
// ============================================================================

final passkeysClientProvider = Provider<PasskeysClient>((ref) {
  return ref.watch(apiClientProvider).passkeys;
});

final usersClientProvider = Provider<UsersClient>((ref) {
  return ref.watch(apiClientProvider).users;
});

final teamsClientProvider = Provider<TeamsClient>((ref) {
  return ref.watch(apiClientProvider).teams;
});

final teamMembersClientProvider = Provider<TeamMembersClient>((ref) {
  return ref.watch(apiClientProvider).teamMembers;
});

final ridesClientProvider = Provider<RidesClient>((ref) {
  return ref.watch(apiClientProvider).rides;
});

final routesClientProvider = Provider<RoutesClient>((ref) {
  return ref.watch(apiClientProvider).routes;
});

final calendarClientProvider = Provider<CalendarClient>((ref) {
  return ref.watch(apiClientProvider).calendar;
});

final postsClientProvider = Provider<PostsClient>((ref) {
  return ref.watch(apiClientProvider).posts;
});

final tripsClientProvider = Provider<TripsClient>((ref) {
  return ref.watch(apiClientProvider).trips;
});

final assetsClientProvider = Provider<AssetsClient>((ref) {
  return ref.watch(apiClientProvider).assets;
});

final configurationClientProvider = Provider<ConfigurationClient>((ref) {
  return ref.watch(apiClientProvider).configuration;
});

final publicationsClientProvider = Provider<PublicationsClient>((ref) {
  return ref.watch(apiClientProvider).publications;
});

final gpsServicesClientProvider = Provider<GpsServicesClient>((ref) {
  return ref.watch(apiClientProvider).gpsServices;
});
