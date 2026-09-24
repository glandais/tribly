// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'moderation_queue_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_ModerationQueueResponse _$ModerationQueueResponseFromJson(
  Map<String, dynamic> json,
) => _ModerationQueueResponse(
  items: (json['items'] as List<dynamic>)
      .map((e) => ModerationItemDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  total: (json['total'] as num).toInt(),
);

Map<String, dynamic> _$ModerationQueueResponseToJson(
  _ModerationQueueResponse instance,
) => <String, dynamic>{
  'items': instance.items.map((e) => e.toJson()).toList(),
  'total': instance.total,
};
