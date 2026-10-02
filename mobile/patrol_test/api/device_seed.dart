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
}
