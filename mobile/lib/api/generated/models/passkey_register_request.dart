// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'passkey_register_request.freezed.dart';
part 'passkey_register_request.g.dart';

/// Request to register a new passkey with device name
@Freezed()
abstract class PasskeyRegisterRequest with _$PasskeyRegisterRequest {
  const factory PasskeyRegisterRequest({
    /// Optional device name for this passkey
    String? deviceName,
  }) = _PasskeyRegisterRequest;

  factory PasskeyRegisterRequest.fromJson(Map<String, Object?> json) =>
      _$PasskeyRegisterRequestFromJson(json);
}
