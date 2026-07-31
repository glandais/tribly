// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'map_terrain_dto.freezed.dart';
part 'map_terrain_dto.g.dart';

/// The elevation source used to shade the relief
@Freezed()
abstract class MapTerrainDto with _$MapTerrainDto {
  const factory MapTerrainDto({
    /// URL of a TileJSON document describing raster-DEM tiles. The document declares the encoding; the clients do not.
    required String url,

    /// Deepest zoom the provider renders. Honoured by the web, which sets it on the source; the mobile SDKs take their zoom range from the TileJSON instead.
    required int maxZoom,
  }) = _MapTerrainDto;

  factory MapTerrainDto.fromJson(Map<String, Object?> json) =>
      _$MapTerrainDtoFromJson(json);
}
