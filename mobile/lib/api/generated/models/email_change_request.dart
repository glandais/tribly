// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'email_change_request.freezed.dart';
part 'email_change_request.g.dart';

/// Request to set/change the account's real email address
@Freezed()
abstract class EmailChangeRequest with _$EmailChangeRequest {
  const factory EmailChangeRequest({
    /// New email address
    required String email,
  }) = _EmailChangeRequest;

  factory EmailChangeRequest.fromJson(Map<String, Object?> json) =>
      _$EmailChangeRequestFromJson(json);
}
