// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'ad_dto.dart';

part 'ad_list_response.freezed.dart';
part 'ad_list_response.g.dart';

/// Paginated ad list response
@Freezed()
abstract class AdListResponse with _$AdListResponse {
  const factory AdListResponse({
    /// List of ads
    required List<AdDto> ads,

    /// Total number of ads
    required int total,

    /// Current page number
    required int page,

    /// Page size
    required int size,
  }) = _AdListResponse;

  factory AdListResponse.fromJson(Map<String, Object?> json) =>
      _$AdListResponseFromJson(json);
}
