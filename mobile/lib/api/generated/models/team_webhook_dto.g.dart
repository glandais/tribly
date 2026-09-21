// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_webhook_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamWebhookDto _$TeamWebhookDtoFromJson(Map<String, dynamic> json) =>
    _TeamWebhookDto(
      configured: json['configured'] as bool,
      enabled: json['enabled'] as bool,
      maskedUrl: json['maskedUrl'] as String?,
      kind: json['kind'] as String?,
      language: json['language'] as String?,
      lastStatus: json['lastStatus'] as String?,
      lastError: json['lastError'] as String?,
      lastAttemptAt: json['lastAttemptAt'] as String?,
    );

Map<String, dynamic> _$TeamWebhookDtoToJson(_TeamWebhookDto instance) =>
    <String, dynamic>{
      'configured': instance.configured,
      'enabled': instance.enabled,
      'maskedUrl': instance.maskedUrl,
      'kind': instance.kind,
      'language': instance.language,
      'lastStatus': instance.lastStatus,
      'lastError': instance.lastError,
      'lastAttemptAt': instance.lastAttemptAt,
    };
