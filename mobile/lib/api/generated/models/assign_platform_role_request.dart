// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'platform_role.dart';

part 'assign_platform_role_request.freezed.dart';
part 'assign_platform_role_request.g.dart';

/// Request to assign or remove platform role
@Freezed()
abstract class AssignPlatformRoleRequest with _$AssignPlatformRoleRequest {
  const factory AssignPlatformRoleRequest({
    /// Platform role to assign (null to remove)
    String? role,
  }) = _AssignPlatformRoleRequest;

  factory AssignPlatformRoleRequest.fromJson(Map<String, Object?> json) =>
      _$AssignPlatformRoleRequestFromJson(json);
}
