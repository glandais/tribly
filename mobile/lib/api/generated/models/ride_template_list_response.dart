// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'ride_template_dto.dart';

part 'ride_template_list_response.freezed.dart';
part 'ride_template_list_response.g.dart';

/// Paginated ride template list response
@Freezed()
abstract class RideTemplateListResponse with _$RideTemplateListResponse {
  const factory RideTemplateListResponse({
    /// List of templates
    required List<RideTemplateDto> templates,

    /// Total number of templates
    required int total,

    /// Current page number
    required int page,

    /// Page size
    required int size,
  }) = _RideTemplateListResponse;

  factory RideTemplateListResponse.fromJson(Map<String, Object?> json) =>
      _$RideTemplateListResponseFromJson(json);
}
