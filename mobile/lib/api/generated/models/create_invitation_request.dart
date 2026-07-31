// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'team_role.dart';

part 'create_invitation_request.freezed.dart';
part 'create_invitation_request.g.dart';

/// Invite an e-mail address to join a team
@Freezed()
abstract class CreateInvitationRequest with _$CreateInvitationRequest {
  const factory CreateInvitationRequest({
    /// Address to invite. The response is the same whether or not an account already exists for it — only the wording of the e-mail differs. Answering differently would turn this into an account-existence oracle for anyone who administers a team.
    required String email,

    /// Role to grant on acceptance. Defaults to MEMBER.
    String? role,
  }) = _CreateInvitationRequest;

  factory CreateInvitationRequest.fromJson(Map<String, Object?> json) =>
      _$CreateInvitationRequestFromJson(json);
}
