// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'media_dto.dart';
import 'place_detail_dto.dart';
import 'public_user_dto.dart';
import 'publication_dto.dart';
import 'publication_type.dart';
import 'ride_group_dto.dart';
import 'status.dart';
import 'team_publication_dto.dart';
import 'visibility.dart';

part 'ride_dto.freezed.dart';
part 'ride_dto.g.dart';

/// Ride summary data
@Freezed()
abstract class RideDto with _$RideDto {
  const factory RideDto({
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

    /// Publication date/time
    required String dateTime,

    /// Publication status
    required String status,

    /// Visibility level
    required String visibility,

    /// Number of participants
    required int participantCount,

    /// Number of groups
    required int groupCount,

    /// Ride groups
    required List<RideGroupDto> groups,

    /// Preview of first participants (max 5)
    required List<PublicUserDto> topParticipants,

    /// Whether the ride is soft-deleted
    required bool deleted,

    /// Whether the current user is registered in one of this ride's groups. False if anonymous.
    required bool registered,

    /// Whether every group of the ride has reached its capacity. False when the ride has no group, or when at least one group has no maxParticipants.
    required bool full,

    /// Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter.
    String? excerpt,

    /// Publication timestamp
    String? publishAt,

    /// Creation timestamp
    String? createdAt,

    /// Route slug
    String? routeSlug,

    /// Start place
    PlaceDetailDto? startPlace,

    /// End place
    PlaceDetailDto? endPlace,

    /// Thumbnail URL (light)
    String? thumbnailLightUrl,

    /// Thumbnail URL (dark)
    String? thumbnailDarkUrl,

    /// The one thumbnail to show when the client does not theme its cards: the light variant if there is one, else the dark one. Saves a compact row from carrying media.assets just to find a picture.
    String? thumbnailUrl,

    /// ID (TSID) of the group the current user joined, null if not registered
    String? registeredGroupId,

    /// Number of comments, replies included. Absent when the caller may not read the comments of this ride — comments are members-only, so an outsider is told nothing, not even zero.
    int? commentCount,
  }) = _RideDto;

  factory RideDto.fromJson(Map<String, Object?> json) =>
      _$RideDtoFromJson(json);
}
