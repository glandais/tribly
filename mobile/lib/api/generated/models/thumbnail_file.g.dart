// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'thumbnail_file.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ThumbnailFile _$ThumbnailFileFromJson(Map<String, dynamic> json) =>
    _ThumbnailFile(
      type: json['type'] as String,
      bytes: (json['bytes'] as num).toInt(),
    );

Map<String, dynamic> _$ThumbnailFileToJson(_ThumbnailFile instance) =>
    <String, dynamic>{'type': instance.type, 'bytes': instance.bytes};
