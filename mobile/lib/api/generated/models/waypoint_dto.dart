// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'geo_json_point.dart';

part 'waypoint_dto.freezed.dart';
part 'waypoint_dto.g.dart';

@Freezed()
abstract class WaypointDto with _$WaypointDto {
  const factory WaypointDto({
    required GeoJsonPoint geometry,
    String? name,
  }) = _WaypointDto;

  factory WaypointDto.fromJson(Map<String, Object?> json) =>
      _$WaypointDtoFromJson(json);
}
