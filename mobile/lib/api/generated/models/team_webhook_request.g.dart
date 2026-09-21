// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_webhook_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamWebhookRequest _$TeamWebhookRequestFromJson(Map<String, dynamic> json) =>
    _TeamWebhookRequest(
      language: json['language'] as String,
      enabled: json['enabled'] as bool,
      url: json['url'] as String?,
    );

Map<String, dynamic> _$TeamWebhookRequestToJson(_TeamWebhookRequest instance) =>
    <String, dynamic>{
      'language': instance.language,
      'enabled': instance.enabled,
      'url': instance.url,
    };
