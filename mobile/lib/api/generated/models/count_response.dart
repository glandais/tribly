// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'count_response.freezed.dart';
part 'count_response.g.dart';

/// Number of items matching a filter set
@Freezed()
abstract class CountResponse with _$CountResponse {
  const factory CountResponse({
    /// Total number of matching items
    required int total,
  }) = _CountResponse;

  factory CountResponse.fromJson(Map<String, Object?> json) =>
      _$CountResponseFromJson(json);
}
