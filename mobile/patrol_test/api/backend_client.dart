import 'package:dio/dio.dart';

import '../config.dart';
import 'mailpit_client.dart';

/// An account created for a test, verified, with its first session.
final class TestUser {
  const TestUser({
    required this.email,
    required this.password,
    required this.displayName,
    required this.refreshToken,
  });

  final String email;
  final String password;
  final String displayName;
  final String refreshToken;
}

/// Seeds data through the REST API, the same endpoints the app uses — the backend has no
/// test-only backdoor. Each test creates what it needs under a unique name: the e2e database is
/// shared with the web suite and never reset between runs.
final class BackendClient {
  BackendClient(this._mailpit);

  final MailpitClient _mailpit;
  final Dio _dio = Dio(BaseOptions(baseUrl: E2eConfig.apiBaseUrl));

  static const String _password = 'e2e-password';

  /// Signs up as the app does — register, then follow the verification link from the mail.
  Future<TestUser> newUser(String label) async {
    final email = '${_uniqueTag(label)}@e2e.test';
    final seen = await _mailpit.mailbox(email);
    await _dio.post<void>(
      '/api/auth/register',
      data: {
        'email': email,
        'displayName': label,
        'password': _password,
        'acceptTerms': true,
      },
    );
    final token = _mailpit.linkTokenIn(
      await _mailpit.waitForNewMail(email, seen),
    );
    final response = await _dio.post<Map<String, dynamic>>(
      '/api/auth/verify-email',
      data: {'token': token},
    );
    return TestUser(
      email: email,
      password: _password,
      displayName: label,
      refreshToken: response.data!['refreshToken'] as String,
    );
  }

  String _uniqueTag(String label) {
    final slug = label.toLowerCase().replaceAll(RegExp('[^a-z0-9]+'), '-');
    return 'mobile-$slug-${DateTime.now().microsecondsSinceEpoch}';
  }
}
