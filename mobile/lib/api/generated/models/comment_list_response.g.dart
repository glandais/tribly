// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'comment_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_CommentListResponse _$CommentListResponseFromJson(Map<String, dynamic> json) =>
    _CommentListResponse(
      items: (json['items'] as List<dynamic>)
          .map((e) => CommentDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      total: (json['total'] as num).toInt(),
      itemTotal: (json['itemTotal'] as num).toInt(),
      page: (json['page'] as num).toInt(),
      size: (json['size'] as num).toInt(),
    );

Map<String, dynamic> _$CommentListResponseToJson(
  _CommentListResponse instance,
) => <String, dynamic>{
  'items': instance.items.map((e) => e.toJson()).toList(),
  'total': instance.total,
  'itemTotal': instance.itemTotal,
  'page': instance.page,
  'size': instance.size,
};
