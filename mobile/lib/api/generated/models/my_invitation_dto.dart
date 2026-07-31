// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'team_publication_dto.dart';
import 'team_role.dart';

part 'my_invitation_dto.freezed.dart';
part 'my_invitation_dto.g.dart';

/// An invitation waiting for the current user
@Freezed()
abstract class MyInvitationDto with _$MyInvitationDto {
  const factory MyInvitationDto({
    /// Invitation ID (TSID)
    required String id,

    /// Team being offered
    required TeamPublicationDto team,

    /// Display name of whoever sent it
    required String inviterName,

    /// Role granted on acceptance
    required String role,

    /// When it stops being redeemable
    required String expiresAt,
  }) = _MyInvitationDto;

  factory MyInvitationDto.fromJson(Map<String, Object?> json) =>
      _$MyInvitationDtoFromJson(json);
}
