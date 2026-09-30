// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'deny_request.freezed.dart';
part 'deny_request.g.dart';

/// Deny device authorization request
@Freezed()
abstract class DenyRequest with _$DenyRequest {
  const factory DenyRequest({
    /// User code from device display
    required String userCode,
  }) = _DenyRequest;

  factory DenyRequest.fromJson(Map<String, Object?> json) =>
      _$DenyRequestFromJson(json);
}
