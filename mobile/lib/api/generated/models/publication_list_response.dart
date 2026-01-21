// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'publication_dto.dart';

part 'publication_list_response.freezed.dart';
part 'publication_list_response.g.dart';

/// Paginated publication list response
@Freezed()
abstract class PublicationListResponse with _$PublicationListResponse {
  const factory PublicationListResponse({
    /// List of publications
    required List<PublicationDto> publications,

    /// Total number of publications
    required int total,

    /// Current page number
    required int page,

    /// Page size
    required int size,
  }) = _PublicationListResponse;

  factory PublicationListResponse.fromJson(Map<String, Object?> json) =>
      _$PublicationListResponseFromJson(json);
}
