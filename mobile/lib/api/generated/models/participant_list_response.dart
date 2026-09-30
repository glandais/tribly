// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'public_user_dto.dart';

part 'participant_list_response.freezed.dart';
part 'participant_list_response.g.dart';

/// Paginated list of the people registered to a ride, a ride group or a trip
@Freezed()
abstract class ParticipantListResponse with _$ParticipantListResponse {
  const factory ParticipantListResponse({
    /// Participants of this page, in registration order (earliest first)
    required List<PublicUserDto> participants,

    /// Number of participants matching the search, over every page — the M of « N of M »
    required int total,

    /// Current page number (0-based)
    required int page,

    /// Page size actually applied
    required int size,
  }) = _ParticipantListResponse;

  factory ParticipantListResponse.fromJson(Map<String, Object?> json) =>
      _$ParticipantListResponseFromJson(json);
}
