// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'ride_template_group_request.dart';
import 'status.dart';
import 'visibility.dart';

part 'ride_template_request.freezed.dart';
part 'ride_template_request.g.dart';

/// Ride template request
@Freezed()
abstract class RideTemplateRequest with _$RideTemplateRequest {
  const factory RideTemplateRequest({
    /// Template name
    required String name,

    /// Template description (markdown)
    required String markdown,

    /// Visibility level
    required String visibility,

    /// Default status for rides created from this template
    required String status,

    /// Template groups
    required List<RideTemplateGroupRequest> groups,
  }) = _RideTemplateRequest;

  factory RideTemplateRequest.fromJson(Map<String, Object?> json) =>
      _$RideTemplateRequestFromJson(json);
}
