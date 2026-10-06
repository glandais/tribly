// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'team_dashboard_admin_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_TeamDashboardAdminDto _$TeamDashboardAdminDtoFromJson(
  Map<String, dynamic> json,
) => _TeamDashboardAdminDto(
  newestMembers: MemberListResponse.fromJson(
    json['newestMembers'] as Map<String, dynamic>,
  ),
  webhook: TeamWebhookDto.fromJson(json['webhook'] as Map<String, dynamic>),
);

Map<String, dynamic> _$TeamDashboardAdminDtoToJson(
  _TeamDashboardAdminDto instance,
) => <String, dynamic>{
  'newestMembers': instance.newestMembers.toJson(),
  'webhook': instance.webhook.toJson(),
};
