// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'gps_o_auth_version.dart';

part 'update_gps_credential_request.freezed.dart';
part 'update_gps_credential_request.g.dart';

/// Request to update a GPS credential
@Freezed()
abstract class UpdateGpsCredentialRequest with _$UpdateGpsCredentialRequest {
  const factory UpdateGpsCredentialRequest({
    /// OAuth client ID
    required String clientId,

    /// OAuth client secret (null = keep current)
    String? clientSecret,

    /// Whether credential is active
    bool? active,

    /// OAuth protocol of the client ID and secret (null = keep current). OAUTH1 is accepted for GARMIN only. Switching it leaves existing connections unusable: each is dropped at its next upload and must be reconnected
    String? oauthVersion,
  }) = _UpdateGpsCredentialRequest;

  factory UpdateGpsCredentialRequest.fromJson(Map<String, Object?> json) =>
      _$UpdateGpsCredentialRequestFromJson(json);
}
