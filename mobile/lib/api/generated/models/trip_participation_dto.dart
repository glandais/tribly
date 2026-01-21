// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';

part 'trip_participation_dto.freezed.dart';
part 'trip_participation_dto.g.dart';

/// Trip participation information
@Freezed()
abstract class TripParticipationDto with _$TripParticipationDto {
  const factory TripParticipationDto({
    /// Participation ID (TSID)
    required String id,

    /// User ID (TSID)
    required String userId,

    /// Registration timestamp
    String? registeredAt,
  }) = _TripParticipationDto;

  factory TripParticipationDto.fromJson(Map<String, Object?> json) =>
      _$TripParticipationDtoFromJson(json);
}
