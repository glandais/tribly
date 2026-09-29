import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart' show find;
import 'package:patrol/patrol.dart';
import 'package:pedalons/api/pedalons_api_client.dart';
import 'package:pedalons/app.dart';

/// Answers the running app's GETs of one API resource with a 500, until [stop] — the Patrol
/// counterpart of the web suite's `page.route(…, fail500)` (`error-states.e2e.ts`).
///
/// The e2e stack has no way to fail on demand, and a 5xx that the backend really produces would
/// be a bug of its own, fixed some day. So the failure is made on the app's side of the wire: an
/// interceptor added, from the test, to the app's authenticated `Dio` (`dioProvider`), rejecting
/// the request before it leaves. Everything above the `Dio` — the repository, the provider and
/// its `providerRetry`, the page — runs as it does against a failing server. The app itself
/// carries no test hook.
final class ServerErrors {
  ServerErrors._(this._dio, this._interceptor);

  /// Starts failing the GETs whose path is exactly [apiPath] (`/api/teams/x/rides/y`).
  static ServerErrors on(PatrolIntegrationTester $, String apiPath) {
    final container = ProviderScope.containerOf(
      $.tester.element(find.byType(PedalonsApp)),
    );
    final dio = container.read(dioProvider);
    final interceptor = _Fail500(apiPath);
    dio.interceptors.add(interceptor);
    return ServerErrors._(dio, interceptor);
  }

  final Dio _dio;
  final _Fail500 _interceptor;

  /// How many requests were answered with a 500 so far.
  int get failed => _interceptor.failed;

  /// Lets the requests through again.
  void stop() => _dio.interceptors.remove(_interceptor);
}

final class _Fail500 extends Interceptor {
  _Fail500(this.apiPath);

  final String apiPath;
  int failed = 0;

  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    if (options.method != 'GET' || options.uri.path != apiPath) {
      handler.next(options);
      return;
    }
    failed++;
    handler.reject(
      DioException(
        requestOptions: options,
        type: DioExceptionType.badResponse,
        response: Response<Map<String, Object?>>(
          requestOptions: options,
          statusCode: 500,
          data: {'code': 'INTERNAL_ERROR', 'message': 'Internal error'},
        ),
      ),
    );
  }
}
