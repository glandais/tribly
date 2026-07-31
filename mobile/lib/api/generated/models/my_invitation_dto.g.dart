// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'my_invitation_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_MyInvitationDto _$MyInvitationDtoFromJson(Map<String, dynamic> json) =>
    _MyInvitationDto(
      id: json['id'] as String,
      team: TeamPublicationDto.fromJson(json['team'] as Map<String, dynamic>),
      inviterName: json['inviterName'] as String,
      role: json['role'] as String,
      expiresAt: json['expiresAt'] as String,
    );

Map<String, dynamic> _$MyInvitationDtoToJson(_MyInvitationDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'team': instance.team.toJson(),
      'inviterName': instance.inviterName,
      'role': instance.role,
      'expiresAt': instance.expiresAt,
    };
