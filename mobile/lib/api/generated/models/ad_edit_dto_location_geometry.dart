// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'ad_edit_dto_location_geometry_type_type.dart';

part 'ad_edit_dto_location_geometry.freezed.dart';
part 'ad_edit_dto_location_geometry.g.dart';

@Freezed()
abstract class AdEditDtoLocationGeometry with _$AdEditDtoLocationGeometry {
  const factory AdEditDtoLocationGeometry({
    required AdEditDtoLocationGeometryTypeType type,

    /// Coordinates [longitude, latitude]
    required List<double> coordinates,
  }) = _AdEditDtoLocationGeometry;

  factory AdEditDtoLocationGeometry.fromJson(Map<String, Object?> json) =>
      _$AdEditDtoLocationGeometryFromJson(json);
}
