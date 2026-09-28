// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'web_push_config_dto.freezed.dart';
part 'web_push_config_dto.g.dart';

/// Firebase web configuration, for push notifications in the browser
@Freezed()
abstract class WebPushConfigDto with _$WebPushConfigDto {
  const factory WebPushConfigDto({
    /// Firebase web API key
    required String apiKey,

    /// Firebase project id, the one the server sends through
    required String projectId,

    /// Firebase web app id
    required String appId,

    /// FCM sender id (the project number)
    required String messagingSenderId,

    /// Public VAPID key of the project's web push certificate
    required String vapidKey,
  }) = _WebPushConfigDto;

  factory WebPushConfigDto.fromJson(Map<String, Object?> json) =>
      _$WebPushConfigDtoFromJson(json);
}
