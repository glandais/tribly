// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_invitation_list_response.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamInvitationListResponse _$TeamInvitationListResponseFromJson(
  Map<String, dynamic> json,
) => _TeamInvitationListResponse(
  invitations: (json['invitations'] as List<dynamic>)
      .map((e) => TeamInvitationDto.fromJson(e as Map<String, dynamic>))
      .toList(),
  total: (json['total'] as num).toInt(),
  page: (json['page'] as num).toInt(),
  size: (json['size'] as num).toInt(),
);

Map<String, dynamic> _$TeamInvitationListResponseToJson(
  _TeamInvitationListResponse instance,
) => <String, dynamic>{
  'invitations': instance.invitations.map((e) => e.toJson()).toList(),
  'total': instance.total,
  'page': instance.page,
  'size': instance.size,
};
