// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'member_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_MemberDto _$MemberDtoFromJson(Map<String, dynamic> json) => _MemberDto(
  team: TeamPublicationDto.fromJson(json['team'] as Map<String, dynamic>),
  id: json['id'] as String,
  user: PublicUserDto.fromJson(json['user'] as Map<String, dynamic>),
  role: json['role'] as String?,
  joinedAt: json['joinedAt'] as String?,
);

Map<String, dynamic> _$MemberDtoToJson(_MemberDto instance) =>
    <String, dynamic>{
      'team': instance.team.toJson(),
      'id': instance.id,
      'user': instance.user.toJson(),
      'role': instance.role,
      'joinedAt': instance.joinedAt,
    };
