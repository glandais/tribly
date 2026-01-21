// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'media_dto.dart';
import 'stage_request.dart';
import 'status.dart';
import 'visibility.dart';

part 'trip_request.freezed.dart';
part 'trip_request.g.dart';

/// Trip request
@Freezed()
abstract class TripRequest with _$TripRequest {
  const factory TripRequest({
    /// Trip name
    required String name,

    /// Trip media
    required MediaDto media,

    /// Trip start date/time
    required String dateTime,

    /// Trip status
    required String status,

    /// Visibility level
    required String visibility,

    /// Trip stages to create
    required List<StageRequest> stages,

    /// Overall route slug for the trip
    String? routeSlug,

    /// Publication timestamp (for scheduled publishing)
    String? publishAt,
  }) = _TripRequest;

  factory TripRequest.fromJson(Map<String, Object?> json) =>
      _$TripRequestFromJson(json);
}
