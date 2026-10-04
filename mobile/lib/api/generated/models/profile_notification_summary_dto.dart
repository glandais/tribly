// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'notification_channel.dart';

part 'profile_notification_summary_dto.freezed.dart';
part 'profile_notification_summary_dto.g.dart';

/// Where the current user's notifications go, summed up
@Freezed()
abstract class ProfileNotificationSummaryDto
    with _$ProfileNotificationSummaryDto {
  const factory ProfileNotificationSummaryDto({
    /// Channels that can be configured on this server, in display order
    required List<NotificationChannel> channels,

    /// Among channels, those on which at least one notification type is turned on for the user (their choices, or the defaults they never changed)
    required List<NotificationChannel> enabledChannels,

    /// Whether non-urgent e-mails are held for a daily digest. Always false when EMAIL is not among channels.
    required bool emailDigest,
  }) = _ProfileNotificationSummaryDto;

  factory ProfileNotificationSummaryDto.fromJson(Map<String, Object?> json) =>
      _$ProfileNotificationSummaryDtoFromJson(json);
}
