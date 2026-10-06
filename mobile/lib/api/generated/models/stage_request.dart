// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'event_date_time.dart';
import 'media_dto.dart';

part 'stage_request.freezed.dart';
part 'stage_request.g.dart';

/// Trip stage creation request
@Freezed()
abstract class StageRequest with _$StageRequest {
  const factory StageRequest({
    /// Stage name
    required String name,

    /// Stage date/time: a wall time without offset, read in the stage's zone (start place, else route, else the previous stage's, else the trip route's, else the team's). An instant with an offset is still tolerated.
    required String dateTime,

    /// Stage media
    required MediaDto media,

    /// Stage ID (for updates)
    String? id,

    /// Average speed in km/h
    double? averageSpeed,

    /// Route slug for this stage
    String? routeSlug,

    /// Start place ID (TSID)
    String? startPlaceId,

    /// End place ID (TSID)
    String? endPlaceId,
  }) = _StageRequest;

  factory StageRequest.fromJson(Map<String, Object?> json) =>
      _$StageRequestFromJson(json);
}
