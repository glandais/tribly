// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user_export_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_UserExportDto _$UserExportDtoFromJson(Map<String, dynamic> json) =>
    _UserExportDto(
      id: json['id'] as String,
      status: json['status'] as String,
      requestedAt: json['requestedAt'] as String,
      completedAt: json['completedAt'] as String?,
      expiresAt: json['expiresAt'] as String?,
      fileSize: (json['fileSize'] as num?)?.toInt(),
    );

Map<String, dynamic> _$UserExportDtoToJson(_UserExportDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'status': instance.status,
      'requestedAt': instance.requestedAt,
      'completedAt': instance.completedAt,
      'expiresAt': instance.expiresAt,
      'fileSize': instance.fileSize,
    };
