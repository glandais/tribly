// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_webhook_test_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamWebhookTestDto _$TeamWebhookTestDtoFromJson(Map<String, dynamic> json) =>
    _TeamWebhookTestDto(
      success: json['success'] as bool,
      statusCode: (json['statusCode'] as num?)?.toInt(),
      error: json['error'] as String?,
    );

Map<String, dynamic> _$TeamWebhookTestDtoToJson(_TeamWebhookTestDto instance) =>
    <String, dynamic>{
      'success': instance.success,
      'statusCode': instance.statusCode,
      'error': instance.error,
    };
