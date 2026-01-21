import 'dart:async';

import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../features/auth/data/secure_storage.dart';
import '../generated/export.dart';
import '../tribly_api_client.dart';

/// Interceptor that handles authentication tokens and refresh
/// Mirrors frontend/src/lib/axiosInstance.ts
class AuthInterceptor extends Interceptor {
  final Ref _ref;
  final Dio _dio;

  // Track if we're currently refreshing to avoid multiple refresh calls
  bool _isRefreshing = false;

  // Queue of failed requests to retry after refresh
  final List<_QueuedRequest> _failedQueue = [];

  AuthInterceptor(this._ref, this._dio);

  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) {
    // Add auth token if available - read from simple token holder to avoid circular dep
    final accessToken = _ref.read(accessTokenHolderProvider);
    if (accessToken != null) {
      options.headers['Authorization'] = 'Bearer $accessToken';
    }

    // Add language header
    options.headers['Accept-Language'] = 'fr'; // Default to French like frontend

    handler.next(options);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) async {
    final response = err.response;
    final requestOptions = err.requestOptions;

    // Only try to refresh on 401 errors
    if (response?.statusCode != 401) {
      return handler.next(err);
    }

    // Don't try to refresh auth endpoints
    if (requestOptions.path.contains('/api/auth/')) {
      return handler.next(err);
    }

    // Check if this request was already retried
    if (requestOptions.extra['_retry'] == true) {
      return handler.next(err);
    }

    // If already refreshing, queue this request
    if (_isRefreshing) {
      final completer = Completer<Response>();
      _failedQueue.add(_QueuedRequest(
        requestOptions: requestOptions,
        completer: completer,
      ));
      try {
        final queuedResponse = await completer.future;
        return handler.resolve(queuedResponse);
      } catch (e) {
        return handler.next(err);
      }
    }

    // Mark as retrying
    requestOptions.extra['_retry'] = true;
    _isRefreshing = true;

    try {
      final storage = _ref.read(secureStorageProvider);
      final refreshToken = await storage.getRefreshToken();

      if (refreshToken == null) {
        _processQueue(err);
        _clearAuth();
        return handler.next(err);
      }

      // Create a fresh Dio instance for refresh to avoid interceptor loops
      final refreshDio = Dio(BaseOptions(
        baseUrl: requestOptions.baseUrl,
        headers: {'Content-Type': 'application/json'},
      ));

      final refreshResponse = await refreshDio.post(
        '/api/auth/refresh',
        options: Options(
          headers: {'X-Refresh-Token': refreshToken},
        ),
      );

      final authResponse = AuthResponse.fromJson(refreshResponse.data);

      // Update token in the simple holder
      _ref.read(accessTokenHolderProvider.notifier).state =
          authResponse.accessToken;

      // Save new refresh token if provided
      if (authResponse.refreshToken != null) {
        await storage.saveRefreshToken(authResponse.refreshToken!);
      }

      // Retry original request with new token
      requestOptions.headers['Authorization'] =
          'Bearer ${authResponse.accessToken}';

      // Process queued requests
      _processQueue(null, authResponse.accessToken);

      // Retry the original request using the main dio instance
      final retryResponse = await _dio.fetch(requestOptions);
      return handler.resolve(retryResponse);
    } catch (e) {
      _processQueue(err);
      _clearAuth();
      return handler.next(err);
    } finally {
      _isRefreshing = false;
    }
  }

  void _processQueue(DioException? error, [String? token]) {
    for (final request in _failedQueue) {
      if (error != null) {
        request.completer.completeError(error);
      } else if (token != null) {
        request.requestOptions.headers['Authorization'] = 'Bearer $token';
        _dio.fetch(request.requestOptions).then(
              request.completer.complete,
              onError: request.completer.completeError,
            );
      }
    }
    _failedQueue.clear();
  }

  void _clearAuth() {
    // Clear the token - navigation will be handled by router
    _ref.read(accessTokenHolderProvider.notifier).state = null;
    _ref.read(secureStorageProvider).deleteRefreshToken();
  }
}

class _QueuedRequest {
  final RequestOptions requestOptions;
  final Completer<Response> completer;

  _QueuedRequest({
    required this.requestOptions,
    required this.completer,
  });
}
