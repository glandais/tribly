// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/slug_change_request.dart';
import '../models/trip_dto.dart';
import '../models/trip_participation_dto.dart';
import '../models/trip_request.dart';

part 'trips_client.g.dart';

@RestApi()
abstract class TripsClient {
  factory TripsClient(Dio dio, {String? baseUrl}) = _TripsClient;

  /// Create trip.
  ///
  /// Create a new trip with optional stages.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/teams/{teamSlug}/trips')
  Future<TripDto> createTrip({
    @Path('teamSlug') required String teamSlug,
    @Body() required TripRequest body,
  });

  /// Update trip.
  ///
  /// Update trip information. Requires organizer permissions.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [tripSlug] - Trip URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PUT('/api/teams/{teamSlug}/trips/{tripSlug}')
  Future<TripDto> updateTrip({
    @Path('teamSlug') required String teamSlug,
    @Path('tripSlug') required String tripSlug,
    @Body() required TripRequest body,
  });

  /// Get trip details.
  ///
  /// Get detailed trip information including stages and participants.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [tripSlug] - Trip URL slug.
  @GET('/api/teams/{teamSlug}/trips/{tripSlug}')
  Future<TripDto> getTrip({
    @Path('teamSlug') required String teamSlug,
    @Path('tripSlug') required String tripSlug,
  });

  /// Delete trip.
  ///
  /// Soft delete a trip. Requires organizer permissions.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [tripSlug] - Trip URL slug.
  @DELETE('/api/teams/{teamSlug}/trips/{tripSlug}')
  Future<void> deleteTrip({
    @Path('teamSlug') required String teamSlug,
    @Path('tripSlug') required String tripSlug,
  });

  /// Join trip.
  ///
  /// Join a trip as a participant.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [tripSlug] - Trip URL slug.
  @POST('/api/teams/{teamSlug}/trips/{tripSlug}/join')
  Future<TripParticipationDto> joinTrip({
    @Path('teamSlug') required String teamSlug,
    @Path('tripSlug') required String tripSlug,
  });

  /// Leave trip.
  ///
  /// Leave a trip as a participant.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [tripSlug] - Trip URL slug.
  @POST('/api/teams/{teamSlug}/trips/{tripSlug}/leave')
  Future<void> leaveTrip({
    @Path('teamSlug') required String teamSlug,
    @Path('tripSlug') required String tripSlug,
  });

  /// Change trip slug.
  ///
  /// Change trip URL slug. Requires organizer permissions.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [tripSlug] - Current trip URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PATCH('/api/teams/{teamSlug}/trips/{tripSlug}/slug')
  Future<TripDto> changeTripSlug({
    @Path('teamSlug') required String teamSlug,
    @Path('tripSlug') required String tripSlug,
    @Body() required SlugChangeRequest body,
  });
}
