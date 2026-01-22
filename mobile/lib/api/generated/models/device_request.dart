// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'device_request.freezed.dart';
part 'device_request.g.dart';

/// Device auth request
@Freezed()
abstract class DeviceRequest with _$DeviceRequest {
  const factory DeviceRequest({
    /// Client ID (e.g., 'karoo', 'garmin')
    String? clientId,
  }) = _DeviceRequest;

  factory DeviceRequest.fromJson(Map<String, Object?> json) =>
      _$DeviceRequestFromJson(json);
}
