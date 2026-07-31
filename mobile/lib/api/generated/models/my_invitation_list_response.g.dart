// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'my_invitation_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_MyInvitationListResponse _$MyInvitationListResponseFromJson(
  Map<String, dynamic> json,
) => _MyInvitationListResponse(
  invitations: (json['invitations'] as List<dynamic>)
      .map((e) => MyInvitationDto.fromJson(e as Map<String, dynamic>))
      .toList(),
);

Map<String, dynamic> _$MyInvitationListResponseToJson(
  _MyInvitationListResponse instance,
) => <String, dynamic>{
  'invitations': instance.invitations.map((e) => e.toJson()).toList(),
};
