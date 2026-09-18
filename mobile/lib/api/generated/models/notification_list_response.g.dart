// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'notification_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_NotificationListResponse _$NotificationListResponseFromJson(
  Map<String, dynamic> json,
) => _NotificationListResponse(
  items: (json['items'] as List<dynamic>)
      .map((e) => NotificationDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  total: (json['total'] as num).toInt(),
  unreadCount: (json['unreadCount'] as num).toInt(),
  page: (json['page'] as num).toInt(),
  size: (json['size'] as num).toInt(),
);

Map<String, dynamic> _$NotificationListResponseToJson(
  _NotificationListResponse instance,
) => <String, dynamic>{
  'items': instance.items.map((e) => e.toJson()).toList(),
  'total': instance.total,
  'unreadCount': instance.unreadCount,
  'page': instance.page,
  'size': instance.size,
};
