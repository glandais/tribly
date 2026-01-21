// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'add_member_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AddMemberRequest _$AddMemberRequestFromJson(Map<String, dynamic> json) =>
    _AddMemberRequest(
      userId: json['userId'] as String,
      role: json['role'] as String?,
    );

Map<String, dynamic> _$AddMemberRequestToJson(_AddMemberRequest instance) =>
    <String, dynamic>{'userId': instance.userId, 'role': instance.role};
