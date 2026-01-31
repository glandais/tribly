// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'assets_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AssetsDto _$AssetsDtoFromJson(Map<String, dynamic> json) => _AssetsDto(
  images: (json['images'] as List<dynamic>)
      .map((e) => AssetDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  attachments: (json['attachments'] as List<dynamic>)
      .map((e) => AssetDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  logo: json['logo'] == null
      ? null
      : AssetDto.fromJson(json['logo'] as Map<String, dynamic>),
  originalGpx: json['originalGpx'] == null
      ? null
      : AssetDto.fromJson(json['originalGpx'] as Map<String, dynamic>),
  gpx: json['gpx'] == null
      ? null
      : AssetDto.fromJson(json['gpx'] as Map<String, dynamic>),
  fit: json['fit'] == null
      ? null
      : AssetDto.fromJson(json['fit'] as Map<String, dynamic>),
  thumbnailLight: json['thumbnailLight'] == null
      ? null
      : AssetDto.fromJson(json['thumbnailLight'] as Map<String, dynamic>),
  thumbnailDark: json['thumbnailDark'] == null
      ? null
      : AssetDto.fromJson(json['thumbnailDark'] as Map<String, dynamic>),
);

Map<String, dynamic> _$AssetsDtoToJson(_AssetsDto instance) =>
    <String, dynamic>{
      'images': instance.images.map((e) => e.toJson()).toList(),
      'attachments': instance.attachments.map((e) => e.toJson()).toList(),
      'logo': instance.logo?.toJson(),
      'originalGpx': instance.originalGpx?.toJson(),
      'gpx': instance.gpx?.toJson(),
      'fit': instance.fit?.toJson(),
      'thumbnailLight': instance.thumbnailLight?.toJson(),
      'thumbnailDark': instance.thumbnailDark?.toJson(),
    };
