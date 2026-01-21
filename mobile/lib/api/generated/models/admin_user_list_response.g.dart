// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'admin_user_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_AdminUserListResponse _$AdminUserListResponseFromJson(
  Map<String, dynamic> json,
) => _AdminUserListResponse(
  users: (json['users'] as List<dynamic>)
      .map((e) => AdminUserDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  total: (json['total'] as num).toInt(),
  page: (json['page'] as num).toInt(),
  size: (json['size'] as num).toInt(),
);

Map<String, dynamic> _$AdminUserListResponseToJson(
  _AdminUserListResponse instance,
) => <String, dynamic>{
  'users': instance.users.map((e) => e.toJson()).toList(),
  'total': instance.total,
  'page': instance.page,
  'size': instance.size,
};
