// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'activate_account_request.freezed.dart';
part 'activate_account_request.g.dart';

/// Activates an account from its verification link: the password is chosen here, by whoever holds the mailbox, never at sign-up.
@Freezed()
abstract class ActivateAccountRequest with _$ActivateAccountRequest {
  const factory ActivateAccountRequest({
    /// Verification token
    required String token,

    /// Password (min 8 chars)
    required String password,
  }) = _ActivateAccountRequest;

  factory ActivateAccountRequest.fromJson(Map<String, Object?> json) =>
      _$ActivateAccountRequestFromJson(json);
}
