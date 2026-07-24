// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'version_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_VersionDto _$VersionDtoFromJson(Map<String, dynamic> json) => _VersionDto(
  apiVersion: json['apiVersion'] as String,
  commit: json['commit'] as String?,
  buildTime: json['buildTime'] as String?,
);

Map<String, dynamic> _$VersionDtoToJson(_VersionDto instance) =>
    <String, dynamic>{
      'apiVersion': instance.apiVersion,
      'commit': instance.commit,
      'buildTime': instance.buildTime,
    };
