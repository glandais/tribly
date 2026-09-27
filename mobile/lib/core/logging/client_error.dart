import 'package:dio/dio.dart';

import '../../api/generated/export.dart';
import 'app_log.dart';

/// Longueurs maximales imposées par `ClientErrorDto`.
const int kClientErrorTypeMax = 200;
const int kClientErrorMessageMax = 2000;
const int kClientErrorStackMax = 16000;

/// Traduit une exception en `ClientErrorDto`.
///
/// La pile part **telle que Dart l'imprime** (`#0  f (package:pedalons/…)`) :
/// c'est sur ces cadres que le serveur calcule l'empreinte qui regroupe les
/// occurrences d'une même erreur.
///
/// Une `DioException` est décrite par ce qu'on en sait sans rien divulguer —
/// méthode, chemin sans query, statut, code métier — et non par son
/// `toString`, qui peut citer l'URL complète.
ClientErrorDto clientErrorFrom(Object error, [StackTrace? stackTrace]) {
  final String stack = stackTrace?.toString() ?? '';
  return ClientErrorDto(
    type: truncate(error.runtimeType.toString(), kClientErrorTypeMax),
    message: truncate(
      sanitizeLogText(describeError(error)),
      kClientErrorMessageMax,
    ),
    stack: stack.trim().isEmpty ? null : truncate(stack, kClientErrorStackMax),
  );
}

/// Une ligne qui dit l'erreur, pour le journal comme pour le rapport.
String describeError(Object error) {
  if (error is DioException) return describeDioException(error);
  final String text = error.toString();
  return text.isEmpty ? error.runtimeType.toString() : text;
}

/// `GET /api/rides/x → 404 NOT_FOUND`, ou `GET /api/rides/x → connectionError`.
String describeDioException(DioException error) {
  final RequestOptions options = error.requestOptions;
  final int? status = error.response?.statusCode;
  final String? code = backendErrorCode(error);
  final String outcome = status == null
      ? error.type.name
      : <String>['$status', ?code].join(' ');
  return '${options.method} ${options.uri.path} → $outcome';
}

/// Le code métier d'une réponse d'erreur, s'il y en a un.
String? backendErrorCode(DioException error) {
  final Object? data = error.response?.data;
  if (data is Map && data['code'] is String) return data['code'] as String;
  return null;
}
