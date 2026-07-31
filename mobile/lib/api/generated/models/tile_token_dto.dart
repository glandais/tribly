// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';

part 'tile_token_dto.freezed.dart';
part 'tile_token_dto.g.dart';

/// Short-lived token authorizing vector tile requests
@Freezed()
abstract class TileTokenDto with _$TileTokenDto {
  const factory TileTokenDto({
    /// Token to pass as the 't' query parameter of the .mvt endpoints
    required String token,

    /// Absolute expiry instant (ISO 8601), for logs and diagnostics
    required String expiresAt,

    /// Seconds until expiry, measured at issuance. Schedule renewal on this, not on expiresAt: it is immune to a device clock that drifts.
    required int expiresIn,
  }) = _TileTokenDto;

  factory TileTokenDto.fromJson(Map<String, Object?> json) =>
      _$TileTokenDtoFromJson(json);
}
