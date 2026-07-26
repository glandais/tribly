import 'dart:convert';
import 'dart:io';

import 'package:dio/dio.dart';
import 'package:easy_localization/easy_localization.dart';
// easy_localization does not export its loader from the barrel; the test needs
// it to install the real translation maps without booting the whole app.
// ignore: implementation_imports
import 'package:easy_localization/src/localization.dart';
// ignore: implementation_imports
import 'package:easy_localization/src/translations.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/core/utils/api_error_handler.dart';

/// Loads the real translation file so the test asserts the shipped copy,
/// not a stub.
void _loadLocale(String languageCode) {
  final raw = File('assets/l10n/$languageCode.json').readAsStringSync();
  final map = json.decode(raw) as Map<String, dynamic>;
  Localization.load(
    Locale(languageCode),
    translations: Translations(map),
    fallbackTranslations: Translations(map),
  );
}

DioException _dio(DioExceptionType type, {Response<dynamic>? response}) =>
    DioException(
      requestOptions: RequestOptions(path: '/api/test'),
      type: type,
      response: response,
    );

Response<dynamic> _errorResponse(Object? data) => Response<dynamic>(
  requestOptions: RequestOptions(path: '/api/test'),
  statusCode: 400,
  data: data,
);

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('resolveApiError', () {
    const offlineTypes = [
      DioExceptionType.connectionError,
      DioExceptionType.connectionTimeout,
      DioExceptionType.sendTimeout,
      DioExceptionType.receiveTimeout,
    ];

    test('the four connection/timeout types yield the offline kind (fr)', () {
      _loadLocale('fr');
      for (final type in offlineTypes) {
        final resolved = resolveApiError(_dio(type));
        expect(resolved.kind, ApiErrorKind.offline, reason: '$type');
        expect(resolved.isOffline, isTrue, reason: '$type');
        expect(resolved.title, 'Pas de connexion', reason: '$type');
        expect(
          resolved.message,
          'Vérifiez votre connexion internet, puis réessayez.',
          reason: '$type',
        );
        expect(resolved.message, isNot('common.error'.tr()), reason: '$type');
      }
    });

    test('the offline copy is localized in english too', () {
      _loadLocale('en');
      final resolved = resolveApiError(_dio(DioExceptionType.connectionError));
      expect(resolved.title, 'No connection');
      expect(resolved.message, 'Check your internet connection and try again.');
    });

    test('a business code maps to its translation', () {
      _loadLocale('fr');
      final resolved = resolveApiError(
        _dio(
          DioExceptionType.badResponse,
          response: _errorResponse(<String, dynamic>{'code': 'GROUP_FULL'}),
        ),
      );
      expect(resolved.kind, ApiErrorKind.api);
      expect(resolved.code, 'GROUP_FULL');
      expect(resolved.message, 'Ce groupe est complet');
    });

    test('an unknown business code falls back but keeps the code', () {
      _loadLocale('fr');
      final resolved = resolveApiError(
        _dio(
          DioExceptionType.badResponse,
          response: _errorResponse(<String, dynamic>{'code': 'NOPE_UNKNOWN'}),
        ),
      );
      expect(resolved.kind, ApiErrorKind.unknown);
      expect(resolved.code, 'NOPE_UNKNOWN');
      expect(resolved.message, 'Erreur');
    });

    test('a response without a code is unknown, not offline', () {
      _loadLocale('fr');
      final resolved = resolveApiError(
        _dio(DioExceptionType.badResponse, response: _errorResponse('boom')),
      );
      expect(resolved.kind, ApiErrorKind.unknown);
      expect(resolved.code, isNull);
      expect(resolved.message, 'Erreur');
    });

    test('a non-DioException is surfaced as unknown, not as offline', () {
      _loadLocale('fr');
      // Mimics a Freezed deserialization failure (the residual suspect behind
      // the bare "Erreur" on ride registration).
      final resolved = resolveApiError(
        TypeError(),
        StackTrace.fromString('fake stack'),
      );
      expect(resolved.kind, ApiErrorKind.unknown);
      expect(resolved.isOffline, isFalse);
      expect(resolved.code, isNull);
      expect(resolved.message, 'Erreur');
    });

    test('getErrorMessage stays a plain string for existing callers', () {
      _loadLocale('fr');
      expect(
        getErrorMessage(_dio(DioExceptionType.receiveTimeout)),
        'Vérifiez votre connexion internet, puis réessayez.',
      );
      expect(getErrorMessage(Exception('nope')), 'Erreur');
    });
  });
}
