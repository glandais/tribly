// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'feedback_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_FeedbackRequest _$FeedbackRequestFromJson(Map<String, dynamic> json) =>
    _FeedbackRequest(
      kind: json['kind'] as String,
      context: ClientContextDto.fromJson(
        json['context'] as Map<String, dynamic>,
      ),
      message: json['message'] as String?,
      error: json['error'] == null
          ? null
          : ClientErrorDto.fromJson(json['error'] as Map<String, dynamic>),
      logs: (json['logs'] as List<dynamic>?)
          ?.map((e) => ClientLogEntryDto.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$FeedbackRequestToJson(_FeedbackRequest instance) =>
    <String, dynamic>{
      'kind': instance.kind,
      'context': instance.context.toJson(),
      'message': instance.message,
      'error': instance.error?.toJson(),
      'logs': instance.logs?.map((e) => e.toJson()).toList(),
    };
