// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'media_dto.dart';
import 'team_request_geometry.dart';
import 'visibility.dart';

part 'team_request.freezed.dart';
part 'team_request.g.dart';

/// Team creation request
@Freezed()
abstract class TeamRequest with _$TeamRequest {
  const factory TeamRequest({
    /// Team name
    required String name,

    /// Media
    required MediaDto media,

    /// Team visibility
    required String visibility,

    /// Trips enabled for team
    required bool enableTrips,

    /// Ads enabled for team
    required bool enableAds,

    /// Posts enabled for team
    required bool enablePosts,

    /// Rides enabled for team
    required bool enableRides,

    /// Routes enabled for team
    required bool enableRoutes,

    /// Member directory readable by every member, not just administrators. Organisers always see the directory; what this flag adds for them is the role and join date of each member.
    required bool enableMemberDirectory,

    /// Whether a new post starts signed by the team rather than by its author. Omitted: left as it is (on for a new team).
    bool? postsAsTeamByDefault,

    /// Team location coordinates [longitude, latitude]
    TeamRequestGeometry? geometry,

    /// The team's IANA zone (Europe/Paris): the one its rides, trips and posts fall back on when no place locates them. Validated against the JDK's timezone database, else 400 INVALID_TIMEZONE. Omitted: Europe/Paris on a creation, left as it is on an update. Changing it keeps the wall time of the upcoming rides, trips and posts that no place locates.
    String? timezone,
  }) = _TeamRequest;

  factory TeamRequest.fromJson(Map<String, Object?> json) =>
      _$TeamRequestFromJson(json);
}
