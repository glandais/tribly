/// Type-safe path builders for navigation
/// Single source for constructing URLs throughout the app
/// Mirrors frontend/src/config/paths.ts
class Paths {
  Paths._();

  // Main tabs
  static String home() => '/home'; // applink OK (/)
  static String teams() => '/teams'; // applink OK
  static String calendar() => '/calendar'; // applink OK
  static String profile() => '/profile'; // applink OK

  // Legal
  static String privacy() => '/privacy'; // applink OK
  static String terms() => '/terms'; // applink OK

  // Auth
  static String login() => '/login'; // applink OK
  static String register() => '/register'; // applink OK
  static String verifyEmail() => '/verify-email'; // applink OK
  static String forgotPassword() => '/forgot-password'; // applink OK
  static String resetPassword() => '/reset-password'; // applink OK

  // Device verification
  static String deviceVerifyGarmin() => '/garmin'; // applink OK
  static String deviceVerifyKaroo() => '/karoo'; // applink OK

  // Teams
  static String team(String teamSlug) => '/teams/$teamSlug'; // applink OK
  static String teamFeed(String teamSlug) => '/teams/$teamSlug/feed';
  static String teamCalendar(String teamSlug) => '/teams/$teamSlug/calendar'; // applink OK
  static String teamAbout(String teamSlug) => '/teams/$teamSlug/about'; // applink OK
  static String teamAds(String teamSlug) => '/teams/$teamSlug/ads';

  // Rides
  static String ride(String teamSlug, String rideSlug) =>
      '/teams/$teamSlug/rides/$rideSlug'; // applink OK

  // Posts
  static String post(String teamSlug, String postSlug) =>
      '/teams/$teamSlug/posts/$postSlug'; // applink OK

  // Routes
  static String routes(String teamSlug) => '/teams/$teamSlug/routes'; // applink OK
  static String route(String teamSlug, String routeSlug) =>
      '/teams/$teamSlug/routes/$routeSlug'; // applink OK

  // Ads
  static String ad(String teamSlug, String adSlug) =>
      '/teams/$teamSlug/ads/$adSlug'; // applink OK

  // Trips
  static String trip(String teamSlug, String tripSlug) =>
      '/teams/$teamSlug/trips/$tripSlug'; // applink OK
  static String stage(String teamSlug, String tripSlug, String stageSlug) =>
      '/teams/$teamSlug/trips/$tripSlug/stages/$stageSlug'; // applink OK
}
