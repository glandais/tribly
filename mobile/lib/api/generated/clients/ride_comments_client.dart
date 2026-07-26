// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/comment_dto.dart';
import '../models/comment_list_response.dart';
import '../models/comment_request.dart';
import '../models/sort_direction.dart';

part 'ride_comments_client.g.dart';

@RestApi()
abstract class RideCommentsClient {
  factory RideCommentsClient(Dio dio, {String? baseUrl}) = _RideCommentsClient;

  /// List ride comments.
  ///
  /// Top-level comments with their replies. Passing neither page nor size returns the whole tree, as before this endpoint took parameters; passing either paginates the top-level comments. parentId switches to listing the replies of a single comment.
  ///
  /// [entitySlug] - Ride URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [page] - Page number (0-indexed). Omit both page and size to get the whole comment tree, as before this endpoint took parameters.
  ///
  /// [parentId] - Load only the replies of this comment (TSID) instead of the top-level comments. Use it to expand one thread without re-reading the others.
  ///
  /// [size] - Page size, capped at 100 (default 20).
  ///
  /// [sort] - Sort by creation date: ASC (oldest first, default) or DESC.
  @GET('/api/teams/{teamSlug}/rides/{entitySlug}/comments')
  Future<CommentListResponse> listRideComments({
    @Path('entitySlug') required String entitySlug,
    @Path('teamSlug') required String teamSlug,
    @Query('page') int? page,
    @Query('parentId') String? parentId,
    @Query('size') int? size,
    @Query('sort') SortDirection? sort,
  });

  /// Create ride comment.
  ///
  /// [entitySlug] - Ride URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/teams/{teamSlug}/rides/{entitySlug}/comments')
  Future<CommentDto> createRideComment({
    @Path('entitySlug') required String entitySlug,
    @Path('teamSlug') required String teamSlug,
    @Body() required CommentRequest body,
  });

  /// Delete ride comment.
  ///
  /// [commentId] - Comment ID.
  ///
  /// [entitySlug] - Ride URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @DELETE('/api/teams/{teamSlug}/rides/{entitySlug}/comments/{commentId}')
  Future<void> deleteRideComment({
    @Path('commentId') required String commentId,
    @Path('entitySlug') required String entitySlug,
    @Path('teamSlug') required String teamSlug,
  });
}
