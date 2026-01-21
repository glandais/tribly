// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/comment_dto.dart';
import '../models/comment_list_response.dart';
import '../models/comment_request.dart';

part 'ride_comments_client.g.dart';

@RestApi()
abstract class RideCommentsClient {
  factory RideCommentsClient(Dio dio, {String? baseUrl}) = _RideCommentsClient;

  /// List ride comments.
  ///
  /// [entitySlug] - Ride URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/teams/{teamSlug}/rides/{entitySlug}/comments')
  Future<CommentListResponse> listRideComments({
    @Path('entitySlug') required String entitySlug,
    @Path('teamSlug') required String teamSlug,
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
