import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../../core/pagination/pagination.dart';
import '../domain/route_filters.dart';

final routeRepositoryProvider = Provider<RouteRepository>((ref) {
  return RouteRepository(ref.watch(routesClientProvider));
});

class RouteRepository {
  final RoutesClient _routesClient;

  RouteRepository(this._routesClient);

  /// One page of routes, filtered and sorted.
  ///
  /// [teamSlug] null queries every team the user can see, otherwise the list
  /// is scoped to that team.
  Future<PageResult<RouteDto>> fetchRoutes({
    required String? teamSlug,
    required RouteFilters filters,
    int page = 0,
    int size = kDefaultPageSize,
  }) async {
    final search = filters.search?.trim();
    final response = teamSlug == null
        ? await _routesClient.listAllRoutes(
            page: page,
            size: size,
            hilliness: filters.hilliness,
            maxDistance: filters.maxDistance,
            maxElevationGain: filters.maxElevationGain,
            minDistance: filters.minDistance,
            minElevationGain: filters.minElevationGain,
            search: (search?.isEmpty ?? true) ? null : search,
            sortBy: filters.sortBy,
            sortDir: filters.sortDir,
            surfaceType: filters.surfaceType,
            windDirection: filters.windDirection,
          )
        : await _routesClient.listRoutes(
            teamSlug: teamSlug,
            page: page,
            size: size,
            hilliness: filters.hilliness,
            maxDistance: filters.maxDistance,
            maxElevationGain: filters.maxElevationGain,
            minDistance: filters.minDistance,
            minElevationGain: filters.minElevationGain,
            search: (search?.isEmpty ?? true) ? null : search,
            sortBy: filters.sortBy,
            sortDir: filters.sortDir,
            surfaceType: filters.surfaceType,
            windDirection: filters.windDirection,
          );

    return PageResult(items: response.routes, total: response.total);
  }

  /// Number of routes matching [filters], without fetching them.
  ///
  /// Used by the filter sheet to keep its button honest about what applying
  /// the draft would yield.
  Future<int> countRoutes({
    required String? teamSlug,
    required RouteFilters filters,
  }) async {
    final result = await fetchRoutes(
      teamSlug: teamSlug,
      filters: filters,
      size: 1,
    );
    return result.total;
  }

  /// Get route details
  Future<RouteDetailDto> getRoute(String teamSlug, String routeSlug) {
    return _routesClient.getRoute(teamSlug: teamSlug, routeSlug: routeSlug);
  }
}
