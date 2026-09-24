// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'moderation_item_dto.dart';

part 'moderation_queue_response.freezed.dart';
part 'moderation_queue_response.g.dart';

/// A moderation queue: every open target, or the 100 most recently decided ones, one item per target
@Freezed()
abstract class ModerationQueueResponse with _$ModerationQueueResponse {
  const factory ModerationQueueResponse({
    /// One item per reported target
    required List<ModerationItemDto> items,

    /// How many items
    required int total,
  }) = _ModerationQueueResponse;

  factory ModerationQueueResponse.fromJson(Map<String, Object?> json) =>
      _$ModerationQueueResponseFromJson(json);
}
