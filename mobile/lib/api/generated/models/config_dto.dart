// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'config_dto.freezed.dart';
part 'config_dto.g.dart';

/// Application configuration
@Freezed()
abstract class ConfigDto with _$ConfigDto {
  const factory ConfigDto({
    /// WebAuthn Relying Party ID (domain)
    required String webAuthnRpId,

    /// Application name
    required String appName,

    /// Single team mode - team creation disabled
    required bool singleTeam,
  }) = _ConfigDto;

  factory ConfigDto.fromJson(Map<String, Object?> json) =>
      _$ConfigDtoFromJson(json);
}
