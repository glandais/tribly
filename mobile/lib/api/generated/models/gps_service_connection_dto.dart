// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'gps_service_type.dart';
import 'instant.dart';

part 'gps_service_connection_dto.freezed.dart';
part 'gps_service_connection_dto.g.dart';

/// GPS service connection information
@Freezed()
abstract class GpsServiceConnectionDto with _$GpsServiceConnectionDto {
  const factory GpsServiceConnectionDto({
    /// Service type identifier
    required String serviceType,

    /// Display name of the service
    required String displayName,

    /// When the service was connected
    required String connectedAt,
  }) = _GpsServiceConnectionDto;

  factory GpsServiceConnectionDto.fromJson(Map<String, Object?> json) =>
      _$GpsServiceConnectionDtoFromJson(json);
}
