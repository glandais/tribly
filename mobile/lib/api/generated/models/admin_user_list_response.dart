// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'admin_user_dto.dart';

part 'admin_user_list_response.freezed.dart';
part 'admin_user_list_response.g.dart';

/// Paginated admin user list response
@Freezed()
abstract class AdminUserListResponse with _$AdminUserListResponse {
  const factory AdminUserListResponse({
    /// List of users
    required List<AdminUserDto> users,

    /// Total number of users
    required int total,

    /// Current page number
    required int page,

    /// Page size
    required int size,
  }) = _AdminUserListResponse;

  factory AdminUserListResponse.fromJson(Map<String, Object?> json) =>
      _$AdminUserListResponseFromJson(json);
}
