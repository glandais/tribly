// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'invitation_status.dart';
import 'team_role.dart';

part 'invitation_preview_dto.freezed.dart';
part 'invitation_preview_dto.g.dart';

/// What an invitation says, readable by whoever holds its token
@Freezed()
abstract class InvitationPreviewDto with _$InvitationPreviewDto {
  const factory InvitationPreviewDto({
    /// Team name
    required String teamName,

    /// Team URL slug
    required String teamSlug,

    /// Display name of whoever sent the invitation
    required String inviterName,

    /// Invited address, masked (a***@example.com)
    required String maskedEmail,

    /// Role granted on acceptance
    required String role,

    /// Where the invitation stands
    required String status,

    /// Whether the invitation can still be redeemed. False for anything but a live PENDING one.
    required bool redeemable,
  }) = _InvitationPreviewDto;

  factory InvitationPreviewDto.fromJson(Map<String, Object?> json) =>
      _$InvitationPreviewDtoFromJson(json);
}
