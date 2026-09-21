// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_team_preference_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_NotificationTeamPreferenceDto _$NotificationTeamPreferenceDtoFromJson(
  Map<String, dynamic> json,
) => _NotificationTeamPreferenceDto(
  teamSlug: json['teamSlug'] as String,
  teamName: json['teamName'] as String,
  muted: json['muted'] as bool,
);

Map<String, dynamic> _$NotificationTeamPreferenceDtoToJson(
  _NotificationTeamPreferenceDto instance,
) => <String, dynamic>{
  'teamSlug': instance.teamSlug,
  'teamName': instance.teamName,
  'muted': instance.muted,
};
