// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'client_context_dto.dart';
import 'client_error_dto.dart';
import 'client_log_entry_dto.dart';
import 'feedback_kind.dart';

part 'feedback_request.freezed.dart';
part 'feedback_request.g.dart';

/// A bug report or a suggestion written by a member
@Freezed()
abstract class FeedbackRequest with _$FeedbackRequest {
  const factory FeedbackRequest({
    /// Bug or suggestion
    required String kind,

    /// What happened, in the member's words
    required String message,

    /// Client, device and screen
    required ClientContextDto context,

    /// The unhandled error the report was opened from, if any. Links the report to the automatic error report of the same error.
    ClientErrorDto? error,

    /// The client's recent log, oldest first. Absent when the member chose not to attach technical details.
    List<ClientLogEntryDto>? logs,
  }) = _FeedbackRequest;

  factory FeedbackRequest.fromJson(Map<String, Object?> json) =>
      _$FeedbackRequestFromJson(json);
}
