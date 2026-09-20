// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/notification_list_response.dart';
import '../models/notification_preferences_dto.dart';
import '../models/notification_preferences_request.dart';
import '../models/push_device_registration.dart';
import '../models/unread_count_dto.dart';

part 'notifications_client.g.dart';

@RestApi()
abstract class NotificationsClient {
  factory NotificationsClient(Dio dio, {String? baseUrl}) =
      _NotificationsClient;

  /// List my notifications.
  ///
  /// The current user's notifications, newest first. Each carries a type and structured fields, not rendered text: the client words it in its own language.
  ///
  /// [page] - Page number (0-indexed).
  ///
  /// [size] - Page size (max 200).
  ///
  /// [unreadOnly] - Only unread notifications.
  @GET('/api/notifications')
  Future<NotificationListResponse> listMyNotifications({
    @Query('page') int? page = 0,
    @Query('size') int? size = 20,
    @Query('unreadOnly') bool? unreadOnly = false,
  });

  /// Update my notification preferences.
  ///
  /// A partial update: only the cells sent change. The full matrix is returned.
  ///
  /// [body] - Name not received - field will be skipped.
  @PUT('/api/notifications/preferences')
  Future<NotificationPreferencesDto> updateMyNotificationPreferences({
    @Body() required NotificationPreferencesRequest body,
  });

  /// Get my notification preferences.
  ///
  /// Every notification type on every channel this server can deliver on. The inbox is not listed: it always receives everything.
  @GET('/api/notifications/preferences')
  Future<NotificationPreferencesDto> getMyNotificationPreferences();

  /// Mark all my notifications read
  @POST('/api/notifications/read-all')
  Future<void> markAllNotificationsRead();

  /// Count my unread notifications.
  ///
  /// The badge on the bell. Cheap by design: clients poll it (on focus, at most once a minute) rather than reloading the list.
  @GET('/api/notifications/unread-count')
  Future<UnreadCountDto> countMyUnreadNotifications();

  /// Mark a notification read.
  ///
  /// Idempotent: marking an already-read notification read succeeds.
  @POST('/api/notifications/{notificationId}/read')
  Future<void> markNotificationRead({
    @Path('notificationId') required String notificationId,
  });

  /// Register a device for push notifications.
  ///
  /// Called at every app launch and whenever FCM rotates the token. Idempotent: a token already known is refreshed, and moved to the current user if it was someone else's.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/push-devices')
  Future<void> registerPushDevice({
    @Body() required PushDeviceRegistration body,
  });

  /// Stop sending push notifications to a device.
  ///
  /// Called on sign-out. Idempotent, and silent about tokens that are not the caller's.
  ///
  /// [token] - The FCM registration token to drop.
  @DELETE('/api/push-devices/{token}')
  Future<void> unregisterPushDevice({
    @Path('token') required String token,
  });
}
