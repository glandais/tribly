// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'instant.dart';
import 'public_user_dto.dart';
import 'report_reason.dart';
import 'report_status.dart';
import 'report_target_type.dart';

part 'moderation_item_dto.freezed.dart';
part 'moderation_item_dto.g.dart';

/// One reported target in a moderation queue, with all its reports grouped
@Freezed()
abstract class ModerationItemDto with _$ModerationItemDto {
  const factory ModerationItemDto({
    /// Type of the reported target
    required String targetType,

    /// ID (TSID) of the reported target
    required String targetId,

    /// Slug of the team the reports were filed in
    required String teamSlug,

    /// Name of that team
    required String teamName,

    /// Who the moderation is about: the author of the content, or the member
    required PublicUserDto targetUser,

    /// How many reports this target gathered
    required int reportCount,

    /// The distinct reasons given
    required List<ReportReason> reasons,

    /// The non-empty free texts of the reports
    required List<String> messages,

    /// When the first report was filed
    required String firstReportedAt,

    /// When the last report was filed
    required String lastReportedAt,

    /// Whether the content is currently hidden from members, having gathered enough reports
    required bool hidden,

    /// OPEN while waiting; REMOVED or DISMISSED once decided (the latest decision)
    required String status,

    /// Name of the publication — of the commented one for a comment. Null for a member, or when the content is gone.
    String? contentName,

    /// Type of the content to open: the publication itself, or the one a comment is on (POST, RIDE, TRIP, ROUTE or AD). Null for a member, or when the content is gone.
    String? contentType,

    /// Slug of the content to open, with contentType
    String? contentSlug,

    /// The reported text as it was when first reported (comment text, name and start of the description, or member name)
    String? excerpt,

    /// Who reported. Only in the platform queue: always null in a team's queue, where reporters stay anonymous.
    List<PublicUserDto>? reporters,
  }) = _ModerationItemDto;

  factory ModerationItemDto.fromJson(Map<String, Object?> json) =>
      _$ModerationItemDtoFromJson(json);
}
