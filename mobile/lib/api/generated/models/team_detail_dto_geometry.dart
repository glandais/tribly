// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'team_detail_dto_geometry_type_type.dart';

part 'team_detail_dto_geometry.freezed.dart';
part 'team_detail_dto_geometry.g.dart';

@Freezed()
abstract class TeamDetailDtoGeometry with _$TeamDetailDtoGeometry {
  const factory TeamDetailDtoGeometry({
    required TeamDetailDtoGeometryTypeType type,

    /// Coordinates [longitude, latitude]
    required List<double> coordinates,
  }) = _TeamDetailDtoGeometry;

  factory TeamDetailDtoGeometry.fromJson(Map<String, Object?> json) =>
      _$TeamDetailDtoGeometryFromJson(json);
}
