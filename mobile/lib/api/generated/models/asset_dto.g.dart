// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'asset_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AssetDto _$AssetDtoFromJson(Map<String, dynamic> json) => _AssetDto(
  id: json['id'] as String,
  fileName: json['fileName'] as String,
  contentType: json['contentType'] as String,
  url: json['url'] as String,
  imageUrl: json['imageUrl'] as String?,
  imageDimensions: json['imageDimensions'] == null
      ? null
      : AssetDimensionsDto.fromJson(
          json['imageDimensions'] as Map<String, dynamic>,
        ),
);

Map<String, dynamic> _$AssetDtoToJson(_AssetDto instance) => <String, dynamic>{
  'id': instance.id,
  'fileName': instance.fileName,
  'contentType': instance.contentType,
  'url': instance.url,
  'imageUrl': instance.imageUrl,
  'imageDimensions': instance.imageDimensions?.toJson(),
};
