// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'publication_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PublicationDtoRide _$PublicationDtoRideFromJson(Map<String, dynamic> json) =>
    PublicationDtoRide(
      full: json['full'] as bool,
      id: json['id'] as String,
      slug: json['slug'] as String,
      name: json['name'] as String,
      media: MediaDto.fromJson(json['media'] as Map<String, dynamic>),
      groupSummaries: (json['groupSummaries'] as List<dynamic>)
          .map((e) => RideGroupSummaryDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      dateTime: json['dateTime'] as String,
      status: json['status'] as String,
      finished: json['finished'] as bool,
      visibility: json['visibility'] as String,
      team: TeamPublicationDto.fromJson(json['team'] as Map<String, dynamic>),
      participantCount: (json['participantCount'] as num).toInt(),
      groupCount: (json['groupCount'] as num).toInt(),
      groups: (json['groups'] as List<dynamic>)
          .map((e) => RideGroupDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      tags: (json['tags'] as List<dynamic>)
          .map((e) => TagDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      topParticipants: (json['topParticipants'] as List<dynamic>)
          .map((e) => PublicUserDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      deleted: json['deleted'] as bool,
      registered: json['registered'] as bool,
      createdAt: json['createdAt'] as String?,
      surfaceType: json['surfaceType'] as String?,
      startPlace: json['startPlace'] == null
          ? null
          : PlaceDetailDto.fromJson(json['startPlace'] as Map<String, dynamic>),
      routeSlug: json['routeSlug'] as String?,
      publishAt: json['publishAt'] as String?,
      thumbnailLightUrl: json['thumbnailLightUrl'] as String?,
      thumbnailDarkUrl: json['thumbnailDarkUrl'] as String?,
      thumbnailUrl: json['thumbnailUrl'] as String?,
      excerpt: json['excerpt'] as String?,
      distance: (json['distance'] as num?)?.toDouble(),
      registeredGroupId: json['registeredGroupId'] as String?,
      registeredGroup: json['registeredGroup'] == null
          ? null
          : RideGroupDto.fromJson(
              json['registeredGroup'] as Map<String, dynamic>,
            ),
      elevationGain: (json['elevationGain'] as num?)?.toDouble(),
      maxParticipants: (json['maxParticipants'] as num?)?.toInt(),
      commentCount: (json['commentCount'] as num?)?.toInt(),
      endPlace: json['endPlace'] == null
          ? null
          : PlaceDetailDto.fromJson(json['endPlace'] as Map<String, dynamic>),
      $type: json['type'] as String?,
    );

Map<String, dynamic> _$PublicationDtoRideToJson(
  PublicationDtoRide instance,
) => <String, dynamic>{
  'full': instance.full,
  'id': instance.id,
  'slug': instance.slug,
  'name': instance.name,
  'media': instance.media.toJson(),
  'groupSummaries': instance.groupSummaries.map((e) => e.toJson()).toList(),
  'dateTime': instance.dateTime,
  'status': instance.status,
  'finished': instance.finished,
  'visibility': instance.visibility,
  'team': instance.team.toJson(),
  'participantCount': instance.participantCount,
  'groupCount': instance.groupCount,
  'groups': instance.groups.map((e) => e.toJson()).toList(),
  'tags': instance.tags.map((e) => e.toJson()).toList(),
  'topParticipants': instance.topParticipants.map((e) => e.toJson()).toList(),
  'deleted': instance.deleted,
  'registered': instance.registered,
  'createdAt': instance.createdAt,
  'surfaceType': instance.surfaceType,
  'startPlace': instance.startPlace?.toJson(),
  'routeSlug': instance.routeSlug,
  'publishAt': instance.publishAt,
  'thumbnailLightUrl': instance.thumbnailLightUrl,
  'thumbnailDarkUrl': instance.thumbnailDarkUrl,
  'thumbnailUrl': instance.thumbnailUrl,
  'excerpt': instance.excerpt,
  'distance': instance.distance,
  'registeredGroupId': instance.registeredGroupId,
  'registeredGroup': instance.registeredGroup?.toJson(),
  'elevationGain': instance.elevationGain,
  'maxParticipants': instance.maxParticipants,
  'commentCount': instance.commentCount,
  'endPlace': instance.endPlace?.toJson(),
  'type': instance.$type,
};

PublicationDtoPost _$PublicationDtoPostFromJson(Map<String, dynamic> json) =>
    PublicationDtoPost(
      team: TeamPublicationDto.fromJson(json['team'] as Map<String, dynamic>),
      id: json['id'] as String,
      slug: json['slug'] as String,
      name: json['name'] as String,
      media: MediaDto.fromJson(json['media'] as Map<String, dynamic>),
      dateTime: json['dateTime'] as String,
      status: json['status'] as String,
      visibility: json['visibility'] as String,
      deleted: json['deleted'] as bool,
      signedAsTeam: json['signedAsTeam'] as bool,
      tags: (json['tags'] as List<dynamic>)
          .map((e) => TagDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      excerpt: json['excerpt'] as String?,
      thumbnailUrl: json['thumbnailUrl'] as String?,
      publishAt: json['publishAt'] as String?,
      createdAt: json['createdAt'] as String?,
      commentCount: (json['commentCount'] as num?)?.toInt(),
      createdBy: json['createdBy'] == null
          ? null
          : PublicUserDto.fromJson(json['createdBy'] as Map<String, dynamic>),
      $type: json['type'] as String?,
    );

Map<String, dynamic> _$PublicationDtoPostToJson(PublicationDtoPost instance) =>
    <String, dynamic>{
      'team': instance.team.toJson(),
      'id': instance.id,
      'slug': instance.slug,
      'name': instance.name,
      'media': instance.media.toJson(),
      'dateTime': instance.dateTime,
      'status': instance.status,
      'visibility': instance.visibility,
      'deleted': instance.deleted,
      'signedAsTeam': instance.signedAsTeam,
      'tags': instance.tags.map((e) => e.toJson()).toList(),
      'excerpt': instance.excerpt,
      'thumbnailUrl': instance.thumbnailUrl,
      'publishAt': instance.publishAt,
      'createdAt': instance.createdAt,
      'commentCount': instance.commentCount,
      'createdBy': instance.createdBy?.toJson(),
      'type': instance.$type,
    };

PublicationDtoTrip _$PublicationDtoTripFromJson(Map<String, dynamic> json) =>
    PublicationDtoTrip(
      team: TeamPublicationDto.fromJson(json['team'] as Map<String, dynamic>),
      id: json['id'] as String,
      slug: json['slug'] as String,
      name: json['name'] as String,
      media: MediaDto.fromJson(json['media'] as Map<String, dynamic>),
      dateTime: json['dateTime'] as String,
      status: json['status'] as String,
      finished: json['finished'] as bool,
      visibility: json['visibility'] as String,
      participantCount: (json['participantCount'] as num).toInt(),
      stageCount: (json['stageCount'] as num).toInt(),
      stages: (json['stages'] as List<dynamic>)
          .map((e) => TripStageDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      participants: (json['participants'] as List<dynamic>)
          .map((e) => PublicUserDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      deleted: json['deleted'] as bool,
      registered: json['registered'] as bool,
      tags: (json['tags'] as List<dynamic>)
          .map((e) => TagDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      excerpt: json['excerpt'] as String?,
      endDate: json['endDate'] as String?,
      publishAt: json['publishAt'] as String?,
      createdAt: json['createdAt'] as String?,
      routeSlug: json['routeSlug'] as String?,
      totalDistance: (json['totalDistance'] as num?)?.toDouble(),
      totalElevationGain: (json['totalElevationGain'] as num?)?.toDouble(),
      thumbnailLightUrl: json['thumbnailLightUrl'] as String?,
      thumbnailDarkUrl: json['thumbnailDarkUrl'] as String?,
      thumbnailUrl: json['thumbnailUrl'] as String?,
      commentCount: (json['commentCount'] as num?)?.toInt(),
      $type: json['type'] as String?,
    );

Map<String, dynamic> _$PublicationDtoTripToJson(PublicationDtoTrip instance) =>
    <String, dynamic>{
      'team': instance.team.toJson(),
      'id': instance.id,
      'slug': instance.slug,
      'name': instance.name,
      'media': instance.media.toJson(),
      'dateTime': instance.dateTime,
      'status': instance.status,
      'finished': instance.finished,
      'visibility': instance.visibility,
      'participantCount': instance.participantCount,
      'stageCount': instance.stageCount,
      'stages': instance.stages.map((e) => e.toJson()).toList(),
      'participants': instance.participants.map((e) => e.toJson()).toList(),
      'deleted': instance.deleted,
      'registered': instance.registered,
      'tags': instance.tags.map((e) => e.toJson()).toList(),
      'excerpt': instance.excerpt,
      'endDate': instance.endDate,
      'publishAt': instance.publishAt,
      'createdAt': instance.createdAt,
      'routeSlug': instance.routeSlug,
      'totalDistance': instance.totalDistance,
      'totalElevationGain': instance.totalElevationGain,
      'thumbnailLightUrl': instance.thumbnailLightUrl,
      'thumbnailDarkUrl': instance.thumbnailDarkUrl,
      'thumbnailUrl': instance.thumbnailUrl,
      'commentCount': instance.commentCount,
      'type': instance.$type,
    };
