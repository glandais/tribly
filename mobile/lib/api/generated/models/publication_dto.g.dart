// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'publication_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PublicationDtoRide _$PublicationDtoRideFromJson(Map<String, dynamic> json) =>
    PublicationDtoRide(
      team: TeamPublicationDto.fromJson(json['team'] as Map<String, dynamic>),
      id: json['id'] as String,
      slug: json['slug'] as String,
      name: json['name'] as String,
      media: MediaDto.fromJson(json['media'] as Map<String, dynamic>),
      dateTime: json['dateTime'] as String,
      status: json['status'] as String,
      visibility: json['visibility'] as String,
      participantCount: (json['participantCount'] as num).toInt(),
      groupCount: (json['groupCount'] as num).toInt(),
      groups: (json['groups'] as List<dynamic>)
          .map((e) => RideGroupDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      topParticipants: (json['topParticipants'] as List<dynamic>)
          .map((e) => PublicUserDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      publishAt: json['publishAt'] as String?,
      createdAt: json['createdAt'] as String?,
      routeSlug: json['routeSlug'] as String?,
      startPlace: json['startPlace'] == null
          ? null
          : PlaceDetailDto.fromJson(json['startPlace'] as Map<String, dynamic>),
      endPlace: json['endPlace'] == null
          ? null
          : PlaceDetailDto.fromJson(json['endPlace'] as Map<String, dynamic>),
      thumbnailLightUrl: json['thumbnailLightUrl'] as String?,
      thumbnailDarkUrl: json['thumbnailDarkUrl'] as String?,
      $type: json['type'] as String?,
    );

Map<String, dynamic> _$PublicationDtoRideToJson(
  PublicationDtoRide instance,
) => <String, dynamic>{
  'team': instance.team.toJson(),
  'id': instance.id,
  'slug': instance.slug,
  'name': instance.name,
  'media': instance.media.toJson(),
  'dateTime': instance.dateTime,
  'status': instance.status,
  'visibility': instance.visibility,
  'participantCount': instance.participantCount,
  'groupCount': instance.groupCount,
  'groups': instance.groups.map((e) => e.toJson()).toList(),
  'topParticipants': instance.topParticipants.map((e) => e.toJson()).toList(),
  'publishAt': instance.publishAt,
  'createdAt': instance.createdAt,
  'routeSlug': instance.routeSlug,
  'startPlace': instance.startPlace?.toJson(),
  'endPlace': instance.endPlace?.toJson(),
  'thumbnailLightUrl': instance.thumbnailLightUrl,
  'thumbnailDarkUrl': instance.thumbnailDarkUrl,
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
      publishAt: json['publishAt'] as String?,
      createdAt: json['createdAt'] as String?,
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
      'publishAt': instance.publishAt,
      'createdAt': instance.createdAt,
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
      'type': instance.$type,
    };
