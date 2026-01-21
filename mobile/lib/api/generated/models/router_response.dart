// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'geo_json_line_string.dart';

part 'router_response.freezed.dart';
part 'router_response.g.dart';

@Freezed()
abstract class RouterResponse with _$RouterResponse {
  const factory RouterResponse({
    required GeoJsonLineString route,
    double? dist,
    double? ascend,
  }) = _RouterResponse;

  factory RouterResponse.fromJson(Map<String, Object?> json) =>
      _$RouterResponseFromJson(json);
}
