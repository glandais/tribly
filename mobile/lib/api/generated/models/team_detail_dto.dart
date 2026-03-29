// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'media_dto.dart';
import 'team_detail_dto_geometry.dart';
import 'team_page_summary_dto.dart';
import 'team_role.dart';
import 'visibility.dart';

part 'team_detail_dto.freezed.dart';
part 'team_detail_dto.g.dart';

/// Detailed team information
@Freezed()
abstract class TeamDetailDto with _$TeamDetailDto {
  const factory TeamDetailDto({
    /// Team ID (TSID)
    required String id,

    /// Team name
    required String name,

    /// Team URL slug
    required String slug,

    /// About page content
    required MediaDto about,

    /// Whether the team is public
    required String visibility,

    /// Trips enabled
    required bool enableTrips,

    /// Ads enabled
    required bool enableAds,

    /// Posts enabled
    required bool enablePosts,

    /// Rides enabled
    required bool enableRides,

    /// Routes enabled
    required bool enableRoutes,

    /// Number of team members
    required int memberCount,

    /// Team creation timestamp
    required String createdAt,

    /// Additional team pages
    List<TeamPageSummaryDto>? pages,

    /// Current user's role (null if not a member)
    String? role,

    /// Team location coordinates [longitude, latitude]
    TeamDetailDtoGeometry? geometry,
  }) = _TeamDetailDto;

  factory TeamDetailDto.fromJson(Map<String, Object?> json) =>
      _$TeamDetailDtoFromJson(json);
}
