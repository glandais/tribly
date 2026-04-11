// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'create_domain_request.freezed.dart';
part 'create_domain_request.g.dart';

/// Request to create a new domain
@Freezed()
abstract class CreateDomainRequest with _$CreateDomainRequest {
  const factory CreateDomainRequest({
    /// Domain hostname
    required String domain,

    /// Domain display name
    required String name,

    /// Base URL for the domain
    required String baseUrl,

    /// Whether domain is single-team mode
    bool? singleTeam,

    /// Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
    String? androidFingerprints,
  }) = _CreateDomainRequest;

  factory CreateDomainRequest.fromJson(Map<String, Object?> json) =>
      _$CreateDomainRequestFromJson(json);
}
