// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_invitation_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamInvitationDto _$TeamInvitationDtoFromJson(Map<String, dynamic> json) =>
    _TeamInvitationDto(
      id: json['id'] as String,
      email: json['email'] as String,
      role: json['role'] as String,
      status: json['status'] as String,
      expiresAt: json['expiresAt'] as String,
      createdAt: json['createdAt'] as String,
      invitedBy: PublicUserDto.fromJson(
        json['invitedBy'] as Map<String, dynamic>,
      ),
      acceptedAt: json['acceptedAt'] as String?,
    );

Map<String, dynamic> _$TeamInvitationDtoToJson(_TeamInvitationDto instance) =>
    <String, dynamic>{
      'id': instance.id,
      'email': instance.email,
      'role': instance.role,
      'status': instance.status,
      'expiresAt': instance.expiresAt,
      'createdAt': instance.createdAt,
      'invitedBy': instance.invitedBy.toJson(),
      'acceptedAt': instance.acceptedAt,
    };
