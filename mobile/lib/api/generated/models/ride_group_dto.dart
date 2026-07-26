// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'local_time.dart';
import 'public_user_dto.dart';

part 'ride_group_dto.freezed.dart';
part 'ride_group_dto.g.dart';

/// Ride group information
@Freezed()
abstract class RideGroupDto with _$RideGroupDto {
  const factory RideGroupDto({
    /// Group ID (TSID)
    required String id,

    /// Group name
    required String name,

    /// Current number of participants
    required int countParticipants,

    /// Participants, empty if not access
    required List<PublicUserDto> participants,

    /// Sort order
    required int sortOrder,

    /// Whether the current user is registered in THIS group. False if anonymous.
    required bool registered,

    /// Whether the group has reached maxParticipants. False when maxParticipants is not set.
    required bool full,
    LocalTime? time,

    /// Route slug
    String? routeSlug,

    /// Average speed in km/h
    double? averageSpeed,

    /// Maximum participants
    int? maxParticipants,

    /// Distance in meters of the group route, if it has one
    double? distance,

    /// Total elevation gain in meters of the group route, if it has one
    double? elevationGain,

    /// The member who leads this group, when one is designated. Null means no leader was designated — render nothing rather than falling back on the ride's creator, who is the same person on every group of the ride.
    PublicUserDto? leader,
  }) = _RideGroupDto;

  factory RideGroupDto.fromJson(Map<String, Object?> json) =>
      _$RideGroupDtoFromJson(json);
}
