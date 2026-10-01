// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/tag_create_request.dart';
import '../models/tag_deleted_dto.dart';
import '../models/tag_target.dart';
import '../models/tag_update_request.dart';
import '../models/tag_with_usage_dto.dart';

part 'tags_client.g.dart';

@RestApi()
abstract class TagsClient {
  factory TagsClient(Dio dio, {String? baseUrl}) = _TagsClient;

  /// List team tags.
  ///
  /// The team's tags, sorted by label, each with its usage count. Open to whoever can see the team.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [type] - Only the tags of this kind of content; all kinds when absent.
  @GET('/api/teams/{teamSlug}/tags')
  Future<List<TagWithUsageDto>> listTeamTags({
    @Path('teamSlug') required String teamSlug,
    @Query('type') TagTarget? type,
  });

  /// Create a team tag.
  ///
  /// Requires team admin permissions.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/teams/{teamSlug}/tags')
  Future<TagWithUsageDto> createTeamTag({
    @Path('teamSlug') required String teamSlug,
    @Body() required TagCreateRequest body,
  });

  /// Rename or recolour a team tag.
  ///
  /// Absent fields are unchanged; the kind never changes. Requires team admin.
  ///
  /// [tagId] - Tag ID (TSID).
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PATCH('/api/teams/{teamSlug}/tags/{tagId}')
  Future<TagWithUsageDto> updateTeamTag({
    @Path('tagId') required String tagId,
    @Path('teamSlug') required String teamSlug,
    @Body() required TagUpdateRequest body,
  });

  /// Delete a team tag.
  ///
  /// Detaches the tag from every content and deletes it for good. Requires team admin.
  ///
  /// [tagId] - Tag ID (TSID).
  ///
  /// [teamSlug] - Team URL slug.
  @DELETE('/api/teams/{teamSlug}/tags/{tagId}')
  Future<TagDeletedDto> deleteTeamTag({
    @Path('tagId') required String tagId,
    @Path('teamSlug') required String teamSlug,
  });
}
