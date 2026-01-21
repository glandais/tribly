// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'member_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_MemberListResponse _$MemberListResponseFromJson(Map<String, dynamic> json) =>
    _MemberListResponse(
      members: (json['members'] as List<dynamic>)
          .map((e) => MemberDto.fromJson(e as Map<String, dynamic>))
          .toList(),
      total: (json['total'] as num).toInt(),
      page: (json['page'] as num).toInt(),
      size: (json['size'] as num).toInt(),
    );

Map<String, dynamic> _$MemberListResponseToJson(_MemberListResponse instance) =>
    <String, dynamic>{
      'members': instance.members.map((e) => e.toJson()).toList(),
      'total': instance.total,
      'page': instance.page,
      'size': instance.size,
    };
