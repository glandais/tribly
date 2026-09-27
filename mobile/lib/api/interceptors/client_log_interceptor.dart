import 'package:dio/dio.dart';

import '../../core/logging/app_log.dart';
import '../../core/logging/client_error.dart';

/// Note au journal de l'app chaque requête échouée : méthode, chemin **sans
/// query string**, statut, code métier — `GET /api/rides/x → 404 NOT_FOUND`.
///
/// Jamais un corps ni un en-tête : le journal part tel quel dans un rapport.
///
/// Les échecs des endpoints de rapport (`/api/feedback…`) ne sont pas notés :
/// un rapport qui échoue ferait entrer son échec dans le rapport suivant, qui
/// échouerait à son tour pour la même raison.
///
/// Nommé pour ne pas se confondre avec le `LogInterceptor` de Dio, qui, lui,
/// imprime corps et en-têtes.
class ClientLogInterceptor extends Interceptor {
  ClientLogInterceptor([AppLog? log]) : _log = log;

  final AppLog? _log;

  AppLog get _target => _log ?? AppLog.instance;

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) {
    if (err.type != DioExceptionType.cancel &&
        !isFeedbackPath(err.requestOptions.uri.path)) {
      final int? status = err.response?.statusCode;
      _target.add(
        status != null && status < 500 ? AppLogLevel.warn : AppLogLevel.error,
        'http',
        describeDioException(err),
      );
    }
    handler.next(err);
  }
}

/// `/api/feedback` et `/api/feedback/errors`, quel que soit le préfixe de
/// l'URL de base.
bool isFeedbackPath(String path) =>
    path.endsWith('/api/feedback') || path.contains('/api/feedback/');
