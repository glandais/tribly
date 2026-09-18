// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'notification_channel.dart';
import 'notification_type.dart';

part 'notification_preference_update.freezed.dart';
part 'notification_preference_update.g.dart';

/// Switch one notification type on or off on one channel
@Freezed()
abstract class NotificationPreferenceUpdate
    with _$NotificationPreferenceUpdate {
  const factory NotificationPreferenceUpdate({
    /// Notification type
    required String type,

    /// Delivery channel. IN_APP is refused: the inbox is always on.
    required String channel,

    /// Whether to deliver it
    required bool enabled,
  }) = _NotificationPreferenceUpdate;

  factory NotificationPreferenceUpdate.fromJson(Map<String, Object?> json) =>
      _$NotificationPreferenceUpdateFromJson(json);
}
