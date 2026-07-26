// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'media_dto.dart';
import 'place_detail_dto.dart';
import 'route_dto.dart';

part 'trip_stage_dto.freezed.dart';
part 'trip_stage_dto.g.dart';

/// Trip stage information
@Freezed()
abstract class TripStageDto with _$TripStageDto {
  const factory TripStageDto({
    /// Stage ID (TSID)
    required String id,

    /// Stage slug
    required String slug,

    /// Stage name
    required String name,

    /// Stage date/time
    required String dateTime,

    /// Stage media
    required MediaDto media,

    /// Sort order
    required int sortOrder,

    /// Position of this stage among the trip's live stages, 1-based — the 'Day 2' of a stage header. Unlike sortOrder, which is a persisted rank that may have gaps, this is a rank a client can print.
    required int stageIndex,

    /// How many live stages the trip has — the '/ 5' of 'Day 2 / 5'.
    required int stageCount,

    /// Route
    RouteDto? route,

    /// Start place
    PlaceDetailDto? startPlace,

    /// End place
    PlaceDetailDto? endPlace,
  }) = _TripStageDto;

  factory TripStageDto.fromJson(Map<String, Object?> json) =>
      _$TripStageDtoFromJson(json);
}
