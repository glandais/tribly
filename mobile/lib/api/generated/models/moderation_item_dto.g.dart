// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'moderation_item_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ModerationItemDto _$ModerationItemDtoFromJson(Map<String, dynamic> json) =>
    _ModerationItemDto(
      targetType: json['targetType'] as String,
      targetId: json['targetId'] as String,
      teamSlug: json['teamSlug'] as String,
      teamName: json['teamName'] as String,
      targetUser: PublicUserDto.fromJson(
        json['targetUser'] as Map<String, dynamic>,
      ),
      reportCount: (json['reportCount'] as num).toInt(),
      reasons: (json['reasons'] as List<dynamic>)
          .map((e) => ReportReason.fromJson(e as String))
          .toList(),
      messages: (json['messages'] as List<dynamic>)
          .map((e) => e as String)
          .toList(),
      firstReportedAt: json['firstReportedAt'] as String,
      lastReportedAt: json['lastReportedAt'] as String,
      hidden: json['hidden'] as bool,
      status: json['status'] as String,
      contentName: json['contentName'] as String?,
      contentType: json['contentType'] as String?,
      contentSlug: json['contentSlug'] as String?,
      excerpt: json['excerpt'] as String?,
      reporters: (json['reporters'] as List<dynamic>?)
          ?.map((e) => PublicUserDto.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$ModerationItemDtoToJson(_ModerationItemDto instance) =>
    <String, dynamic>{
      'targetType': instance.targetType,
      'targetId': instance.targetId,
      'teamSlug': instance.teamSlug,
      'teamName': instance.teamName,
      'targetUser': instance.targetUser.toJson(),
      'reportCount': instance.reportCount,
      'reasons': instance.reasons.map((e) => e.toJson()).toList(),
      'messages': instance.messages,
      'firstReportedAt': instance.firstReportedAt,
      'lastReportedAt': instance.lastReportedAt,
      'hidden': instance.hidden,
      'status': instance.status,
      'contentName': instance.contentName,
      'contentType': instance.contentType,
      'contentSlug': instance.contentSlug,
      'excerpt': instance.excerpt,
      'reporters': instance.reporters?.map((e) => e.toJson()).toList(),
    };
