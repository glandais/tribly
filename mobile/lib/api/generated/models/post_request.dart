// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:freezed_annotation/freezed_annotation.dart';

import 'event_date_time.dart';
import 'media_dto.dart';
import 'status.dart';
import 'visibility.dart';

part 'post_request.freezed.dart';
part 'post_request.g.dart';

/// Post request
@Freezed()
abstract class PostRequest with _$PostRequest {
  const factory PostRequest({
    /// Post name
    required String name,

    /// Post description
    required MediaDto media,

    /// Post date/time: a wall time without offset, read in the team's zone. An instant with an offset is still tolerated.
    required String dateTime,

    /// Post status
    required String status,

    /// Visibility level
    required String visibility,

    /// Publication time (for scheduled publishing), a wall time in the team's zone like dateTime.
    String? publishAt,

    /// Sign the post as the team rather than as its author. Omitted: on creation, the team's postsAsTeamByDefault; on an update, left as it is.
    bool? signedAsTeam,

    /// IDs (TSID) of the team's POST tags the post carries, replacing the whole set — at most 10, each a tag of this team and of kind POST, else 400 (TAG_INVALID, TOO_MANY_TAGS). An empty list removes them all. Omitted: none on a creation, left as they are on an update.
    List<String>? tagIds,
  }) = _PostRequest;

  factory PostRequest.fromJson(Map<String, Object?> json) =>
      _$PostRequestFromJson(json);
}
