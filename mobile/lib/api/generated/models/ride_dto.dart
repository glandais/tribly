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

    /// Route thumbnail URL (light)
    String? routeThumbnailLightUrl,

    /// Route thumbnail URL (dark)
    String? routeThumbnailDarkUrl,
  }) = _RideDto;

  factory RideDto.fromJson(Map<String, Object?> json) =>
      _$RideDtoFromJson(json);
}
