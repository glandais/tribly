// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'map_center_dto.freezed.dart';
part 'map_center_dto.g.dart';

/// Initial map camera
@Freezed()
abstract class MapCenterDto with _$MapCenterDto {
  const factory MapCenterDto({
    /// Latitude in WGS84 degrees
    required double lat,

    /// Longitude in WGS84 degrees
    required double lon,

    /// Initial zoom level
    required double zoom,
  }) = _MapCenterDto;

  factory MapCenterDto.fromJson(Map<String, Object?> json) =>
      _$MapCenterDtoFromJson(json);
}
