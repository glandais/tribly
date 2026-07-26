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

    /// Whether the current user is registered for this trip. False if anonymous.
    required bool registered,

    /// Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter.
    String? excerpt,

    /// Date of the last stage — the day the trip ends. Null when the trip has no stage, in which case it lasts a day and dateTime is both ends.
    String? endDate,

    /// Publication timestamp
    String? publishAt,

    /// Creation timestamp
    String? createdAt,

    /// Route slug
    String? routeSlug,

    /// Distance in metres over every stage that has a route. Null when no stage has one — an unrouted trip has no distance, which is not the same as a distance of zero.
    double? totalDistance,

    /// Elevation gain in metres over every stage that has a route. Null when no stage has one.
    double? totalElevationGain,

    /// Thumbnail URL (light)
    String? thumbnailLightUrl,

    /// Thumbnail URL (dark)
    String? thumbnailDarkUrl,

    /// The one thumbnail to show when the client does not theme its cards: the light variant if there is one, else the dark one. Saves a compact row from carrying media.assets just to find a picture.
    String? thumbnailUrl,

    /// Number of comments, replies included. Absent when the caller may not read the comments of this trip — comments are members-only, so an outsider is told nothing, not even zero.
    int? commentCount,
  }) = _TripDto;

  factory TripDto.fromJson(Map<String, Object?> json) =>
      _$TripDtoFromJson(json);
}
