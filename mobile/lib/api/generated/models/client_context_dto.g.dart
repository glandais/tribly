// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'client_context_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ClientContextDto _$ClientContextDtoFromJson(Map<String, dynamic> json) =>
    _ClientContextDto(
      platform: json['platform'] as String,
      appVersion: json['appVersion'] as String,
      buildNumber: json['buildNumber'] as String?,
      osVersion: json['osVersion'] as String?,
      device: json['device'] as String?,
      userAgent: json['userAgent'] as String?,
      route: json['route'] as String?,
      locale: json['locale'] as String?,
      timezone: json['timezone'] as String?,
      teamSlug: json['teamSlug'] as String?,
    );

Map<String, dynamic> _$ClientContextDtoToJson(_ClientContextDto instance) =>
    <String, dynamic>{
      'platform': instance.platform,
      'appVersion': instance.appVersion,
      'buildNumber': instance.buildNumber,
      'osVersion': instance.osVersion,
      'device': instance.device,
      'userAgent': instance.userAgent,
      'route': instance.route,
      'locale': instance.locale,
      'timezone': instance.timezone,
      'teamSlug': instance.teamSlug,
    };
