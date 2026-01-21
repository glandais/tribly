// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'ad_request_location_geometry_type_type.dart';

part 'ad_request_location_geometry.freezed.dart';
part 'ad_request_location_geometry.g.dart';

@Freezed()
abstract class AdRequestLocationGeometry with _$AdRequestLocationGeometry {
  const factory AdRequestLocationGeometry({
    required AdRequestLocationGeometryTypeType type,

    /// Coordinates [longitude, latitude]
    required List<double> coordinates,
  }) = _AdRequestLocationGeometry;

  factory AdRequestLocationGeometry.fromJson(Map<String, Object?> json) =>
      _$AdRequestLocationGeometryFromJson(json);
}
