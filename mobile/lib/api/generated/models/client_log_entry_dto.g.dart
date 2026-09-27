// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'client_log_entry_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ClientLogEntryDto _$ClientLogEntryDtoFromJson(Map<String, dynamic> json) =>
    _ClientLogEntryDto(
      ts: json['ts'] as String,
      level: json['level'] as String,
      source: json['source'] as String,
      message: json['message'] as String,
    );

Map<String, dynamic> _$ClientLogEntryDtoToJson(_ClientLogEntryDto instance) =>
    <String, dynamic>{
      'ts': instance.ts,
      'level': instance.level,
      'source': instance.source,
      'message': instance.message,
    };
