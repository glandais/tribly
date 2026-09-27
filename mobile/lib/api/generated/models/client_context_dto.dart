// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'client_platform.dart';

part 'client_context_dto.freezed.dart';
part 'client_context_dto.g.dart';

/// Where a feedback or an error report comes from: client, device, screen
@Freezed()
abstract class ClientContextDto with _$ClientContextDto {
  const factory ClientContextDto({
    /// The client
    required String platform,

    /// Version of the client, e.g. 1.0.0 or a git commit
    required String appVersion,

    /// Build number of a mobile client
    String? buildNumber,

    /// OS name and version, e.g. Android 15
    String? osVersion,

    /// Device model
    String? device,

    /// Browser user agent
    String? userAgent,

    /// Path of the current page or screen, without its query string
    String? route,

    /// UI language, e.g. fr
    String? locale,

    /// IANA time zone, e.g. Europe/Paris
    String? timezone,

    /// Slug of the team being browsed, if any
    String? teamSlug,
  }) = _ClientContextDto;

  factory ClientContextDto.fromJson(Map<String, Object?> json) =>
      _$ClientContextDtoFromJson(json);
}
