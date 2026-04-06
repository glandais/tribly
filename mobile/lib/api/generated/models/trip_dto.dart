// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'media_dto.dart';
import 'public_user_dto.dart';
import 'publication_dto.dart';
import 'publication_type.dart';
import 'status.dart';
import 'team_publication_dto.dart';
import 'trip_stage_dto.dart';
import 'visibility.dart';

part 'trip_dto.freezed.dart';
part 'trip_dto.g.dart';

/// Trip data
@Freezed()
abstract class TripDto with _$TripDto {
  const factory TripDto({
    /// Type
    required String type,

    /// Team
    required TeamPublicationDto team,

    /// Publication ID (TSID)
    required String id,

    /// Publication URL slug
    required String slug,

    /// Publication name
    required String name,

    /// Publication media
    required MediaDto media,

    /// Trip start date/time
    required String dateTime,

    /// Publication status
    required String status,

    /// Visibility level
    required String visibility,

    /// Number of participants
    required int participantCount,

    /// Number of stages
    required int stageCount,

    /// Trip stages
    required List<TripStageDto> stages,

    /// Trip participants
    required List<PublicUserDto> participants,

    /// Whether the trip is soft-deleted
    required bool deleted,

    /// Publication timestamp
    String? publishAt,

    /// Creation timestamp
    String? createdAt,

    /// Route slug
    String? routeSlug,

    /// Thumbnail URL (light)
    String? thumbnailLightUrl,

    /// Thumbnail URL (dark)
    String? thumbnailDarkUrl,
  }) = _TripDto;

  factory TripDto.fromJson(Map<String, Object?> json) =>
      _$TripDtoFromJson(json);
}
