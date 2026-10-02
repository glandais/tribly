// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'gps_o_auth_version.dart';
import 'gps_service_type.dart';
import 'instant.dart';

part 'admin_gps_credential_dto.freezed.dart';
part 'admin_gps_credential_dto.g.dart';

/// Admin GPS credential view (without secret)
@Freezed()
abstract class AdminGpsCredentialDto with _$AdminGpsCredentialDto {
  const factory AdminGpsCredentialDto({
    /// Credential ID (TSID)
    required String id,

    /// GPS service type
    required String serviceType,

    /// OAuth client ID (consumer key for OAuth 1.0a)
    required String clientId,

    /// OAuth protocol of the client ID and secret
    required String oauthVersion,

    /// Whether credential is active
    required bool active,

    /// Credential creation timestamp
    required String createdAt,
  }) = _AdminGpsCredentialDto;

  factory AdminGpsCredentialDto.fromJson(Map<String, Object?> json) =>
      _$AdminGpsCredentialDtoFromJson(json);
}
