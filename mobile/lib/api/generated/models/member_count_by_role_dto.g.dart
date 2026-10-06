// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'member_count_by_role_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_MemberCountByRoleDto _$MemberCountByRoleDtoFromJson(
  Map<String, dynamic> json,
) => _MemberCountByRoleDto(
  admins: (json['admins'] as num).toInt(),
  organizers: (json['organizers'] as num).toInt(),
  members: (json['members'] as num).toInt(),
);

Map<String, dynamic> _$MemberCountByRoleDtoToJson(
  _MemberCountByRoleDto instance,
) => <String, dynamic>{
  'admins': instance.admins,
  'organizers': instance.organizers,
  'members': instance.members,
};
