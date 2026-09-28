import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/core/utils/provider_retry.dart';

DioException _status(int code) {
  final RequestOptions options = RequestOptions(path: '/api/x');
  return DioException(
    requestOptions: options,
    type: DioExceptionType.badResponse,
    response: Response<dynamic>(requestOptions: options, statusCode: code),
  );
}

DioException _type(DioExceptionType type) => DioException(
  requestOptions: RequestOptions(path: '/api/x'),
  type: type,
);

void main() {
  group('providerRetry', () {
    test('never retries a client error', () {
      for (final int code in <int>[400, 401, 403, 404, 409, 422, 429]) {
        expect(providerRetry(0, _status(code)), isNull, reason: '$code');
      }
    });

    test('retries server errors and 408', () {
      for (final int code in <int>[408, 500, 502, 503, 504]) {
        expect(providerRetry(0, _status(code)), isNotNull, reason: '$code');
      }
    });

    test('retries network failures and timeouts', () {
      for (final DioExceptionType type in <DioExceptionType>[
        DioExceptionType.connectionError,
        DioExceptionType.connectionTimeout,
        DioExceptionType.sendTimeout,
        DioExceptionType.receiveTimeout,
        DioExceptionType.transformTimeout,
        DioExceptionType.unknown,
      ]) {
        expect(providerRetry(0, _type(type)), isNotNull, reason: '$type');
      }
      expect(providerRetry(0, TimeoutException('slow')), isNotNull);
    });

    test('does not retry what cannot fix itself', () {
      expect(providerRetry(0, _type(DioExceptionType.cancel)), isNull);
      expect(providerRetry(0, _type(DioExceptionType.badCertificate)), isNull);
      expect(providerRetry(0, const FormatException('bad json')), isNull);
      expect(providerRetry(0, StateError('bug')), isNull);
    });

    test('backs off 0.5 s, 1 s, 2 s, then gives up', () {
      final DioException error = _status(503);
      expect(providerRetry(0, error), const Duration(milliseconds: 500));
      expect(providerRetry(1, error), const Duration(seconds: 1));
      expect(providerRetry(2, error), const Duration(seconds: 2));
      expect(providerRetry(3, error), isNull);
    });
  });

  group('startupRetry', () {
    test('same classification, Riverpod default schedule', () {
      expect(startupRetry(0, _status(403)), isNull);
      final DioException error = _type(DioExceptionType.connectionError);
      expect(startupRetry(0, error), const Duration(milliseconds: 200));
      expect(startupRetry(6, error), const Duration(milliseconds: 6400));
      expect(startupRetry(9, error), const Duration(milliseconds: 6400));
      expect(startupRetry(10, error), isNull);
    });
  });

  test('a 403 surfaces as an error at once, not as a retrying load', () async {
    int calls = 0;
    final FutureProvider<int> provider = FutureProvider<int>((Ref ref) async {
      calls++;
      throw _status(403);
    });
    final ProviderContainer container = ProviderContainer(retry: providerRetry);
    addTearDown(container.dispose);
    final ProviderSubscription<AsyncValue<int>> sub = container.listen(
      provider,
      (_, _) {},
    );
    addTearDown(sub.close);

    await expectLater(container.read(provider.future), throwsA(anything));
    final AsyncValue<int> state = container.read(provider);
    expect(state, isA<AsyncError<int>>());
    expect(state.isLoading, isFalse);
    expect(calls, 1);
  });
}
