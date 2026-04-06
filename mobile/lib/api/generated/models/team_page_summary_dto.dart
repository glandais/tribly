// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'visibility.dart';

part 'team_page_summary_dto.freezed.dart';
part 'team_page_summary_dto.g.dart';

/// Team page summary for listings
@Freezed()
abstract class TeamPageSummaryDto with _$TeamPageSummaryDto {
  const factory TeamPageSummaryDto({
    /// Page ID (TSID)
    required String id,

    /// Page title
    required String title,

    /// Page URL slug
    required String slug,

    /// Visibility level
    required String visibility,

    /// Page order
    required int order,

    /// Whether the page is soft-deleted
    required bool deleted,
  }) = _TeamPageSummaryDto;

  factory TeamPageSummaryDto.fromJson(Map<String, Object?> json) =>
      _$TeamPageSummaryDtoFromJson(json);
}
