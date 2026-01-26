// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'gps_service_type.dart';

part 'device_user_status_response.freezed.dart';
part 'device_user_status_response.g.dart';

/// Response containing user's device connection status
@Freezed()
abstract class DeviceUserStatusResponse with _$DeviceUserStatusResponse {
  const factory DeviceUserStatusResponse({
    /// List of connected GPS services
    required List<GpsServiceType> connectedGpsServices,
  }) = _DeviceUserStatusResponse;

  factory DeviceUserStatusResponse.fromJson(Map<String, Object?> json) =>
      _$DeviceUserStatusResponseFromJson(json);
}
