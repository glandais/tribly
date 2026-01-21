// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'geo_json_point.dart';
import 'instant.dart';
import 'media_dto.dart';
import 'public_user_dto.dart';
import 'surface_type.dart';
import 'team_publication_dto.dart';
import 'track_dto.dart';
import 'visibility.dart';
import 'waypoint_dto.dart';

part 'route_detail_dto.freezed.dart';
part 'route_detail_dto.g.dart';

/// Detailed route information
@Freezed()
abstract class RouteDetailDto with _$RouteDetailDto {
  const factory RouteDetailDto({
    /// Route ID (TSID)
    required String id,

    /// Route slug
    required String slug,

    /// Team
    required TeamPublicationDto team,

    /// Route name
    required String name,

    /// Media
    required MediaDto media,

    /// Distance in meters
    required double distance,

    /// Total elevation gain in meters
    required double elevationGain,

    /// Total elevation loss in meters
    required double elevationLoss,

    /// Surface type
    required String surfaceType,

    /// Whether the route is public
    required String visibility,

    /// Creator user
    required PublicUserDto createdBy,

    /// Creation timestamp
    required String createdAt,

    /// Last update timestamp
    required String updatedAt,

    /// Tracks
    required List<TrackDto> tracks,

    /// Waypoints
    required List<WaypointDto> waypoints,
    GeoJsonPoint? start,
    GeoJsonPoint? end,
  }) = _RouteDetailDto;

  factory RouteDetailDto.fromJson(Map<String, Object?> json) =>
      _$RouteDetailDtoFromJson(json);
}
