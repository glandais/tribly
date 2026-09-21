// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'notification_team_preference_dto.freezed.dart';
part 'notification_team_preference_dto.g.dart';

/// Whether the current user silenced one of their teams
@Freezed()
abstract class NotificationTeamPreferenceDto
    with _$NotificationTeamPreferenceDto {
  const factory NotificationTeamPreferenceDto({
    /// Team slug
    required String teamSlug,

    /// Team name
    required String teamName,

    /// Muted: none of the team's announcements (publications) reach the user, inbox included. What concerns them personally — a cancelled ride they joined, a reply — still does.
    required bool muted,
  }) = _NotificationTeamPreferenceDto;

  factory NotificationTeamPreferenceDto.fromJson(Map<String, Object?> json) =>
      _$NotificationTeamPreferenceDtoFromJson(json);
}
