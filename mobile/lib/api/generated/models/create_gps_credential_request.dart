// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'gps_service_type.dart';

part 'create_gps_credential_request.freezed.dart';
part 'create_gps_credential_request.g.dart';

/// Request to create a new GPS credential
@Freezed()
abstract class CreateGpsCredentialRequest with _$CreateGpsCredentialRequest {
  const factory CreateGpsCredentialRequest({
    /// GPS service type
    required String serviceType,

    /// OAuth client ID
    required String clientId,

    /// OAuth client secret (nullable for PKCE services)
    String? clientSecret,

    /// Whether credential is active
    bool? active,
  }) = _CreateGpsCredentialRequest;

  factory CreateGpsCredentialRequest.fromJson(Map<String, Object?> json) =>
      _$CreateGpsCredentialRequestFromJson(json);
}
