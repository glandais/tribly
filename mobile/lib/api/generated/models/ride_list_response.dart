// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'ride_dto.dart';

part 'ride_list_response.freezed.dart';
part 'ride_list_response.g.dart';

/// Paginated ride list response
@Freezed()
abstract class RideListResponse with _$RideListResponse {
  const factory RideListResponse({
    /// List of rides
    required List<RideDto> rides,

    /// Total number of rides
    required int total,

    /// Current page number
    required int page,

    /// Page size
    required int size,
  }) = _RideListResponse;

  factory RideListResponse.fromJson(Map<String, Object?> json) =>
      _$RideListResponseFromJson(json);
}
