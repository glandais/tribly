// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'client_context_dto.dart';
import 'client_error_dto.dart';
import 'client_log_entry_dto.dart';

part 'error_report_request.freezed.dart';
part 'error_report_request.g.dart';

/// An unhandled error, reported automatically by a client
@Freezed()
abstract class ErrorReportRequest with _$ErrorReportRequest {
  const factory ErrorReportRequest({
    /// Client, device and screen
    required ClientContextDto context,

    /// The error
    required ClientErrorDto error,

    /// The client's recent log, oldest first
    List<ClientLogEntryDto>? logs,
  }) = _ErrorReportRequest;

  factory ErrorReportRequest.fromJson(Map<String, Object?> json) =>
      _$ErrorReportRequestFromJson(json);
}
