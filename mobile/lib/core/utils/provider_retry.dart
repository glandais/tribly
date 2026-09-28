import 'dart:async';
import 'dart:math' as math;

import 'package:dio/dio.dart';
import 'package:flutter_riverpod/misc.dart' show ProviderException;

/// La politique de nouvel essai des providers de l'app, posée sur le
/// `ProviderScope` de `main.dart`.
///
/// Sans elle, Riverpod 3 applique `ProviderContainer.defaultRetry` : **toute**
/// erreur est rejouée dix fois, de 200 ms en doublant jusqu'à 6,4 s, et le
/// provider reste `AsyncLoading` pendant ce temps. Un 403 sur une sortie
/// réservée aux membres laissait ainsi le squelette à l'écran ~38 s avant
/// l'erreur.
///
/// Seul ce qui peut passer tout seul est rejoué ([isTransientError]) : réseau
/// coupé, délai dépassé, 5xx. Un 4xx est une réponse, pas une panne — le
/// rejouer ne change rien, l'erreur s'affiche tout de suite. Le 401 ne fait
/// pas exception : `AuthInterceptor` a déjà rafraîchi le jeton et rejoué la
/// requête avant que le provider ne voie l'erreur ; s'il remonte, la session
/// est perdue et le routeur renvoie vers la connexion.
///
/// Trois essais seulement (0,5 s, 1 s, 2 s — 3,5 s en tout) : de quoi passer
/// un changement de réseau ou le redémarrage d'un serveur, pas davantage.
/// Chaque écran en erreur porte son bouton « Réessayer », et l'erreur hors
/// ligne a sa propre copie : la cacher derrière un squelette de 38 s serait
/// pire que de la montrer.
Duration? providerRetry(int retryCount, Object error) => _retry(
  retryCount,
  error,
  maxRetries: 3,
  minDelay: const Duration(milliseconds: 500),
  maxDelay: const Duration(seconds: 2),
);

/// La politique d'une donnée de démarrage qu'aucun écran n'affiche en erreur
/// (`appConfigProvider`) : même tri que [providerRetry], mais le calendrier
/// par défaut de Riverpod (dix essais, jusqu'à 6,4 s d'écart, ~38 s en tout),
/// puisque personne n'est là pour appuyer sur « Réessayer ».
Duration? startupRetry(int retryCount, Object error) => _retry(
  retryCount,
  error,
  maxRetries: 10,
  minDelay: const Duration(milliseconds: 200),
  maxDelay: const Duration(milliseconds: 6400),
);

Duration? _retry(
  int retryCount,
  Object error, {
  required int maxRetries,
  required Duration minDelay,
  required Duration maxDelay,
}) {
  if (retryCount >= maxRetries) return null;
  if (!isTransientError(error)) return null;
  final Duration delay = minDelay * math.pow(2, retryCount).toInt();
  return delay > maxDelay ? maxDelay : delay;
}

/// Vrai si l'erreur a une chance de disparaître d'elle-même.
///
/// - un [DioException] sans réponse (connexion, délais, erreur de socket
///   rangée en `unknown`) ou avec une réponse 5xx, 408 compris ;
/// - un [TimeoutException] levé par un `.timeout()` côté client.
///
/// Tout le reste est définitif : les 4xx (dont 429 — le serveur demande
/// justement de ralentir), une requête annulée, un certificat refusé, une
/// [Error] (bogue, désérialisation), et une [ProviderException] — c'est le
/// provider dont on dépend qui échoue, il se rejoue lui-même et entraîne ses
/// dépendants avec lui, comme le fait déjà `defaultRetry`.
bool isTransientError(Object error) {
  if (error is ProviderException || error is Error) return false;
  if (error is TimeoutException) return true;
  if (error is! DioException) return false;
  switch (error.type) {
    case DioExceptionType.connectionError:
    case DioExceptionType.connectionTimeout:
    case DioExceptionType.sendTimeout:
    case DioExceptionType.receiveTimeout:
    case DioExceptionType.transformTimeout:
      return true;
    case DioExceptionType.cancel:
    case DioExceptionType.badCertificate:
      return false;
    case DioExceptionType.badResponse:
    case DioExceptionType.unknown:
      final int? status = error.response?.statusCode;
      if (status == null) return error.type == DioExceptionType.unknown;
      return status >= 500 || status == 408;
  }
}
