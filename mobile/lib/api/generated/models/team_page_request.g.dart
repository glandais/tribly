// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_page_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamPageRequest _$TeamPageRequestFromJson(Map<String, dynamic> json) =>
    _TeamPageRequest(
      title: json['title'] as String,
      media: MediaDto.fromJson(json['media'] as Map<String, dynamic>),
      visibility: json['visibility'] as String,
    );

Map<String, dynamic> _$TeamPageRequestToJson(_TeamPageRequest instance) =>
    <String, dynamic>{
      'title': instance.title,
      'media': instance.media.toJson(),
      'visibility': instance.visibility,
    };
