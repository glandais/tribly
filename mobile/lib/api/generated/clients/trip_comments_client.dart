// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/comment_dto.dart';
import '../models/comment_list_response.dart';
import '../models/comment_request.dart';

part 'trip_comments_client.g.dart';

@RestApi()
abstract class TripCommentsClient {
  factory TripCommentsClient(Dio dio, {String? baseUrl}) = _TripCommentsClient;

  /// List trip comments.
  ///
  /// [entitySlug] - Trip URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/teams/{teamSlug}/trips/{entitySlug}/comments')
  Future<CommentListResponse> listTripComments({
    @Path('entitySlug') required String entitySlug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Create trip comment.
  ///
  /// [entitySlug] - Trip URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/teams/{teamSlug}/trips/{entitySlug}/comments')
  Future<CommentDto> createTripComment({
    @Path('entitySlug') required String entitySlug,
    @Path('teamSlug') required String teamSlug,
    @Body() required CommentRequest body,
  });

  /// Delete trip comment.
  ///
  /// [commentId] - Comment ID.
  ///
  /// [entitySlug] - Trip URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @DELETE('/api/teams/{teamSlug}/trips/{entitySlug}/comments/{commentId}')
  Future<void> deleteTripComment({
    @Path('commentId') required String commentId,
    @Path('entitySlug') required String entitySlug,
    @Path('teamSlug') required String teamSlug,
  });
}
