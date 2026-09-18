// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_preferences_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_NotificationPreferencesRequest _$NotificationPreferencesRequestFromJson(
  Map<String, dynamic> json,
) => _NotificationPreferencesRequest(
  preferences: (json['preferences'] as List<dynamic>)
      .map(
        (e) => NotificationPreferenceUpdate.fromJson(e as Map<String, dynamic>),
      )
      .toList(),
);

Map<String, dynamic> _$NotificationPreferencesRequestToJson(
  _NotificationPreferencesRequest instance,
) => <String, dynamic>{
  'preferences': instance.preferences.map((e) => e.toJson()).toList(),
};
