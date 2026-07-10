// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'bounds_dto.freezed.dart';
part 'bounds_dto.g.dart';

/// Geographic bounding box, in WGS84 degrees
@Freezed()
abstract class BoundsDto with _$BoundsDto {
  const factory BoundsDto({
    /// Western edge
    required double minLon,

    /// Southern edge
    required double minLat,

    /// Eastern edge
    required double maxLon,

    /// Northern edge
    required double maxLat,
  }) = _BoundsDto;

  factory BoundsDto.fromJson(Map<String, Object?> json) =>
      _$BoundsDtoFromJson(json);
}
