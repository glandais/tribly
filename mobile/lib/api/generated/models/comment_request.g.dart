// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'comment_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_CommentRequest _$CommentRequestFromJson(Map<String, dynamic> json) =>
    _CommentRequest(
      content: json['content'] as String,
      parentId: json['parentId'] as String?,
    );

Map<String, dynamic> _$CommentRequestToJson(_CommentRequest instance) =>
    <String, dynamic>{
      'content': instance.content,
      'parentId': instance.parentId,
    };
