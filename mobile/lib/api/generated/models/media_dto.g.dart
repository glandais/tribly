// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'media_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_MediaDto _$MediaDtoFromJson(Map<String, dynamic> json) => _MediaDto(
  markdown: json['markdown'] as String,
  assets: AssetsDto.fromJson(json['assets'] as Map<String, dynamic>),
);

Map<String, dynamic> _$MediaDtoToJson(_MediaDto instance) => <String, dynamic>{
  'markdown': instance.markdown,
  'assets': instance.assets.toJson(),
};
