// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'invitation_preview_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_InvitationPreviewDto _$InvitationPreviewDtoFromJson(
  Map<String, dynamic> json,
) => _InvitationPreviewDto(
  teamName: json['teamName'] as String,
  teamSlug: json['teamSlug'] as String,
  inviterName: json['inviterName'] as String,
  maskedEmail: json['maskedEmail'] as String,
  role: json['role'] as String,
  status: json['status'] as String,
  redeemable: json['redeemable'] as bool,
);

Map<String, dynamic> _$InvitationPreviewDtoToJson(
  _InvitationPreviewDto instance,
) => <String, dynamic>{
  'teamName': instance.teamName,
  'teamSlug': instance.teamSlug,
  'inviterName': instance.inviterName,
  'maskedEmail': instance.maskedEmail,
  'role': instance.role,
  'status': instance.status,
  'redeemable': instance.redeemable,
};
