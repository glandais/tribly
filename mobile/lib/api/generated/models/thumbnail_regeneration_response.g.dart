// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'thumbnail_regeneration_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ThumbnailRegenerationResponse _$ThumbnailRegenerationResponseFromJson(
  Map<String, dynamic> json,
) => _ThumbnailRegenerationResponse(
  dryRun: json['dryRun'] as bool,
  matched: (json['matched'] as num).toInt(),
  entities: (json['entities'] as List<dynamic>)
      .map((e) => ThumbnailOwnerReport.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$ThumbnailRegenerationResponseToJson(
  _ThumbnailRegenerationResponse instance,
) => <String, dynamic>{
  'dryRun': instance.dryRun,
  'matched': instance.matched,
  'entities': instance.entities.map((e) => e.toJson()).toList(),
};
