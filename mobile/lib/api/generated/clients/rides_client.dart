// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/ride_dto.dart';
import '../models/ride_participation_dto.dart';
import '../models/ride_request.dart';
import '../models/slug_change_request.dart';

part 'rides_client.g.dart';

@RestApi()
abstract class RidesClient {
  factory RidesClient(Dio dio, {String? baseUrl}) = _RidesClient;

  /// Create ride.
  ///
  /// Create a new ride with optional groups.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/teams/{teamSlug}/rides')
  Future<RideDto> createRide({
    @Path('teamSlug') required String teamSlug,
    @Body() required RideRequest body,
  });

  /// Update ride.
  ///
  /// Update ride information. Requires organizer permissions.
  ///
  /// [rideSlug] - Ride URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PUT('/api/teams/{teamSlug}/rides/{rideSlug}')
  Future<RideDto> updateRide({
    @Path('rideSlug') required String rideSlug,
    @Path('teamSlug') required String teamSlug,
    @Body() required RideRequest body,
  });

  /// Get ride details.
  ///
  /// Get detailed ride information including groups.
  ///
  /// [rideSlug] - Ride URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/teams/{teamSlug}/rides/{rideSlug}')
  Future<RideDto> getRide({
    @Path('rideSlug') required String rideSlug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Delete ride.
  ///
  /// Soft delete a ride. Requires organizer permissions.
  ///
  /// [rideSlug] - Ride URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @DELETE('/api/teams/{teamSlug}/rides/{rideSlug}')
  Future<void> deleteRide({
    @Path('rideSlug') required String rideSlug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Join ride group.
  ///
  /// Join a ride group.
  ///
  /// [groupId] - Group ID (TSID).
  ///
  /// [rideSlug] - Ride URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @POST('/api/teams/{teamSlug}/rides/{rideSlug}/groups/{groupId}/join')
  Future<RideParticipationDto> joinGroup({
    @Path('groupId') required String groupId,
    @Path('rideSlug') required String rideSlug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Leave ride group.
  ///
  /// Leave a ride group.
  ///
  /// [groupId] - Group ID (TSID).
  ///
  /// [rideSlug] - Ride URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @POST('/api/teams/{teamSlug}/rides/{rideSlug}/groups/{groupId}/leave')
  Future<void> leaveGroup({
    @Path('groupId') required String groupId,
    @Path('rideSlug') required String rideSlug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Change ride slug.
  ///
  /// Change ride URL slug. Requires organizer permissions.
  ///
  /// [rideSlug] - Current ride URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PATCH('/api/teams/{teamSlug}/rides/{rideSlug}/slug')
  Future<RideDto> changeRideSlug({
    @Path('rideSlug') required String rideSlug,
    @Path('teamSlug') required String teamSlug,
    @Body() required SlugChangeRequest body,
  });

  /// Restore ride.
  ///
  /// Restore a soft-deleted ride. Requires organizer permissions.
  ///
  /// [rideSlug] - Ride URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @POST('/api/teams/{teamSlug}/rides/{rideSlug}/undelete')
  Future<RideDto> undeleteRide({
    @Path('rideSlug') required String rideSlug,
    @Path('teamSlug') required String teamSlug,
  });
}
