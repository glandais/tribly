// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'invitation_status.dart';
import 'public_user_dto.dart';
import 'team_role.dart';

part 'team_invitation_dto.freezed.dart';
part 'team_invitation_dto.g.dart';

/// A pending or settled invitation to join a team
@Freezed()
abstract class TeamInvitationDto with _$TeamInvitationDto {
  const factory TeamInvitationDto({
    /// Invitation ID (TSID)
    required String id,

    /// Invited e-mail address
    required String email,

    /// Role granted on acceptance
    required String role,

    /// Where the invitation stands
    required String status,

    /// When the invitation stops being redeemable
    required String expiresAt,

    /// When the invitation was sent
    required String createdAt,

    /// Who sent it
    required PublicUserDto invitedBy,

    /// When it was accepted, if it was
    String? acceptedAt,
  }) = _TeamInvitationDto;

  factory TeamInvitationDto.fromJson(Map<String, Object?> json) =>
      _$TeamInvitationDtoFromJson(json);
}
