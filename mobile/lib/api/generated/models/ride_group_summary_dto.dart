// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'local_time.dart';

part 'ride_group_summary_dto.freezed.dart';
part 'ride_group_summary_dto.g.dart';

/// One group of a ride, as a list row shows it: name, pace and fill. Same figures as the matching entry of the detail's groups, without the participants nor the leader.
@Freezed()
abstract class RideGroupSummaryDto with _$RideGroupSummaryDto {
  const factory RideGroupSummaryDto({
    /// Group ID (TSID)
    required String id,

    /// Group name
    required String name,

    /// When the group leaves, as RideGroupDto.startAt. Replaces time, kept for the clients that still read it.
    required String startAt,

    /// Current number of participants
    required int countParticipants,

    /// Whether the group has reached maxParticipants. False when maxParticipants is not set.
    required bool full,

    /// Sort order
    required int sortOrder,

    /// Start time of the group, when it differs from the ride's
    String? time,

    /// Average speed in km/h
    double? averageSpeed,

    /// Maximum participants, null when the group is uncapped
    int? maxParticipants,

    /// Slug of the group route, if it has one
    String? routeSlug,

    /// Distance in meters of the group route, if it has one
    double? distance,

    /// Total elevation gain in meters of the group route, if it has one
    double? elevationGain,
  }) = _RideGroupSummaryDto;

  factory RideGroupSummaryDto.fromJson(Map<String, Object?> json) =>
      _$RideGroupSummaryDtoFromJson(json);
}
