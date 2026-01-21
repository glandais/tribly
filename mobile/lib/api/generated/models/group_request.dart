// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'local_time.dart';

part 'group_request.freezed.dart';
part 'group_request.g.dart';

/// Ride group creation request
@Freezed()
abstract class GroupRequest with _$GroupRequest {
  const factory GroupRequest({
    /// Group name
    required String name,

    /// id
    String? id,
    LocalTime? time,

    /// Average speed in km/h
    double? averageSpeed,

    /// Maximum participants
    int? maxParticipants,

    /// Route slug for this group
    String? routeSlug,
  }) = _GroupRequest;

  factory GroupRequest.fromJson(Map<String, Object?> json) =>
      _$GroupRequestFromJson(json);
}
