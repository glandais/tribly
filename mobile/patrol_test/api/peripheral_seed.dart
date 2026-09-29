import 'backend_client.dart';

/// Reads for the peripheral screens: the beta sign-ups, the notification channels.
extension PeripheralSeed on BackendClient {
  /// The addresses of the latest beta sign-ups, as the platform admin lists them (newest first).
  Future<List<String>> latestBetaSignups() async {
    final page = await get(
      await admin(),
      '/api/admin/beta-signups',
      query: {'page': 0, 'size': 100},
    );
    return [
      for (final signup in (page['signups'] as List).cast<Json>())
        signup['email'] as String,
    ];
  }

  /// The channels the server can deliver to [who] on — `PUSH` only once FCM is configured.
  Future<List<String>> notificationChannels(TestUser who) async {
    final preferences = await get(who, '/api/notifications/preferences');
    return (preferences['channels'] as List).cast<String>();
  }
}
