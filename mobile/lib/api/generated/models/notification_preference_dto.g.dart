// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_preference_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_NotificationPreferenceDto _$NotificationPreferenceDtoFromJson(
  Map<String, dynamic> json,
) => _NotificationPreferenceDto(
  type: json['type'] as String,
  channel: json['channel'] as String,
  enabled: json['enabled'] as bool,
  enabledByDefault: json['enabledByDefault'] as bool,
);

Map<String, dynamic> _$NotificationPreferenceDtoToJson(
  _NotificationPreferenceDto instance,
) => <String, dynamic>{
  'type': instance.type,
  'channel': instance.channel,
  'enabled': instance.enabled,
  'enabledByDefault': instance.enabledByDefault,
};
