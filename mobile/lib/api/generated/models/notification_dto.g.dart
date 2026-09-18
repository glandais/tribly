// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_NotificationDto _$NotificationDtoFromJson(Map<String, dynamic> json) =>
    _NotificationDto(
      id: json['id'] as String,
      type: json['type'] as String,
      read: json['read'] as bool,
      createdAt: json['createdAt'] as String,
      teamSlug: json['teamSlug'] as String,
      teamName: json['teamName'] as String,
      subjectType: json['subjectType'] as String,
      subjectSlug: json['subjectSlug'] as String,
      subjectName: json['subjectName'] as String,
      actorName: json['actorName'] as String?,
      subjectDateTime: json['subjectDateTime'] as String?,
      excerpt: json['excerpt'] as String?,
    );

Map<String, dynamic> _$NotificationDtoToJson(_NotificationDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'type': instance.type,
      'read': instance.read,
      'createdAt': instance.createdAt,
      'teamSlug': instance.teamSlug,
      'teamName': instance.teamName,
      'subjectType': instance.subjectType,
      'subjectSlug': instance.subjectSlug,
      'subjectName': instance.subjectName,
      'actorName': instance.actorName,
      'subjectDateTime': instance.subjectDateTime,
      'excerpt': instance.excerpt,
    };
