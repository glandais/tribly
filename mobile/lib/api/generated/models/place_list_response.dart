// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'place_detail_dto.dart';

part 'place_list_response.freezed.dart';
part 'place_list_response.g.dart';

/// Paginated place list response
@Freezed()
abstract class PlaceListResponse with _$PlaceListResponse {
  const factory PlaceListResponse({
    /// List of places
    required List<PlaceDetailDto> places,

    /// Total number of places
    required int total,

    /// Current page number
    required int page,

    /// Page size
    required int size,
  }) = _PlaceListResponse;

  factory PlaceListResponse.fromJson(Map<String, Object?> json) =>
      _$PlaceListResponseFromJson(json);
}
