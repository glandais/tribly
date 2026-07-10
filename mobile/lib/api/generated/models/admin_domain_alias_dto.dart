// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';

part 'admin_domain_alias_dto.freezed.dart';
part 'admin_domain_alias_dto.g.dart';

/// Admin view of a domain alias (dedicated hostname pinned to a team)
@Freezed()
abstract class AdminDomainAliasDto with _$AdminDomainAliasDto {
  const factory AdminDomainAliasDto({
    /// Alias ID (TSID)
    required String id,

    /// Alias hostname
    required String hostname,

    /// Parent domain ID (TSID)
    required String domainId,

    /// Pinned team ID (TSID)
    required String pinnedTeamId,

    /// Pinned team slug
    required String pinnedTeamSlug,

    /// Alias display name
    required String name,

    /// Base URL for the alias
    required String baseUrl,

    /// Whether alias is active
    required bool active,

    /// Alias creation timestamp
    required String createdAt,

    /// Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format)
    String? androidFingerprints,
  }) = _AdminDomainAliasDto;

  factory AdminDomainAliasDto.fromJson(Map<String, Object?> json) =>
      _$AdminDomainAliasDtoFromJson(json);
}
