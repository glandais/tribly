// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamRequest _$TeamRequestFromJson(Map<String, dynamic> json) => _TeamRequest(
  name: json['name'] as String,
  media: MediaDto.fromJson(json['media'] as Map<String, dynamic>),
  visibility: json['visibility'] as String,
  enableTrips: json['enableTrips'] as bool,
  enableAds: json['enableAds'] as bool,
  enablePosts: json['enablePosts'] as bool,
  enableRides: json['enableRides'] as bool,
  enableRoutes: json['enableRoutes'] as bool,
  geometry: json['geometry'] == null
      ? null
      : TeamRequestGeometry.fromJson(json['geometry'] as Map<String, dynamic>),
);

Map<String, dynamic> _$TeamRequestToJson(_TeamRequest instance) =>
    <String, dynamic>{
      'name': instance.name,
      'media': instance.media.toJson(),
      'visibility': instance.visibility,
      'enableTrips': instance.enableTrips,
      'enableAds': instance.enableAds,
      'enablePosts': instance.enablePosts,
      'enableRides': instance.enableRides,
      'enableRoutes': instance.enableRoutes,
      'geometry': instance.geometry?.toJson(),
    };
