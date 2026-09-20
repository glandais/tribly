// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'push_platform.dart';

part 'push_device_registration.freezed.dart';
part 'push_device_registration.g.dart';

/// A device to receive push notifications on
@Freezed()
abstract class PushDeviceRegistration with _$PushDeviceRegistration {
  const factory PushDeviceRegistration({
    /// The FCM registration token. Registering a token already known moves it to the current user and refreshes its last-seen date.
    required String token,

    /// The device's platform
    required String platform,

    /// A human-readable device name, for the member's own device list
    String? deviceName,

    /// The app version that registered, for support
    String? appVersion,
  }) = _PushDeviceRegistration;

  factory PushDeviceRegistration.fromJson(Map<String, Object?> json) =>
      _$PushDeviceRegistrationFromJson(json);
}
