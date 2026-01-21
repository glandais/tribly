// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'trip_dto.dart';

part 'trip_list_response.freezed.dart';
part 'trip_list_response.g.dart';

/// Paginated trip list response
@Freezed()
abstract class TripListResponse with _$TripListResponse {
  const factory TripListResponse({
    /// List of trips
    required List<TripDto> trips,

    /// Total number of trips
    required int total,

    /// Current page number
    required int page,

    /// Page size
    required int size,
  }) = _TripListResponse;

  factory TripListResponse.fromJson(Map<String, Object?> json) =>
      _$TripListResponseFromJson(json);
}
