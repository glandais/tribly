// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/comment_dto.dart';
import '../models/comment_list_response.dart';
import '../models/comment_request.dart';

part 'post_comments_client.g.dart';

@RestApi()
abstract class PostCommentsClient {
  factory PostCommentsClient(Dio dio, {String? baseUrl}) = _PostCommentsClient;

  /// List post comments.
  ///
  /// [entitySlug] - Post URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/teams/{teamSlug}/posts/{entitySlug}/comments')
  Future<CommentListResponse> listPostComments({
    @Path('entitySlug') required String entitySlug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Create post comment.
  ///
  /// [entitySlug] - Post URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/teams/{teamSlug}/posts/{entitySlug}/comments')
  Future<CommentDto> createPostComment({
    @Path('entitySlug') required String entitySlug,
    @Path('teamSlug') required String teamSlug,
    @Body() required CommentRequest body,
  });

  /// Delete post comment.
  ///
  /// [commentId] - Comment ID.
  ///
  /// [entitySlug] - Post URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @DELETE('/api/teams/{teamSlug}/posts/{entitySlug}/comments/{commentId}')
  Future<void> deletePostComment({
    @Path('commentId') required String commentId,
    @Path('entitySlug') required String entitySlug,
    @Path('teamSlug') required String teamSlug,
  });
}
