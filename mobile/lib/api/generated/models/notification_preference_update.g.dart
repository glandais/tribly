// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_preference_update.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_NotificationPreferenceUpdate _$NotificationPreferenceUpdateFromJson(
  Map<String, dynamic> json,
) => _NotificationPreferenceUpdate(
  type: json['type'] as String,
  channel: json['channel'] as String,
  enabled: json['enabled'] as bool,
);

Map<String, dynamic> _$NotificationPreferenceUpdateToJson(
  _NotificationPreferenceUpdate instance,
) => <String, dynamic>{
  'type': instance.type,
  'channel': instance.channel,
  'enabled': instance.enabled,
};
