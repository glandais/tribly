// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'group_request.dart';
import 'instant.dart';
import 'media_dto.dart';
import 'status.dart';
import 'visibility.dart';

part 'ride_request.freezed.dart';
part 'ride_request.g.dart';

/// Ride request
@Freezed()
abstract class RideRequest with _$RideRequest {
  const factory RideRequest({
    /// Ride name
    required String name,

    /// Ride media
    required MediaDto media,

    /// Ride date/time
    required String dateTime,

    /// Ride status
    required String status,

    /// Visibility level
    required String visibility,

    /// Ride groups to create
    required List<GroupRequest> groups,

    /// Route slug
    String? routeSlug,

    /// Start place ID (TSID)
    String? startPlaceId,

    /// End place ID (TSID)
    String? endPlaceId,

    /// Publication timestamp (for scheduled publishing)
    String? publishAt,
  }) = _RideRequest;

  factory RideRequest.fromJson(Map<String, Object?> json) =>
      _$RideRequestFromJson(json);
}
