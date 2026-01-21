// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'route_dto.dart';

part 'route_list_response.freezed.dart';
part 'route_list_response.g.dart';

/// Paginated route list response
@Freezed()
abstract class RouteListResponse with _$RouteListResponse {
  const factory RouteListResponse({
    /// List of routes
    required List<RouteDto> routes,

    /// Total number of routes
    required int total,

    /// Current page number
    required int page,

    /// Page size
    required int size,
  }) = _RouteListResponse;

  factory RouteListResponse.fromJson(Map<String, Object?> json) =>
      _$RouteListResponseFromJson(json);
}
