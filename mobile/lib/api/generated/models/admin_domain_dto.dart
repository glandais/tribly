// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';

part 'admin_domain_dto.freezed.dart';
part 'admin_domain_dto.g.dart';

/// Admin domain view with statistics
@Freezed()
abstract class AdminDomainDto with _$AdminDomainDto {
  const factory AdminDomainDto({
    /// Domain ID (TSID)
    required String id,

    /// Domain hostname
    required String domain,

    /// Domain display name
    required String name,

    /// Base URL for the domain
    required String baseUrl,

    /// Whether domain is single-team mode
    required bool singleTeam,

    /// Whether domain is active
    required bool active,

    /// Number of teams in this domain
    required int teamCount,

    /// Number of users in this domain
    required int userCount,

    /// Domain creation timestamp
    required String createdAt,
  }) = _AdminDomainDto;

  factory AdminDomainDto.fromJson(Map<String, Object?> json) =>
      _$AdminDomainDtoFromJson(json);
}
