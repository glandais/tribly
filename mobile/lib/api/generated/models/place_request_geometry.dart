// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'place_request_geometry_type_type.dart';

part 'place_request_geometry.freezed.dart';
part 'place_request_geometry.g.dart';

@Freezed()
abstract class PlaceRequestGeometry with _$PlaceRequestGeometry {
  const factory PlaceRequestGeometry({
    required PlaceRequestGeometryTypeType type,

    /// Coordinates [longitude, latitude]
    required List<double> coordinates,
  }) = _PlaceRequestGeometry;

  factory PlaceRequestGeometry.fromJson(Map<String, Object?> json) =>
      _$PlaceRequestGeometryFromJson(json);
}
