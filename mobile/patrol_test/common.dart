import 'package:flutter/widgets.dart';
import 'package:patrol/patrol.dart';
import 'package:pedalons/features/auth/data/secure_storage.dart';
import 'package:pedalons/main.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'api/api_clients.dart';
import 'api/backend_client.dart';
import 'config.dart';
import 'modules/modules.dart';

typedef TestAppCallback =
    Future<void> Function(
      PatrolIntegrationTester $,
      Modules modules,
      ApiClients apiClients,
    );

/// The wrapper every test goes through: one test per file, run by `patrol test`.
void testApp(String description, TestAppCallback callback) {
  patrolTest(description, ($) async {
    E2eConfig.ensureLocalStack();
    await callback($, Modules($), ApiClients());
  });
}

/// Mounts the app as a fresh install would find it: no session, no stored preference.
///
/// The keychain outlives the app on the simulator — and the app is not reinstalled between
/// tests — so a session left by the previous test would otherwise skip the login screen.
Future<void> openApp(PatrolIntegrationTester $) async {
  await _forgetPreviousTest();
  await _pumpApp($);
}

/// Mounts the app already signed in as [user], as if they had logged in on an earlier launch.
Future<void> openAppSignedIn(PatrolIntegrationTester $, TestUser user) async {
  await _forgetPreviousTest();
  await SecureTokenStorage().saveRefreshToken(user.refreshToken);
  await _pumpApp($);
}

Future<void> _forgetPreviousTest() async {
  await SecureTokenStorage().clearAll();
  await (await SharedPreferences.getInstance()).clear();
}

Future<void> _pumpApp(PatrolIntegrationTester $) async {
  final Widget app = await createApp(installErrorHandlers: false);
  // Not `pumpWidgetAndSettle`: a signed-in home keeps animating while it loads, so it never
  // settles. Each action waits for its own widget instead.
  await $.pumpWidget(app);
}
