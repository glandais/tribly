// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'publication_type.dart';

part 'route_usage_dto.freezed.dart';
part 'route_usage_dto.g.dart';

/// A ride or trip that uses a route
@Freezed()
abstract class RouteUsageDto with _$RouteUsageDto {
  const factory RouteUsageDto({
    /// Publication type (RIDE or TRIP)
    required String type,

    /// Publication URL slug
    required String slug,

    /// Publication name
    required String name,

    /// Publication date/time
    required String dateTime,

    /// IANA zone of the publication, as RideDto.timezone / TripDto.timezone: dateTime and endDate are rendezvous in it.
    required String timezone,

    /// Slug of the team owning the publication
    required String teamSlug,

    /// Whether the publication references the route directly (not only via a child)
    required bool referencedDirectly,

    /// Names of the ride groups or trip stages that reference the route, if any
    required List<String> viaChildNames,

    /// For a trip, the date of its last stage — the same value as TripDto.endDate. Null for a ride, and for a trip with no stage, which lasts a day.
    String? endDate,
  }) = _RouteUsageDto;

  factory RouteUsageDto.fromJson(Map<String, Object?> json) =>
      _$RouteUsageDtoFromJson(json);
}
