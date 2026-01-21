// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'post_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_PostRequest _$PostRequestFromJson(Map<String, dynamic> json) => _PostRequest(
  name: json['name'] as String,
  media: MediaDto.fromJson(json['media'] as Map<String, dynamic>),
  dateTime: json['dateTime'] as String,
  status: json['status'] as String,
  visibility: json['visibility'] as String,
  publishAt: json['publishAt'] as String?,
);

Map<String, dynamic> _$PostRequestToJson(_PostRequest instance) =>
    <String, dynamic>{
      'name': instance.name,
      'media': instance.media.toJson(),
      'dateTime': instance.dateTime,
      'status': instance.status,
      'visibility': instance.visibility,
      'publishAt': instance.publishAt,
    };
