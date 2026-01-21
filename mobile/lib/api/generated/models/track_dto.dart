// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'climb_dto.dart';
import 'geo_json_line_string.dart';

part 'track_dto.freezed.dart';
part 'track_dto.g.dart';

/// GPX track with track points
@Freezed()
abstract class TrackDto with _$TrackDto {
  const factory TrackDto({
    required GeoJsonLineString line,

    /// List of climbs on the route
    required List<ClimbDto> climbs,
  }) = _TrackDto;

  factory TrackDto.fromJson(Map<String, Object?> json) =>
      _$TrackDtoFromJson(json);
}
