// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'web_push_config_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_WebPushConfigDto _$WebPushConfigDtoFromJson(Map<String, dynamic> json) =>
    _WebPushConfigDto(
      apiKey: json['apiKey'] as String,
      projectId: json['projectId'] as String,
      appId: json['appId'] as String,
      messagingSenderId: json['messagingSenderId'] as String,
      vapidKey: json['vapidKey'] as String,
    );

Map<String, dynamic> _$WebPushConfigDtoToJson(_WebPushConfigDto instance) =>
    <String, dynamic>{
      'apiKey': instance.apiKey,
      'projectId': instance.projectId,
      'appId': instance.appId,
      'messagingSenderId': instance.messagingSenderId,
      'vapidKey': instance.vapidKey,
    };
