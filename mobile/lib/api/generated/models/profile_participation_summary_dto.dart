// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'publication_dto.dart';

part 'profile_participation_summary_dto.freezed.dart';
part 'profile_participation_summary_dto.g.dart';

/// The rides and trips the current user is registered to, summed up
@Freezed()
abstract class ProfileParticipationSummaryDto
    with _$ProfileParticipationSummaryDto {
  const factory ProfileParticipationSummaryDto({
    /// Outings starting from now on
    required int upcomingCount,

    /// Outings that started before now
    required int pastCount,

    /// The next outing, in the compact list view (no markdown body): empty when nothing is coming up, never more than one row
    required List<PublicationDto> next,
  }) = _ProfileParticipationSummaryDto;

  factory ProfileParticipationSummaryDto.fromJson(Map<String, Object?> json) =>
      _$ProfileParticipationSummaryDtoFromJson(json);
}
