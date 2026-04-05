/// Type-safe path builders for navigation
/// Single source for constructing URLs throughout the app
/// Mirrors frontend/src/config/paths.ts
class Paths {
  Paths._();

  // Main tabs
  static String home() => '/home';
  static String teams() => '/teams';
  static String calendar() => '/calendar';
  static String profile() => '/profile';

  // Legal
  static String privacy() => '/privacy';
  static String terms() => '/terms';

  // Auth
  static String login() => '/login';
  static String register() => '/register';
  static String verifyEmail() => '/verify-email';
  static String magicLinkVerify() => '/magic-link/verify';
  static String forgotPassword() => '/forgot-password';
  static String resetPassword() => '/reset-password';

  // Teams
  static String teamsNew() => '/teams/new';
  static String team(String teamSlug) => '/teams/$teamSlug';
  static String teamCalendar(String teamSlug) => '/teams/$teamSlug/calendar';

  // Rides
  static String ride(String teamSlug, String rideSlug) =>
      '/teams/$teamSlug/rides/$rideSlug';

  // Routes
  static String routes(String teamSlug) => '/teams/$teamSlug/routes';
  static String route(String teamSlug, String routeSlug) =>
      '/teams/$teamSlug/routes/$routeSlug';
}
