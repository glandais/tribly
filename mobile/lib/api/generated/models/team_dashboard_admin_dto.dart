// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'member_list_response.dart';
import 'team_webhook_dto.dart';

part 'team_dashboard_admin_dto.freezed.dart';
part 'team_dashboard_admin_dto.g.dart';

/// The administration panel of a team dashboard. The split of the members per role is team.memberCountByRole, the enabled modules the team's enable* flags.
@Freezed()
abstract class TeamDashboardAdminDto with _$TeamDashboardAdminDto {
  const factory TeamDashboardAdminDto({
    /// The newest members, latest joined first (at most 3), with role and joinedAt. total is the member count.
    required MemberListResponse newestMembers,

    /// The team's webhook: configured, kind, enabled, lastStatus, lastAttemptAt. The URL only comes masked.
    required TeamWebhookDto webhook,
  }) = _TeamDashboardAdminDto;

  factory TeamDashboardAdminDto.fromJson(Map<String, Object?> json) =>
      _$TeamDashboardAdminDtoFromJson(json);
}
