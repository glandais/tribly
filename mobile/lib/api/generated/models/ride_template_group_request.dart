// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'local_time.dart';

part 'ride_template_group_request.freezed.dart';
part 'ride_template_group_request.g.dart';

/// Ride template group request
@Freezed()
abstract class RideTemplateGroupRequest with _$RideTemplateGroupRequest {
  const factory RideTemplateGroupRequest({
    /// Group name
    required String name,

    /// Group ID (TSID) - only for updates
    String? id,
    LocalTime? time,

    /// Average speed in km/h
    double? averageSpeed,

    /// Maximum participants
    int? maxParticipants,
  }) = _RideTemplateGroupRequest;

  factory RideTemplateGroupRequest.fromJson(Map<String, Object?> json) =>
      _$RideTemplateGroupRequestFromJson(json);
}
