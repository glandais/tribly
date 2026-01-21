// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'unit_system.dart';

part 'update_user_request.freezed.dart';
part 'update_user_request.g.dart';

/// User profile update request
@Freezed()
abstract class UpdateUserRequest with _$UpdateUserRequest {
  const factory UpdateUserRequest({
    /// User display name
    String? displayName,

    /// Preferred unit system
    String? unitSystem,
  }) = _UpdateUserRequest;

  factory UpdateUserRequest.fromJson(Map<String, Object?> json) =>
      _$UpdateUserRequestFromJson(json);
}
