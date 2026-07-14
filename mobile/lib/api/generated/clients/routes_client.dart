// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'dart:convert';
import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/hilliness.dart';
import '../models/min_role.dart';
import '../models/near_type.dart';
import '../models/route_bounds_response.dart';
import '../models/route_detail_dto.dart';
import '../models/route_dto.dart';
import '../models/route_list_response.dart';
import '../models/route_request.dart';
import '../models/route_sort_by.dart';
import '../models/route_usages_response.dart';
import '../models/slug_change_request.dart';
import '../models/sort_direction.dart';
import '../models/surface_type.dart';
import '../models/wind_direction.dart';

part 'routes_client.g.dart';

@RestApi()
abstract class RoutesClient {
  factory RoutesClient(Dio dio, {String? baseUrl}) = _RoutesClient;

  /// List all routes.
  ///
  /// Get paginated list of routes from all accessible teams (user's teams + public teams).
  ///
  /// [hilliness] - Hilliness preset (FLAT, HILLY, MOUNTAINOUS).
  ///
  /// [maxDistance] - Maximum distance in meters.
  ///
  /// [maxElevationGain] - Maximum elevation gain in meters.
  ///
  /// [minDistance] - Minimum distance in meters.
  ///
  /// [minElevationGain] - Minimum elevation gain in meters.
  ///
  /// [minRole] - Only routes from teams where the user has at least this role. Yields nothing for an anonymous visitor.
  ///
  /// [nearLat] - Latitude for proximity search.
  ///
  /// [nearLon] - Longitude for proximity search.
  ///
  /// [nearRadius] - Search radius in meters (default: 25000).
  ///
  /// [nearType] - Search near START, END, or START_OR_END (default).
  ///
  /// [page] - Page number (0-indexed).
  ///
  /// [search] - Search by name/markdown.
  ///
  /// [size] - Page size.
  ///
  /// [sortBy] - Sort by field (DISTANCE, ELEVATION_GAIN, HILLINESS, DATE_TIME).
  ///
  /// [sortDir] - Sort direction (ASC, DESC).
  ///
  /// [surfaceType] - Filter by surface type.
  ///
  /// [windDirection] - Filter by wind direction.
  @GET('/api/routes')
  Future<RouteListResponse> listAllRoutes({
    @Query('page') int? page = 0,
    @Query('size') int? size = 20,
    @Query('hilliness') Hilliness? hilliness,
    @Query('maxDistance') double? maxDistance,
    @Query('maxElevationGain') double? maxElevationGain,
    @Query('minDistance') double? minDistance,
    @Query('minElevationGain') double? minElevationGain,
    @Query('minRole') MinRole? minRole,
    @Query('nearLat') double? nearLat,
    @Query('nearLon') double? nearLon,
    @Query('nearRadius') double? nearRadius,
    @Query('nearType') NearType? nearType,
    @Query('search') String? search,
    @Query('sortBy') RouteSortBy? sortBy,
    @Query('sortDir') SortDirection? sortDir,
    @Query('surfaceType') SurfaceType? surfaceType,
    @Query('windDirection') WindDirection? windDirection,
  });

  /// All routes bounding box.
  ///
  /// Extent enclosing the routes of all accessible teams, so a map can open framed on them. Accepts the same filters as the route list, minus sorting and pagination. Yields a null box when no route matches.
  ///
  /// [hilliness] - Hilliness preset (FLAT, HILLY, MOUNTAINOUS).
  ///
  /// [maxDistance] - Maximum distance in meters.
  ///
  /// [maxElevationGain] - Maximum elevation gain in meters.
  ///
  /// [minDistance] - Minimum distance in meters.
  ///
  /// [minElevationGain] - Minimum elevation gain in meters.
  ///
  /// [minRole] - Only routes from teams where the user has at least this role. Yields a null box for an anonymous visitor.
  ///
  /// [nearLat] - Latitude for proximity search.
  ///
  /// [nearLon] - Longitude for proximity search.
  ///
  /// [nearRadius] - Search radius in meters (default: 25000).
  ///
  /// [nearType] - Search near START, END, or START_OR_END (default).
  ///
  /// [search] - Search by name/markdown.
  ///
  /// [surfaceType] - Filter by surface type.
  ///
  /// [windDirection] - Filter by wind direction.
  @GET('/api/routes/bounds')
  Future<RouteBoundsResponse> getAllRoutesBounds({
    @Query('hilliness') Hilliness? hilliness,
    @Query('maxDistance') double? maxDistance,
    @Query('maxElevationGain') double? maxElevationGain,
    @Query('minDistance') double? minDistance,
    @Query('minElevationGain') double? minElevationGain,
    @Query('minRole') MinRole? minRole,
    @Query('nearLat') double? nearLat,
    @Query('nearLon') double? nearLon,
    @Query('nearRadius') double? nearRadius,
    @Query('nearType') NearType? nearType,
    @Query('search') String? search,
    @Query('surfaceType') SurfaceType? surfaceType,
    @Query('windDirection') WindDirection? windDirection,
  });

