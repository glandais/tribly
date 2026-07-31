// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'create_invitation_request.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_CreateInvitationRequest _$CreateInvitationRequestFromJson(
  Map<String, dynamic> json,
) => _CreateInvitationRequest(
  email: json['email'] as String,
  role: json['role'] as String?,
);

Map<String, dynamic> _$CreateInvitationRequestToJson(
  _CreateInvitationRequest instance,
) => <String, dynamic>{'email': instance.email, 'role': instance.role};
