// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'team_webhook_request.freezed.dart';
part 'team_webhook_request.g.dart';

/// Create or change a team's webhook
@Freezed()
abstract class TeamWebhookRequest with _$TeamWebhookRequest {
  const factory TeamWebhookRequest({
    /// Language the messages are written in
    required String language,

    /// Whether announcements are posted
    required bool enabled,

    /// The https URL to post to. Omit it to keep the one already set — the API never returns it in full. Required when the team has no webhook yet.
    String? url,
  }) = _TeamWebhookRequest;

  factory TeamWebhookRequest.fromJson(Map<String, Object?> json) =>
      _$TeamWebhookRequestFromJson(json);
}
