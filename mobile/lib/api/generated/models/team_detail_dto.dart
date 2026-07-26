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

    /// Whether visibility is editable by team admins
    required bool visibilityEditable,

    /// Whether any domain user can join this team
    required bool joinable,

    /// Whether team admins can add members
    required bool addMemberAllowed,

    /// Number of team members
    required int memberCount,

    /// Rides of this team dated in the future that the caller may open. Follows the same visibility rules as the ride listing, so it never announces more than the caller can actually see.
    required int upcomingRideCount,

    /// Routes of this team the caller may open, under the same visibility rules as the route listing.
    required int routeCount,

    /// Team creation timestamp
    required String createdAt,

    /// Plain-text opening of the about page, flattened and cut on a word boundary at about 200 characters. Null when the about page holds no text. Lets a team card render its two lines without parsing the markdown client-side.
    String? excerpt,

    /// URL template of the team's logo, when it has one. Same picture as about.assets.logo, hoisted so a card does not have to walk the asset inventory to find it.
    String? logoUrl,

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