  /// All routes vector tile.
  ///
  /// Mapbox vector tile holding the routes of all accessible teams, layer 'routes'. Accepts the same filters as the route list, minus sorting and pagination, which a tile has no use for. Fetched directly by the map renderer, so it authenticates with the session cookie rather than a bearer token.
  ///
  /// [x] - Tile column.
  ///
  /// [y] - Tile row.
  ///
  /// [z] - Zoom level.
  ///
  /// [hilliness] - Hilliness preset (FLAT, HILLY, MOUNTAINOUS).
  ///
  /// [maxDistance] - Maximum distance in meters.
  ///
  /// [maxElevationGain] - Maximum elevation gain in meters.
  ///
  /// [minDistance] - Minimum distance in meters.
  ///
  /// [minElevationGain] - Minimum elevation gain in meters.
  ///
  /// [minRole] - Only routes from teams where the user has at least this role. Yields an empty tile for an anonymous visitor.
  ///
  /// [nearLat] - Latitude for proximity search.
  ///
  /// [nearLon] - Longitude for proximity search.
  ///
  /// [nearRadius] - Search radius in meters (default: 25000).
  ///
  /// [nearType] - Search near START, END, or START_OR_END (default).
  ///
  /// [search] - Search by name/markdown.
  ///
  /// [surfaceType] - Filter by surface type.
  ///
  /// [windDirection] - Filter by wind direction.
  @GET('/api/routes/tiles/{z}/{x}/{y}.mvt')
  Future<void> allRoutesTile({
    @Path('x') required int x,
    @Path('y') required int y,
    @Path('z') required int z,
    @Query('hilliness') Hilliness? hilliness,
    @Query('maxDistance') double? maxDistance,
    @Query('maxElevationGain') double? maxElevationGain,
    @Query('minDistance') double? minDistance,
    @Query('minElevationGain') double? minElevationGain,
    @Query('minRole') MinRole? minRole,
    @Query('nearLat') double? nearLat,
    @Query('nearLon') double? nearLon,
    @Query('nearRadius') double? nearRadius,
    @Query('nearType') NearType? nearType,
    @Query('search') String? search,
    @Query('surfaceType') SurfaceType? surfaceType,
    @Query('windDirection') WindDirection? windDirection,
  });

