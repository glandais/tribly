// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'ride_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_RideDto _$RideDtoFromJson(Map<String, dynamic> json) => _RideDto(
  groupSummaries: (json['groupSummaries'] as List<dynamic>)
      .map((e) => RideGroupSummaryDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  team: TeamPublicationDto.fromJson(json['team'] as Map<String, dynamic>),
  id: json['id'] as String,
  slug: json['slug'] as String,
  name: json['name'] as String,
  media: MediaDto.fromJson(json['media'] as Map<String, dynamic>),
  dateTime: json['dateTime'] as String,
  status: json['status'] as String,
  finished: json['finished'] as bool,
  visibility: json['visibility'] as String,
  type: json['type'] as String,
  participantCount: (json['participantCount'] as num).toInt(),
  groupCount: (json['groupCount'] as num).toInt(),
  groups: (json['groups'] as List<dynamic>)
      .map((e) => RideGroupDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  registered: json['registered'] as bool,
  full: json['full'] as bool,
  deleted: json['deleted'] as bool,
  topParticipants: (json['topParticipants'] as List<dynamic>)
      .map((e) => PublicUserDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  tags: (json['tags'] as List<dynamic>)
      .map((e) => TagDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  elevationGain: (json['elevationGain'] as num?)?.toDouble(),
  surfaceType: json['surfaceType'] as String?,
  startPlace: json['startPlace'] == null
      ? null
      : PlaceDetailDto.fromJson(json['startPlace'] as Map<String, dynamic>),
  distance: (json['distance'] as num?)?.toDouble(),
  createdAt: json['createdAt'] as String?,
  thumbnailLightUrl: json['thumbnailLightUrl'] as String?,
  thumbnailDarkUrl: json['thumbnailDarkUrl'] as String?,
  thumbnailUrl: json['thumbnailUrl'] as String?,
  routeSlug: json['routeSlug'] as String?,
  publishAt: json['publishAt'] as String?,
  registeredGroupId: json['registeredGroupId'] as String?,
  registeredGroup: json['registeredGroup'] == null
      ? null
      : RideGroupDto.fromJson(json['registeredGroup'] as Map<String, dynamic>),
  excerpt: json['excerpt'] as String?,
  maxParticipants: (json['maxParticipants'] as num?)?.toInt(),
  commentCount: (json['commentCount'] as num?)?.toInt(),
  endPlace: json['endPlace'] == null
      ? null
      : PlaceDetailDto.fromJson(json['endPlace'] as Map<String, dynamic>),
);

Map<String, dynamic> _$RideDtoToJson(_RideDto instance) => <String, dynamic>{
  'groupSummaries': instance.groupSummaries.map((e) => e.toJson()).toList(),
  'team': instance.team.toJson(),
  'id': instance.id,
  'slug': instance.slug,
  'name': instance.name,
  'media': instance.media.toJson(),
  'dateTime': instance.dateTime,
  'status': instance.status,
  'finished': instance.finished,
  'visibility': instance.visibility,
  'type': instance.type,
  'participantCount': instance.participantCount,
  'groupCount': instance.groupCount,
  'groups': instance.groups.map((e) => e.toJson()).toList(),
  'registered': instance.registered,
  'full': instance.full,
  'deleted': instance.deleted,
  'topParticipants': instance.topParticipants.map((e) => e.toJson()).toList(),
  'tags': instance.tags.map((e) => e.toJson()).toList(),
  'elevationGain': instance.elevationGain,
  'surfaceType': instance.surfaceType,
  'startPlace': instance.startPlace?.toJson(),
  'distance': instance.distance,
  'createdAt': instance.createdAt,
  'thumbnailLightUrl': instance.thumbnailLightUrl,
  'thumbnailDarkUrl': instance.thumbnailDarkUrl,
  'thumbnailUrl': instance.thumbnailUrl,
  'routeSlug': instance.routeSlug,
  'publishAt': instance.publishAt,
  'registeredGroupId': instance.registeredGroupId,
  'registeredGroup': instance.registeredGroup?.toJson(),
  'excerpt': instance.excerpt,
  'maxParticipants': instance.maxParticipants,
  'commentCount': instance.commentCount,
  'endPlace': instance.endPlace?.toJson(),
};
