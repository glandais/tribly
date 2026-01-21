// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'team_role.dart';

part 'update_member_role_request.freezed.dart';
part 'update_member_role_request.g.dart';

/// Request to update a member's role
@Freezed()
abstract class UpdateMemberRoleRequest with _$UpdateMemberRoleRequest {
  const factory UpdateMemberRoleRequest({
    /// New role
    required String role,
  }) = _UpdateMemberRoleRequest;

  factory UpdateMemberRoleRequest.fromJson(Map<String, Object?> json) =>
      _$UpdateMemberRoleRequestFromJson(json);
}
