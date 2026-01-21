// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'route_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RouteDto _$RouteDtoFromJson(Map<String, dynamic> json) => _RouteDto(
  id: json['id'] as String,
  slug: json['slug'] as String,
  team: TeamPublicationDto.fromJson(json['team'] as Map<String, dynamic>),
  name: json['name'] as String,
  media: MediaDto.fromJson(json['media'] as Map<String, dynamic>),
  distance: (json['distance'] as num).toDouble(),
  elevationGain: (json['elevationGain'] as num).toDouble(),
  elevationLoss: (json['elevationLoss'] as num).toDouble(),
  surfaceType: json['surfaceType'] as String,
  visibility: json['visibility'] as String,
  createdAt: json['createdAt'] as String,
);

Map<String, dynamic> _$RouteDtoToJson(_RouteDto instance) => <String, dynamic>{
  'id': instance.id,
  'slug': instance.slug,
  'team': instance.team.toJson(),
  'name': instance.name,
  'media': instance.media.toJson(),
  'distance': instance.distance,
  'elevationGain': instance.elevationGain,
  'elevationLoss': instance.elevationLoss,
  'surfaceType': instance.surfaceType,
  'visibility': instance.visibility,
  'createdAt': instance.createdAt,
};
