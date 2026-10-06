// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ride_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RideDto _$RideDtoFromJson(Map<String, dynamic> json) => _RideDto(
  groups: (json['groups'] as List<dynamic>)
      .map((e) => RideGroupDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  team: TeamPublicationDto.fromJson(json['team'] as Map<String, dynamic>),
  id: json['id'] as String,
  slug: json['slug'] as String,
  name: json['name'] as String,
  media: MediaDto.fromJson(json['media'] as Map<String, dynamic>),
  visibility: json['visibility'] as String,
  dateTime: json['dateTime'] as String,
  timezone: json['timezone'] as String,
  endDateTime: json['endDateTime'] as String,
  status: json['status'] as String,
  finished: json['finished'] as bool,
  type: json['type'] as String,
  participantCount: (json['participantCount'] as num).toInt(),
  groupCount: (json['groupCount'] as num).toInt(),
  groupSummaries: (json['groupSummaries'] as List<dynamic>)
      .map((e) => RideGroupSummaryDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  tags: (json['tags'] as List<dynamic>)
      .map((e) => TagDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  full: json['full'] as bool,
  deleted: json['deleted'] as bool,
  registered: json['registered'] as bool,
  topParticipants: (json['topParticipants'] as List<dynamic>)
      .map((e) => PublicUserDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  elevationGain: (json['elevationGain'] as num?)?.toDouble(),
  surfaceType: json['surfaceType'] as String?,
  startPlace: json['startPlace'] == null
      ? null
      : PlaceDetailDto.fromJson(json['startPlace'] as Map<String, dynamic>),
  weather: json['weather'] == null
      ? null
      : RideWeatherSummaryDto.fromJson(json['weather'] as Map<String, dynamic>),
  distance: (json['distance'] as num?)?.toDouble(),
  thumbnailLightUrl: json['thumbnailLightUrl'] as String?,
  thumbnailDarkUrl: json['thumbnailDarkUrl'] as String?,
  thumbnailUrl: json['thumbnailUrl'] as String?,
  excerpt: json['excerpt'] as String?,
  routeSlug: json['routeSlug'] as String?,
  registeredGroupId: json['registeredGroupId'] as String?,
  registeredGroup: json['registeredGroup'] == null
      ? null
      : RideGroupDto.fromJson(json['registeredGroup'] as Map<String, dynamic>),
  createdAt: json['createdAt'] as String?,
  maxParticipants: (json['maxParticipants'] as num?)?.toInt(),
  commentCount: (json['commentCount'] as num?)?.toInt(),
  publishAt: json['publishAt'] as String?,
  endPlace: json['endPlace'] == null
      ? null
      : PlaceDetailDto.fromJson(json['endPlace'] as Map<String, dynamic>),
);

Map<String, dynamic> _$RideDtoToJson(_RideDto instance) => <String, dynamic>{
  'groups': instance.groups.map((e) => e.toJson()).toList(),
  'team': instance.team.toJson(),
  'id': instance.id,
  'slug': instance.slug,
  'name': instance.name,
  'media': instance.media.toJson(),
  'visibility': instance.visibility,
  'dateTime': instance.dateTime,
  'timezone': instance.timezone,
  'endDateTime': instance.endDateTime,
  'status': instance.status,
  'finished': instance.finished,
  'type': instance.type,
  'participantCount': instance.participantCount,
  'groupCount': instance.groupCount,
  'groupSummaries': instance.groupSummaries.map((e) => e.toJson()).toList(),
  'tags': instance.tags.map((e) => e.toJson()).toList(),
  'full': instance.full,
  'deleted': instance.deleted,
  'registered': instance.registered,
  'topParticipants': instance.topParticipants.map((e) => e.toJson()).toList(),
  'elevationGain': instance.elevationGain,
  'surfaceType': instance.surfaceType,
  'startPlace': instance.startPlace?.toJson(),
  'weather': instance.weather?.toJson(),
  'distance': instance.distance,
  'thumbnailLightUrl': instance.thumbnailLightUrl,
  'thumbnailDarkUrl': instance.thumbnailDarkUrl,
  'thumbnailUrl': instance.thumbnailUrl,
  'excerpt': instance.excerpt,
  'routeSlug': instance.routeSlug,
  'registeredGroupId': instance.registeredGroupId,
  'registeredGroup': instance.registeredGroup?.toJson(),
  'createdAt': instance.createdAt,
  'maxParticipants': instance.maxParticipants,
  'commentCount': instance.commentCount,
  'publishAt': instance.publishAt,
  'endPlace': instance.endPlace?.toJson(),
};
