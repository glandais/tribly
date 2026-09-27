// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'error_report_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ErrorReportRequest _$ErrorReportRequestFromJson(Map<String, dynamic> json) =>
    _ErrorReportRequest(
      context: ClientContextDto.fromJson(
        json['context'] as Map<String, dynamic>,
      ),
      error: ClientErrorDto.fromJson(json['error'] as Map<String, dynamic>),
      logs: (json['logs'] as List<dynamic>?)
          ?.map((e) => ClientLogEntryDto.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$ErrorReportRequestToJson(_ErrorReportRequest instance) =>
    <String, dynamic>{
      'context': instance.context.toJson(),
      'error': instance.error.toJson(),
      'logs': instance.logs?.map((e) => e.toJson()).toList(),
    };
