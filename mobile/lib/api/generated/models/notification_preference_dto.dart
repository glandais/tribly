// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'notification_channel.dart';
import 'notification_type.dart';

part 'notification_preference_dto.freezed.dart';
part 'notification_preference_dto.g.dart';

/// One cell of the notification preferences: a type on a channel
@Freezed()
abstract class NotificationPreferenceDto with _$NotificationPreferenceDto {
  const factory NotificationPreferenceDto({
    /// Notification type
    required String type,

    /// Delivery channel
    required String channel,

    /// Whether it is delivered, as currently in effect
    required bool enabled,

    /// What applies when the user never touched this cell. Lets a client offer a 'restore defaults' without hard-coding them.
    required bool enabledByDefault,
  }) = _NotificationPreferenceDto;

  factory NotificationPreferenceDto.fromJson(Map<String, Object?> json) =>
      _$NotificationPreferenceDtoFromJson(json);
}
