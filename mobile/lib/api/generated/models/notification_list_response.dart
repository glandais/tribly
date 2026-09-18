// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'notification_dto.dart';

part 'notification_list_response.freezed.dart';
part 'notification_list_response.g.dart';

/// A page of the current user's notifications, newest first
@Freezed()
abstract class NotificationListResponse with _$NotificationListResponse {
  const factory NotificationListResponse({
    /// The notifications of this page
    required List<NotificationDto> items,

    /// How many notifications match the query (unread only, if asked)
    required int total,

    /// How many notifications are unread, whatever the filter
    required int unreadCount,

    /// Page number (0-indexed)
    required int page,

    /// Page size applied
    required int size,
  }) = _NotificationListResponse;

  factory NotificationListResponse.fromJson(Map<String, Object?> json) =>
      _$NotificationListResponseFromJson(json);
}
