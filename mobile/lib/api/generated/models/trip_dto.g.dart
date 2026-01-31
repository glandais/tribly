// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'trip_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TripDto _$TripDtoFromJson(Map<String, dynamic> json) => _TripDto(
  type: json['type'] as String,
  team: TeamPublicationDto.fromJson(json['team'] as Map<String, dynamic>),
  id: json['id'] as String,
  slug: json['slug'] as String,
  name: json['name'] as String,
  media: MediaDto.fromJson(json['media'] as Map<String, dynamic>),
  dateTime: json['dateTime'] as String,
  status: json['status'] as String,
  visibility: json['visibility'] as String,
  participantCount: (json['participantCount'] as num).toInt(),
  stageCount: (json['stageCount'] as num).toInt(),
  stages: (json['stages'] as List<dynamic>)
      .map((e) => TripStageDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  participants: (json['participants'] as List<dynamic>)
      .map((e) => PublicUserDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  publishAt: json['publishAt'] as String?,
  createdAt: json['createdAt'] as String?,
  routeSlug: json['routeSlug'] as String?,
  thumbnailLightUrl: json['thumbnailLightUrl'] as String?,
  thumbnailDarkUrl: json['thumbnailDarkUrl'] as String?,
);

Map<String, dynamic> _$TripDtoToJson(_TripDto instance) => <String, dynamic>{
  'type': instance.type,
  'team': instance.team.toJson(),
  'id': instance.id,
  'slug': instance.slug,
  'name': instance.name,
  'media': instance.media.toJson(),
  'dateTime': instance.dateTime,
  'status': instance.status,
  'visibility': instance.visibility,
  'participantCount': instance.participantCount,
  'stageCount': instance.stageCount,
  'stages': instance.stages.map((e) => e.toJson()).toList(),
  'participants': instance.participants.map((e) => e.toJson()).toList(),
  'publishAt': instance.publishAt,
  'createdAt': instance.createdAt,
  'routeSlug': instance.routeSlug,
  'thumbnailLightUrl': instance.thumbnailLightUrl,
  'thumbnailDarkUrl': instance.thumbnailDarkUrl,
};
