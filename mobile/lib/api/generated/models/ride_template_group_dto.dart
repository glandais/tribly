// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'local_time.dart';

part 'ride_template_group_dto.freezed.dart';
part 'ride_template_group_dto.g.dart';

/// Ride template group information
@Freezed()
abstract class RideTemplateGroupDto with _$RideTemplateGroupDto {
  const factory RideTemplateGroupDto({
    /// Group ID (TSID)
    required String id,

    /// Group name
    required String name,

    /// Sort order
    required int sortOrder,
    LocalTime? time,

    /// Average speed in km/h
    double? averageSpeed,

    /// Maximum participants
    int? maxParticipants,
  }) = _RideTemplateGroupDto;

  factory RideTemplateGroupDto.fromJson(Map<String, Object?> json) =>
      _$RideTemplateGroupDtoFromJson(json);
}
