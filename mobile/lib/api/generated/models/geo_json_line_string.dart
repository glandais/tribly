// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'geo_json_line_string_type_type.dart';

part 'geo_json_line_string.freezed.dart';
part 'geo_json_line_string.g.dart';

@Freezed()
abstract class GeoJsonLineString with _$GeoJsonLineString {
  const factory GeoJsonLineString({
    required GeoJsonLineStringTypeType type,

    /// Array of [lon, lat] coordinates
    required List<List<double>> coordinates,
  }) = _GeoJsonLineString;

  factory GeoJsonLineString.fromJson(Map<String, Object?> json) =>
      _$GeoJsonLineStringFromJson(json);
}
