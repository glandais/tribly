import 'package:dio/dio.dart';

import 'backend_client.dart';

/// The device pairing's server side that is not the device's own calls.
extension DeviceSeed on BackendClient {
  /// Hammerhead offered on `localhost`, as the platform admin configures it: what makes the Karoo
  /// page chain its Hammerhead step (docs/LEDGER_*.md API-63). Get-or-create, like the web suite's
  /// `ensureHammerheadOffered` — the two suites share the stack. The client id is a dummy: no test
  /// reaches Hammerhead.
  Future<void> ensureHammerheadOffered() async {
    final who = await admin();
    final domains = await get(who, '/api/admin/domains', query: {'size': 100});
    final domain = (domains['domains'] as List).cast<Json>().firstWhere(
      (d) => d['domain'] == 'localhost',
    );
    final path = '/api/admin/domains/${domain['id']}/gps-credentials';
    Future<bool> offered() async {
      final credentials = (await http.get<List<dynamic>>(
        path,
        options: Options(
          headers: {'Authorization': 'Bearer ${who.accessToken}'},
        ),
      )).data!.cast<Json>();
      return credentials.any(
        (c) => c['serviceType'] == 'HAMMERHEAD' && c['active'] == true,
      );
    }

    if (await offered()) return;
    try {
      await post(who, path, {
        'serviceType': 'HAMMERHEAD',
        'clientId': 'e2e-hammerhead',
        'clientSecret': 'e2e-hammerhead-secret',
        'active': true,
      });
    } on DioException {
      // Created in between by the other suite.
      if (!await offered()) rethrow;
    }
  }

  /// A device paired with [rider] without the page: the code asked for, authorized as the rider
  /// (the « Autoriser » the page sends) and exchanged for the device's tokens.
  Future<Json> pairDevice(TestUser rider, String clientId) async {
    final flow = await startDeviceFlow(clientId);
    await post(rider, '/api/device/oauth/complete', {
      'userCode': flow['userCode'],
      'confirmed': true,
    });
    final tokens = await pollDeviceToken(flow['deviceCode'] as String);
    if (tokens['refreshToken'] == null) {
      throw StateError('pairing of $clientId refused: $tokens');
    }
    return tokens;
  }

  /// The devices paired with [who]'s account, as the profile lists them (docs/LEDGER_*.md API-64).
  Future<List<Json>> pairedDevices(TestUser who) async =>
      (await http.get<List<dynamic>>(
        '/api/users/me/devices',
        options: Options(
          headers: {'Authorization': 'Bearer ${who.accessToken}'},
        ),
      )).data!.cast<Json>();

  /// The status of a device's refresh: 200 while its pairing lives.
  Future<int> deviceRefreshStatus(String refreshToken) async =>
      (await http.post<Json>(
        '/api/device/oauth/token',
        data: {'grantType': 'refresh_token', 'refreshToken': refreshToken},
        options: Options(validateStatus: (_) => true),
      )).statusCode!;
}
