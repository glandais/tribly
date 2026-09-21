// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'notification_team_preference_update.freezed.dart';
part 'notification_team_preference_update.g.dart';

/// Mute or unmute one of the current user's teams
@Freezed()
abstract class NotificationTeamPreferenceUpdate
    with _$NotificationTeamPreferenceUpdate {
  const factory NotificationTeamPreferenceUpdate({
    /// Team slug — a team the user belongs to
    required String teamSlug,

    /// Whether to mute its announcements
    required bool muted,
  }) = _NotificationTeamPreferenceUpdate;

  factory NotificationTeamPreferenceUpdate.fromJson(
    Map<String, Object?> json,
  ) => _$NotificationTeamPreferenceUpdateFromJson(json);
}
