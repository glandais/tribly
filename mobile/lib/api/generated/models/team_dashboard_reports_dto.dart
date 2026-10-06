// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'report_reason.dart';
import 'report_target_type.dart';

part 'team_dashboard_reports_dto.freezed.dart';
part 'team_dashboard_reports_dto.g.dart';

/// The reports tile of a team dashboard. The full queue is GET /api/teams/{teamSlug}/reports?status=OPEN; reporters are never named.
@Freezed()
abstract class TeamDashboardReportsDto with _$TeamDashboardReportsDto {
  const factory TeamDashboardReportsDto({
    /// Reported targets waiting for a decision — the number of items of the open queue
    required int openCount,

    /// Reason of the most recent open report, null when none
    String? latestReason,

    /// What the most recent open report is about, null when none
    String? latestTargetType,

    /// The reported text as it was when the most recent open report was filed, null when none
    String? latestExcerpt,

    /// When the most recent open report was filed, null when none
    String? latestReportedAt,
  }) = _TeamDashboardReportsDto;

  factory TeamDashboardReportsDto.fromJson(Map<String, Object?> json) =>
      _$TeamDashboardReportsDtoFromJson(json);
}
