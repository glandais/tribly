// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_preferences_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_NotificationPreferencesDto _$NotificationPreferencesDtoFromJson(
  Map<String, dynamic> json,
) => _NotificationPreferencesDto(
  channels: (json['channels'] as List<dynamic>)
      .map((e) => NotificationChannel.fromJson(e as String))
      .toList(),
  preferences: (json['preferences'] as List<dynamic>)
      .map((e) => NotificationPreferenceDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  teams: (json['teams'] as List<dynamic>)
      .map(
        (e) =>
            NotificationTeamPreferenceDto.fromJson(e as Map<String, dynamic>),
      )
      .toList(),
  emailDigest: json['emailDigest'] as bool,
);

Map<String, dynamic> _$NotificationPreferencesDtoToJson(
  _NotificationPreferencesDto instance,
) => <String, dynamic>{
  'channels': instance.channels.map((e) => e.toJson()).toList(),
  'preferences': instance.preferences.map((e) => e.toJson()).toList(),
  'teams': instance.teams.map((e) => e.toJson()).toList(),
  'emailDigest': instance.emailDigest,
};
