// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'report_reason.dart';
import 'report_target_type.dart';

part 'report_request.freezed.dart';
part 'report_request.g.dart';

/// A report of a comment, a publication, an ad, a route or a member
@Freezed()
abstract class ReportRequest with _$ReportRequest {
  const factory ReportRequest({
    /// The team the target belongs to. Its organizers and administrators moderate the report.
    required String teamSlug,

    /// What is reported
    required String targetType,

    /// ID (TSID) of the comment, publication, ad or route — or of the user for a MEMBER
    required String targetId,

    /// Why
    required String reason,

    /// Optional free text for the moderators, up to 500 characters
    String? message,
  }) = _ReportRequest;

  factory ReportRequest.fromJson(Map<String, Object?> json) =>
      _$ReportRequestFromJson(json);
}
