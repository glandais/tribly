// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'push_device_unregistration.freezed.dart';
part 'push_device_unregistration.g.dart';

/// A device to stop sending push notifications to
@Freezed()
abstract class PushDeviceUnregistration with _$PushDeviceUnregistration {
  const factory PushDeviceUnregistration({
    /// The FCM registration token to drop
    required String token,
  }) = _PushDeviceUnregistration;

  factory PushDeviceUnregistration.fromJson(Map<String, Object?> json) =>
      _$PushDeviceUnregistrationFromJson(json);
}
