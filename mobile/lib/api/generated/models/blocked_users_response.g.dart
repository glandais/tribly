// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'blocked_users_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_BlockedUsersResponse _$BlockedUsersResponseFromJson(
  Map<String, dynamic> json,
) => _BlockedUsersResponse(
  users: (json['users'] as List<dynamic>)
      .map((e) => PublicUserDto.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$BlockedUsersResponseToJson(
  _BlockedUsersResponse instance,
) => <String, dynamic>{'users': instance.users.map((e) => e.toJson()).toList()};
