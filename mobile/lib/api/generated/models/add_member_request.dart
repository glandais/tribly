// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'team_role.dart';

part 'add_member_request.freezed.dart';
part 'add_member_request.g.dart';

/// Request to add a member to the team
@Freezed()
abstract class AddMemberRequest with _$AddMemberRequest {
  const factory AddMemberRequest({
    /// User ID (TSID) to add
    required String userId,

    /// Role to assign (defaults to MEMBER)
    String? role,
  }) = _AddMemberRequest;

  factory AddMemberRequest.fromJson(Map<String, Object?> json) =>
      _$AddMemberRequestFromJson(json);
}
