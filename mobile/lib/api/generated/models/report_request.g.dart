// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'report_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ReportRequest _$ReportRequestFromJson(Map<String, dynamic> json) =>
    _ReportRequest(
      teamSlug: json['teamSlug'] as String,
      targetType: json['targetType'] as String,
      targetId: json['targetId'] as String,
      reason: json['reason'] as String,
      message: json['message'] as String?,
    );

Map<String, dynamic> _$ReportRequestToJson(_ReportRequest instance) =>
    <String, dynamic>{
      'teamSlug': instance.teamSlug,
      'targetType': instance.targetType,
      'targetId': instance.targetId,
      'reason': instance.reason,
      'message': instance.message,
    };
