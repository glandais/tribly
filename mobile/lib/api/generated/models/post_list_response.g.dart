// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'post_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_PostListResponse _$PostListResponseFromJson(Map<String, dynamic> json) =>
    _PostListResponse(
      posts: (json['posts'] as List<dynamic>)
          .map((e) => PostDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      total: (json['total'] as num).toInt(),
      page: (json['page'] as num).toInt(),
      size: (json['size'] as num).toInt(),
    );

Map<String, dynamic> _$PostListResponseToJson(_PostListResponse instance) =>
    <String, dynamic>{
      'posts': instance.posts.map((e) => e.toJson()).toList(),
      'total': instance.total,
      'page': instance.page,
      'size': instance.size,
    };
