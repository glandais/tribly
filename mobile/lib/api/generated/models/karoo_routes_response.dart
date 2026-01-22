// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'karoo_route_dto.dart';

part 'karoo_routes_response.freezed.dart';
part 'karoo_routes_response.g.dart';

/// List of routes for Karoo device
@Freezed()
abstract class KarooRoutesResponse with _$KarooRoutesResponse {
  const factory KarooRoutesResponse({
    /// Available routes
    required List<KarooRouteDto> routes,
  }) = _KarooRoutesResponse;

  factory KarooRoutesResponse.fromJson(Map<String, Object?> json) =>
      _$KarooRoutesResponseFromJson(json);
}
