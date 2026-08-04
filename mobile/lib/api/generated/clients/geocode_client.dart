// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/geocode_result_dto.dart';

part 'geocode_client.g.dart';

@RestApi()
abstract class GeocodeClient {
  factory GeocodeClient(Dio dio, {String? baseUrl}) = _GeocodeClient;

  /// Search places by name.
  ///
  /// Returns at most 5 places matching the query, or an empty list when the query is shorter than 3 characters or the provider is unreachable. Results come from OpenStreetMap via Nominatim: a client displaying them must credit '© OpenStreetMap contributors'.
  @GET('/api/geocode/search')
  Future<List<GeocodeResultDto>> searchPlaces({
    @Query('q') String? q,
  });
}
