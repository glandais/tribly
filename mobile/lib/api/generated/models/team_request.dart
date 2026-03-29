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

    /// Team location coordinates [longitude, latitude]
    TeamRequestGeometry? geometry,
  }) = _TeamRequest;

  factory TeamRequest.fromJson(Map<String, Object?> json) =>
      _$TeamRequestFromJson(json);
}
