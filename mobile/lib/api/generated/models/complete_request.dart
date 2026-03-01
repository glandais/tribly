// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

part 'complete_request.freezed.dart';
part 'complete_request.g.dart';

/// Complete device authorization request
@Freezed()
abstract class CompleteRequest with _$CompleteRequest {
  const factory CompleteRequest({
    /// User code from device display
    required String userCode,
  }) = _CompleteRequest;

  factory CompleteRequest.fromJson(Map<String, Object?> json) =>
      _$CompleteRequestFromJson(json);
}
