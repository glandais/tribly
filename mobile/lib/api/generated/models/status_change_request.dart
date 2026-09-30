// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'status.dart';

part 'status_change_request.freezed.dart';
part 'status_change_request.g.dart';

/// Status change request
@Freezed()
abstract class StatusChangeRequest with _$StatusChangeRequest {
  const factory StatusChangeRequest({
    /// New status
    required String status,
  }) = _StatusChangeRequest;

  factory StatusChangeRequest.fromJson(Map<String, Object?> json) =>
      _$StatusChangeRequestFromJson(json);
}
