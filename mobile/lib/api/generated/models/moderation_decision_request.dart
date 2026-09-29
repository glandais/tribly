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

    /// Slug of the team the reports were filed in — the item's teamSlug. A member can be reported in several teams, one queue item per team: on the platform queue, this decides that item only. Omitted there, the decision applies to the target's open reports in every team. Ignored by a team's queue, whose path names the team.
    String? teamSlug,
  }) = _ModerationDecisionRequest;

  factory ModerationDecisionRequest.fromJson(Map<String, Object?> json) =>
      _$ModerationDecisionRequestFromJson(json);
}
