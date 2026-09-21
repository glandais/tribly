// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'notification_channel.dart';
import 'notification_preference_dto.dart';
import 'notification_team_preference_dto.dart';

part 'notification_preferences_dto.freezed.dart';
part 'notification_preferences_dto.g.dart';

/// The current user's notification preferences
@Freezed()
abstract class NotificationPreferencesDto with _$NotificationPreferencesDto {
  const factory NotificationPreferencesDto({
    /// Channels that can be configured on this server, in display order
    required List<NotificationChannel> channels,

    /// One cell per type and configurable channel
    required List<NotificationPreferenceDto> preferences,

    /// The user's teams on this site, each with its mute switch. Offered whatever the channels: muting also keeps the team's announcements out of the inbox.
    required List<NotificationTeamPreferenceDto> teams,

    /// Non-urgent e-mails are held and sent as one digest a day, at 7:00 in the user's time zone. Cancellations, changes and reminders still leave at once. Only meaningful when EMAIL is among the channels.
    required bool emailDigest,
  }) = _NotificationPreferencesDto;

  factory NotificationPreferencesDto.fromJson(Map<String, Object?> json) =>
      _$NotificationPreferencesDtoFromJson(json);
}
