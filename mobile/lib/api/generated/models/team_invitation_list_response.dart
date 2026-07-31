// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'team_invitation_dto.dart';

part 'team_invitation_list_response.freezed.dart';
part 'team_invitation_list_response.g.dart';

/// Paginated list of a team's invitations
@Freezed()
abstract class TeamInvitationListResponse with _$TeamInvitationListResponse {
  const factory TeamInvitationListResponse({
    /// Invitations
    required List<TeamInvitationDto> invitations,

    /// Total number of invitations
    required int total,

    /// Current page (0-indexed)
    required int page,

    /// Page size
    required int size,
  }) = _TeamInvitationListResponse;

  factory TeamInvitationListResponse.fromJson(Map<String, Object?> json) =>
      _$TeamInvitationListResponseFromJson(json);
}
