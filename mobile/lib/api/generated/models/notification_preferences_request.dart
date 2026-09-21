// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'notification_preference_update.dart';
import 'notification_team_preference_update.dart';

part 'notification_preferences_request.freezed.dart';
part 'notification_preferences_request.g.dart';

/// Notification preference cells to change
@Freezed()
abstract class NotificationPreferencesRequest
    with _$NotificationPreferencesRequest {
  const factory NotificationPreferencesRequest({
    /// The cells to change
    required List<NotificationPreferenceUpdate> preferences,

    /// The teams to mute or unmute
    List<NotificationTeamPreferenceUpdate>? teams,

    /// Switch the daily e-mail digest on or off; absent leaves it
    bool? emailDigest,
  }) = _NotificationPreferencesRequest;

  factory NotificationPreferencesRequest.fromJson(Map<String, Object?> json) =>
      _$NotificationPreferencesRequestFromJson(json);
}
