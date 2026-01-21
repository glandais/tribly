// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/comment_dto.dart';
import '../models/comment_list_response.dart';
import '../models/comment_request.dart';

part 'route_comments_client.g.dart';

@RestApi()
abstract class RouteCommentsClient {
  factory RouteCommentsClient(Dio dio, {String? baseUrl}) =
      _RouteCommentsClient;

  /// List route comments.
  ///
  /// [entitySlug] - Route URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/teams/{teamSlug}/routes/{entitySlug}/comments')
  Future<CommentListResponse> listRouteComments({
    @Path('entitySlug') required String entitySlug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Create route comment.
  ///
  /// [entitySlug] - Route URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/teams/{teamSlug}/routes/{entitySlug}/comments')
  Future<CommentDto> createRouteComment({
    @Path('entitySlug') required String entitySlug,
    @Path('teamSlug') required String teamSlug,
    @Body() required CommentRequest body,
  });

  /// Delete route comment.
  ///
  /// [commentId] - Comment ID.
  ///
  /// [entitySlug] - Route URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @DELETE('/api/teams/{teamSlug}/routes/{entitySlug}/comments/{commentId}')
  Future<void> deleteRouteComment({
    @Path('commentId') required String commentId,
    @Path('entitySlug') required String entitySlug,
    @Path('teamSlug') required String teamSlug,
  });
}
