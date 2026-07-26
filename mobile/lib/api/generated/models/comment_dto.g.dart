// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'comment_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_CommentDto _$CommentDtoFromJson(Map<String, dynamic> json) => _CommentDto(
  id: json['id'] as String,
  content: json['content'] as String,
  author: PublicUserDto.fromJson(json['author'] as Map<String, dynamic>),
  createdAt: json['createdAt'] as String,
  replies: (json['replies'] as List<dynamic>)
      .map((e) => CommentDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  replyCount: (json['replyCount'] as num).toInt(),
  parentId: json['parentId'] as String?,
);

Map<String, dynamic> _$CommentDtoToJson(_CommentDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'content': instance.content,
      'author': instance.author.toJson(),
      'createdAt': instance.createdAt,
      'replies': instance.replies.map((e) => e.toJson()).toList(),
      'replyCount': instance.replyCount,
      'parentId': instance.parentId,
    };
