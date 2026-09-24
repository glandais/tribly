// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'moderation_action.dart';
import 'report_target_type.dart';

part 'moderation_decision_request.freezed.dart';
part 'moderation_decision_request.g.dart';

/// A moderator's decision, applied to every open report of one target
@Freezed()
abstract class ModerationDecisionRequest with _$ModerationDecisionRequest {
  const factory ModerationDecisionRequest({
    /// Type of the reported target
    required String targetType,

    /// ID (TSID) of the reported target
    required String targetId,

    /// REMOVE_CONTENT deletes the content (not allowed on a MEMBER); DISMISS keeps it and shows it again if reports had hidden it
    required String action,
  }) = _ModerationDecisionRequest;

  factory ModerationDecisionRequest.fromJson(Map<String, Object?> json) =>
      _$ModerationDecisionRequestFromJson(json);
}
