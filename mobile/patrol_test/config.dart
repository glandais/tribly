import 'package:pedalons/config/app_config.dart';

/// Where the e2e stack answers, passed by `e2e.sh` as dart-defines read from `../.env.e2e`.
///
/// The app itself reads `API_BASE_URL`, whose default is **production**. A suite run without it
/// would register accounts and send mail on www.pedalons.fr — so the suite refuses to start
/// unless the API is on this machine.
abstract final class E2eConfig {
  static void ensureLocalStack() => apiBaseUrl;

  static String get apiBaseUrl {
    final host = Uri.parse(AppConfig.apiBaseUrl).host;
    if (host != 'localhost' && host != '127.0.0.1') {
      throw StateError(
        'API_BASE_URL is ${AppConfig.apiBaseUrl}: the e2e suite only runs against a local '
        'stack. Start it with mobile/e2e.sh, not with a bare `patrol test`.',
      );
    }
    return AppConfig.apiBaseUrl;
  }

  static const String mailpitUrl = String.fromEnvironment(
    'E2E_MAILPIT_URL',
    defaultValue: 'http://localhost:18025',
  );

  /// The platform admin the stack bootstraps (`PEDALONS_BOOTSTRAP_ADMIN_EMAIL`).
  static const String adminEmail = String.fromEnvironment(
    'E2E_ADMIN_EMAIL',
    defaultValue: 'admin@e2e.test',
  );
}
