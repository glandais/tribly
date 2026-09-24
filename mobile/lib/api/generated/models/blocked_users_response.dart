// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'public_user_dto.dart';

part 'blocked_users_response.freezed.dart';
part 'blocked_users_response.g.dart';

/// The members the current user blocked, most recent first
@Freezed()
abstract class BlockedUsersResponse with _$BlockedUsersResponse {
  const factory BlockedUsersResponse({
    /// Blocked members
    required List<PublicUserDto> users,
  }) = _BlockedUsersResponse;

  factory BlockedUsersResponse.fromJson(Map<String, Object?> json) =>
      _$BlockedUsersResponseFromJson(json);
}
