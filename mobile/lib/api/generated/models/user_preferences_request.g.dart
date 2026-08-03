// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_preferences_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_UserPreferencesRequest _$UserPreferencesRequestFromJson(
  Map<String, dynamic> json,
) => _UserPreferencesRequest(
  unitSystem: json['unitSystem'] as String?,
  theme: json['theme'] as String?,
  language: json['language'] as String?,
  timezone: json['timezone'] as String?,
  contactableByMembers: json['contactableByMembers'] as bool?,
);

Map<String, dynamic> _$UserPreferencesRequestToJson(
  _UserPreferencesRequest instance,
) => <String, dynamic>{
  'unitSystem': instance.unitSystem,
  'theme': instance.theme,
  'language': instance.language,
  'timezone': instance.timezone,
  'contactableByMembers': instance.contactableByMembers,
};
