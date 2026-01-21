// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'stage_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_StageRequest _$StageRequestFromJson(Map<String, dynamic> json) =>
    _StageRequest(
      name: json['name'] as String,
      dateTime: json['dateTime'] as String,
      media: MediaDto.fromJson(json['media'] as Map<String, dynamic>),
      id: json['id'] as String?,
      routeSlug: json['routeSlug'] as String?,
      startPlaceId: json['startPlaceId'] as String?,
      endPlaceId: json['endPlaceId'] as String?,
    );

Map<String, dynamic> _$StageRequestToJson(_StageRequest instance) =>
    <String, dynamic>{
      'name': instance.name,
      'dateTime': instance.dateTime,
      'media': instance.media.toJson(),
      'id': instance.id,
      'routeSlug': instance.routeSlug,
      'startPlaceId': instance.startPlaceId,
      'endPlaceId': instance.endPlaceId,
    };
