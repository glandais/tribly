// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'accept_invitation_request.freezed.dart';
part 'accept_invitation_request.g.dart';

/// Redeem an invitation token
@Freezed()
abstract class AcceptInvitationRequest with _$AcceptInvitationRequest {
  const factory AcceptInvitationRequest({
    /// The token from the invitation e-mail. Sent in the body rather than in the path, because a bearer secret in a URL path ends up in access logs and in the Referer of anything the page loads.
    required String token,
  }) = _AcceptInvitationRequest;

  factory AcceptInvitationRequest.fromJson(Map<String, Object?> json) =>
      _$AcceptInvitationRequestFromJson(json);
}
