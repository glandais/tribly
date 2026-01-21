// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'member_dto.dart';

part 'member_list_response.freezed.dart';
part 'member_list_response.g.dart';

/// Paginated member list response
@Freezed()
abstract class MemberListResponse with _$MemberListResponse {
  const factory MemberListResponse({
    /// List of members
    required List<MemberDto> members,

    /// Total number of members
    required int total,

    /// Current page number
    required int page,

    /// Page size
    required int size,
  }) = _MemberListResponse;

  factory MemberListResponse.fromJson(Map<String, Object?> json) =>
      _$MemberListResponseFromJson(json);
}
