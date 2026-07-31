// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'my_invitation_dto.dart';

part 'my_invitation_list_response.freezed.dart';
part 'my_invitation_list_response.g.dart';

/// The invitations waiting for the current user
@Freezed()
abstract class MyInvitationListResponse with _$MyInvitationListResponse {
  const factory MyInvitationListResponse({
    /// Invitations, newest first
    required List<MyInvitationDto> invitations,
  }) = _MyInvitationListResponse;

  factory MyInvitationListResponse.fromJson(Map<String, Object?> json) =>
      _$MyInvitationListResponseFromJson(json);
}
