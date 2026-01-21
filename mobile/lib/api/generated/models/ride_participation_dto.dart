// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';

part 'ride_participation_dto.freezed.dart';
part 'ride_participation_dto.g.dart';

/// Ride participation information
@Freezed()
abstract class RideParticipationDto with _$RideParticipationDto {
  const factory RideParticipationDto({
    /// Participation ID (TSID)
    required String id,

    /// User ID (TSID)
    required String userId,

    /// Registration timestamp
    String? registeredAt,
  }) = _RideParticipationDto;

  factory RideParticipationDto.fromJson(Map<String, Object?> json) =>
      _$RideParticipationDtoFromJson(json);
}
