// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'media_dto.dart';
import 'surface_type.dart';
import 'team_publication_dto.dart';
import 'visibility.dart';

part 'route_dto.freezed.dart';
part 'route_dto.g.dart';

/// Route summary data
@Freezed()
abstract class RouteDto with _$RouteDto {
  const factory RouteDto({
    /// Route ID (TSID)
    required String id,

    /// Route slug
    required String slug,

    /// Team
    required TeamPublicationDto team,

    /// Route name
    required String name,

    /// Route description
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

    /// Creation timestamp
    required String createdAt,

    /// Whether the route is soft-deleted
    required bool deleted,
  }) = _RouteDto;

  factory RouteDto.fromJson(Map<String, Object?> json) =>
      _$RouteDtoFromJson(json);
}