  /// List routes.
  ///
  /// Get paginated list of routes for a team with optional filters and sorting.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [hilliness] - Hilliness preset (FLAT, HILLY, MOUNTAINOUS).
  ///
  /// [maxDistance] - Maximum distance in meters.
  ///
  /// [maxElevationGain] - Maximum elevation gain in meters.
  ///
  /// [minDistance] - Minimum distance in meters.
  ///
  /// [minElevationGain] - Minimum elevation gain in meters.
  ///
  /// [nearLat] - Latitude for proximity search.
  ///
  /// [nearLon] - Longitude for proximity search.
  ///
  /// [nearRadius] - Search radius in meters (default: 25000).
  ///
  /// [nearType] - Search near START, END, or START_OR_END (default).
  ///
  /// [page] - Page number (0-indexed).
  ///
  /// [search] - Search by name/markdown.
  ///
  /// [size] - Page size.
  ///
  /// [sortBy] - Sort by field (DISTANCE, ELEVATION_GAIN, HILLINESS, DATE_TIME).
  ///
  /// [sortDir] - Sort direction (ASC, DESC).
  ///
  /// [surfaceType] - Filter by surface type.
  ///
  /// [windDirection] - Filter by wind direction.
  @GET('/api/teams/{teamSlug}/routes')
  Future<RouteListResponse> listRoutes({
    @Path('teamSlug') required String teamSlug,
    @Query('page') int? page = 0,
    @Query('size') int? size = 20,
    @Query('hilliness') Hilliness? hilliness,
    @Query('maxDistance') double? maxDistance,
    @Query('maxElevationGain') double? maxElevationGain,
    @Query('minDistance') double? minDistance,
    @Query('minElevationGain') double? minElevationGain,
    @Query('nearLat') double? nearLat,
    @Query('nearLon') double? nearLon,
    @Query('nearRadius') double? nearRadius,
    @Query('nearType') NearType? nearType,
    @Query('search') String? search,
    @Query('sortBy') RouteSortBy? sortBy,
    @Query('sortDir') SortDirection? sortDir,
    @Query('surfaceType') SurfaceType? surfaceType,
    @Query('windDirection') WindDirection? windDirection,
  });

  /// Create route.
  ///
  /// Create a new route by uploading a GPX file.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [route] - Name not received - field will be skipped.
  ///
  /// [gpxFile] - Name not received - field will be skipped.
  @MultiPart()
  @POST('/api/teams/{teamSlug}/routes')
  Future<RouteDto> createRoute({
    @Path('teamSlug') required String teamSlug,
    @Part(name: 'route') RouteRequest? route,
    @Part(name: 'gpxFile') MultipartFile? gpxFile,
  });

  /// Team routes bounding box.
  ///
  /// Extent enclosing the team's routes, so a map can open framed on them. Accepts the same filters as the route list, minus sorting and pagination. Yields a null box when no route matches.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [hilliness] - Hilliness preset (FLAT, HILLY, MOUNTAINOUS).
  ///
  /// [maxDistance] - Maximum distance in meters.
  ///
  /// [maxElevationGain] - Maximum elevation gain in meters.
  ///
  /// [minDistance] - Minimum distance in meters.
  ///
  /// [minElevationGain] - Minimum elevation gain in meters.
  ///
  /// [nearLat] - Latitude for proximity search.
  ///
  /// [nearLon] - Longitude for proximity search.
  ///
  /// [nearRadius] - Search radius in meters (default: 25000).
  ///
  /// [nearType] - Search near START, END, or START_OR_END (default).
  ///
  /// [search] - Search by name/markdown.
  ///
  /// [surfaceType] - Filter by surface type.
  ///
  /// [windDirection] - Filter by wind direction.
  @GET('/api/teams/{teamSlug}/routes/bounds')
  Future<RouteBoundsResponse> getRoutesBounds({
    @Path('teamSlug') required String teamSlug,
    @Query('hilliness') Hilliness? hilliness,
    @Query('maxDistance') double? maxDistance,
    @Query('maxElevationGain') double? maxElevationGain,
    @Query('minDistance') double? minDistance,
    @Query('minElevationGain') double? minElevationGain,
    @Query('nearLat') double? nearLat,
    @Query('nearLon') double? nearLon,
    @Query('nearRadius') double? nearRadius,
    @Query('nearType') NearType? nearType,
    @Query('search') String? search,
    @Query('surfaceType') SurfaceType? surfaceType,
    @Query('windDirection') WindDirection? windDirection,
  });

