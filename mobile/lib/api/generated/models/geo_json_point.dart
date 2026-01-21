// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'geo_json_point_type_type.dart';

part 'geo_json_point.freezed.dart';
part 'geo_json_point.g.dart';

/// Location coordinates [longitude, latitude]
@Freezed()
abstract class GeoJsonPoint with _$GeoJsonPoint {
  const factory GeoJsonPoint({
    required GeoJsonPointTypeType type,

    /// Coordinates [longitude, latitude]
    required List<double> coordinates,
  }) = _GeoJsonPoint;

  factory GeoJsonPoint.fromJson(Map<String, Object?> json) =>
      _$GeoJsonPointFromJson(json);
}
