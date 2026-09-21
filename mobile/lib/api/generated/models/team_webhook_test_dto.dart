// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'team_webhook_test_dto.freezed.dart';
part 'team_webhook_test_dto.g.dart';

/// Outcome of a test message sent to the team's webhook
@Freezed()
abstract class TeamWebhookTestDto with _$TeamWebhookTestDto {
  const factory TeamWebhookTestDto({
    /// Whether the endpoint accepted it (2xx)
    required bool success,

    /// HTTP status the endpoint answered, if it answered
    int? statusCode,

    /// Why it failed, when it did
    String? error,
  }) = _TeamWebhookTestDto;

  factory TeamWebhookTestDto.fromJson(Map<String, Object?> json) =>
      _$TeamWebhookTestDtoFromJson(json);
}
