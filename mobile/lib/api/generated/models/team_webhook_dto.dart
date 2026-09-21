// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'notification_delivery_status.dart';
import 'team_webhook_kind.dart';

part 'team_webhook_dto.freezed.dart';
part 'team_webhook_dto.g.dart';

/// The team's outgoing webhook
@Freezed()
abstract class TeamWebhookDto with _$TeamWebhookDto {
  const factory TeamWebhookDto({
    /// Whether the team has a webhook at all
    required bool configured,

    /// Whether announcements are posted
    required bool enabled,

    /// The URL, masked: scheme, host and the last characters only
    String? maskedUrl,

    /// Message format, read from the URL
    String? kind,

    /// Language the messages are written in
    String? language,

    /// Outcome of the latest attempt
    String? lastStatus,

    /// Why the latest attempt failed, when it did
    String? lastError,

    /// When the latest attempt was made
    String? lastAttemptAt,
  }) = _TeamWebhookDto;

  factory TeamWebhookDto.fromJson(Map<String, Object?> json) =>
      _$TeamWebhookDtoFromJson(json);
}
