// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'bounds_dto.dart';
import 'route_detail_dto.dart';

part 'routes_bulk_response.freezed.dart';
part 'routes_bulk_response.g.dart';

/// Detail of several routes fetched at once, plus their combined extent
@Freezed()
abstract class RoutesBulkResponse with _$RoutesBulkResponse {
  const factory RoutesBulkResponse({
    /// Route details, one per readable requested slug
    required List<RouteDetailDto> routes,

    /// Bounding box of every returned route, or null when routes is empty
    BoundsDto? extent,
  }) = _RoutesBulkResponse;

  factory RoutesBulkResponse.fromJson(Map<String, Object?> json) =>
      _$RoutesBulkResponseFromJson(json);
}
