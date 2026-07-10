// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'create_domain_alias_request.freezed.dart';
part 'create_domain_alias_request.g.dart';

/// Request to create a domain alias (dedicated hostname pinned to a team)
@Freezed()
abstract class CreateDomainAliasRequest with _$CreateDomainAliasRequest {
  const factory CreateDomainAliasRequest({
    /// Alias hostname
    required String hostname,

    /// Slug of the team to pin (must belong to the domain)
    required String teamSlug,

    /// Alias display name
    required String name,

    /// Base URL for the alias
    required String baseUrl,

    /// Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
    String? androidFingerprints,
  }) = _CreateDomainAliasRequest;

  factory CreateDomainAliasRequest.fromJson(Map<String, Object?> json) =>
      _$CreateDomainAliasRequestFromJson(json);
}
