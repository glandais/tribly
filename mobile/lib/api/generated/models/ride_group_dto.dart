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
    LocalTime? time,

    /// Route slug
    String? routeSlug,

    /// Average speed in km/h
    double? averageSpeed,

    /// Maximum participants
    int? maxParticipants,
  }) = _RideGroupDto;

  factory RideGroupDto.fromJson(Map<String, Object?> json) =>
      _$RideGroupDtoFromJson(json);
}
