// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'passkey_authentication_request.freezed.dart';
part 'passkey_authentication_request.g.dart';

/// Request for passkey authentication options
@Freezed()
abstract class PasskeyAuthenticationRequest
    with _$PasskeyAuthenticationRequest {
  const factory PasskeyAuthenticationRequest({
    /// Optional email to filter passkeys
    String? email,
  }) = _PasskeyAuthenticationRequest;

  factory PasskeyAuthenticationRequest.fromJson(Map<String, Object?> json) =>
      _$PasskeyAuthenticationRequestFromJson(json);
}
