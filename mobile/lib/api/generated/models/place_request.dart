// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'place_request_geometry.dart';

part 'place_request.freezed.dart';
part 'place_request.g.dart';

/// Place create/update request
@Freezed()
abstract class PlaceRequest with _$PlaceRequest {
  const factory PlaceRequest({
    /// Place name
    required String name,

    /// Can be used as ride start point
    required bool startPlace,

    /// Can be used as ride end point
    required bool endPlace,

    /// Address
    String? address,

    /// External link (e.g., Google Maps URL)
    String? link,

    /// Geographic coordinates [longitude, latitude]
    PlaceRequestGeometry? geometry,
  }) = _PlaceRequest;

  factory PlaceRequest.fromJson(Map<String, Object?> json) =>
      _$PlaceRequestFromJson(json);
}
