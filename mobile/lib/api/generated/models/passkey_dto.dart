// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';

part 'passkey_dto.freezed.dart';
part 'passkey_dto.g.dart';

/// Passkey information
@Freezed()
abstract class PasskeyDto with _$PasskeyDto {
  const factory PasskeyDto({
    /// Passkey ID
    String? id,

    /// Credential ID (base64url)
    String? credentialId,

    /// Device name
    String? deviceName,

    /// Transport methods
    List<String>? transports,

    /// Created timestamp
    String? createdAt,

    /// Last used timestamp
    String? lastUsedAt,
  }) = _PasskeyDto;

  factory PasskeyDto.fromJson(Map<String, Object?> json) =>
      _$PasskeyDtoFromJson(json);
}
