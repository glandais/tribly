// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'team_request_geometry_type_type.dart';

part 'team_request_geometry.freezed.dart';
part 'team_request_geometry.g.dart';

@Freezed()
abstract class TeamRequestGeometry with _$TeamRequestGeometry {
  const factory TeamRequestGeometry({
    required TeamRequestGeometryTypeType type,

    /// Coordinates [longitude, latitude]
    required List<double> coordinates,
  }) = _TeamRequestGeometry;

  factory TeamRequestGeometry.fromJson(Map<String, Object?> json) =>
      _$TeamRequestGeometryFromJson(json);
}
