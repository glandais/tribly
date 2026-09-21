// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_team_preference_update.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_NotificationTeamPreferenceUpdate _$NotificationTeamPreferenceUpdateFromJson(
  Map<String, dynamic> json,
) => _NotificationTeamPreferenceUpdate(
  teamSlug: json['teamSlug'] as String,
  muted: json['muted'] as bool,
);

Map<String, dynamic> _$NotificationTeamPreferenceUpdateToJson(
  _NotificationTeamPreferenceUpdate instance,
) => <String, dynamic>{'teamSlug': instance.teamSlug, 'muted': instance.muted};