  /// Team routes vector tile.
  ///
  /// Mapbox vector tile holding the team's routes, layer 'routes'. Accepts the same filters as the route list, minus sorting and pagination, which a tile has no use for. Fetched directly by the map renderer, so it authenticates with the session cookie rather than a bearer token.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [x] - Tile column.
  ///
  /// [y] - Tile row.
  ///
  /// [z] - Zoom level.
  ///
  /// [hilliness] - Hilliness preset (FLAT, HILLY, MOUNTAINOUS).
  ///
  /// [maxDistance] - Maximum distance in meters.
  ///
  /// [maxElevationGain] - Maximum elevation gain in meters.
  ///
  /// [minDistance] - Minimum distance in meters.
  ///
  /// [minElevationGain] - Minimum elevation gain in meters.
  ///
  /// [nearLat] - Latitude for proximity search.
  ///
  /// [nearLon] - Longitude for proximity search.
  ///
  /// [nearRadius] - Search radius in meters (default: 25000).
  ///
  /// [nearType] - Search near START, END, or START_OR_END (default).
  ///
  /// [search] - Search by name/markdown.
  ///
  /// [surfaceType] - Filter by surface type.
  ///
  /// [windDirection] - Filter by wind direction.
  @GET('/api/teams/{teamSlug}/routes/tiles/{z}/{x}/{y}.mvt')
  Future<void> routesTile({
    @Path('teamSlug') required String teamSlug,
    @Path('x') required int x,
    @Path('y') required int y,
    @Path('z') required int z,
    @Query('hilliness') Hilliness? hilliness,
    @Query('maxDistance') double? maxDistance,
    @Query('maxElevationGain') double? maxElevationGain,
    @Query('minDistance') double? minDistance,
    @Query('minElevationGain') double? minElevationGain,
    @Query('nearLat') double? nearLat,
    @Query('nearLon') double? nearLon,
    @Query('nearRadius') double? nearRadius,
    @Query('nearType') NearType? nearType,
    @Query('search') String? search,
    @Query('surfaceType') SurfaceType? surfaceType,
    @Query('windDirection') WindDirection? windDirection,
  });

  /// Update route.
  ///
  /// Update route metadata (name, markdown, etc.) and optionally replace the GPX file. If a new GPX file is provided, the old track data and climbs will be replaced.
  ///
  /// [routeSlug] - Route slug.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [route] - Name not received - field will be skipped.
  ///
  /// [gpxFile] - Name not received - field will be skipped.
  @MultiPart()
  @PUT('/api/teams/{teamSlug}/routes/{routeSlug}')
  Future<RouteDto> updateRoute({
    @Path('routeSlug') required String routeSlug,
    @Path('teamSlug') required String teamSlug,
    @Part(name: 'route') RouteRequest? route,
    @Part(name: 'gpxFile') MultipartFile? gpxFile,
  });

  /// Get route details.
  ///
  /// Get detailed route information including GPS coordinates and statistics.
  ///
  /// [routeSlug] - Route slug.
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/teams/{teamSlug}/routes/{routeSlug}')
  Future<RouteDetailDto> getRoute({
    @Path('routeSlug') required String routeSlug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Delete route.
  ///
  /// Soft delete a route. Requires route creator or team admin permissions.
  ///
  /// [routeSlug] - Route slug.
  ///
  /// [teamSlug] - Team URL slug.
  @DELETE('/api/teams/{teamSlug}/routes/{routeSlug}')
  Future<void> deleteRoute({
    @Path('routeSlug') required String routeSlug,
    @Path('teamSlug') required String teamSlug,
  });

  /// Change route slug.
  ///
  /// Change route URL slug. Requires organizer permissions.
  ///
  /// [routeSlug] - Current route URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PATCH('/api/teams/{teamSlug}/routes/{routeSlug}/slug')
  Future<RouteDetailDto> changeRouteSlug({
    @Path('routeSlug') required String routeSlug,
    @Path('teamSlug') required String teamSlug,
    @Body() required SlugChangeRequest body,
  });

  /// Restore route.
  ///
  /// Restore a soft-deleted route. Requires route creator or team admin permissions.
  ///
  /// [routeSlug] - Route slug.
  ///
  /// [teamSlug] - Team URL slug.
  @POST('/api/teams/{teamSlug}/routes/{routeSlug}/undelete')
  Future<RouteDetailDto> undeleteRoute({
    @Path('routeSlug') required String routeSlug,
    @Path('teamSlug') required String teamSlug,
  });

  /// List route usages.
  ///
  /// Rides and trips that reference this route, directly or via a group/stage. Results are visibility filtered for the caller.
  ///
  /// [routeSlug] - Route slug.
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/teams/{teamSlug}/routes/{routeSlug}/usages')
  Future<RouteUsagesResponse> getRouteUsages({
    @Path('routeSlug') required String routeSlug,
    @Path('teamSlug') required String teamSlug,
  });
}
