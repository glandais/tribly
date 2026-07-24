// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'version_dto.freezed.dart';
part 'version_dto.g.dart';

/// Server API contract version and build information
@Freezed()
abstract class VersionDto with _$VersionDto {
  const factory VersionDto({
    /// API contract version (semver). Bumped whenever the OpenAPI contract changes, and also exposed as info.version in the contract itself.
    required String apiVersion,

    /// Short git commit the server was built from, null when unavailable
    String? commit,

    /// Build timestamp (ISO-8601, UTC), null when unavailable
    String? buildTime,
  }) = _VersionDto;

  factory VersionDto.fromJson(Map<String, Object?> json) =>
      _$VersionDtoFromJson(json);
}
