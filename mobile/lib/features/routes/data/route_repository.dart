import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../../core/pagination/pagination.dart';
import '../../../core/pdl/map/pdl_map_controller.dart';
import '../domain/route_filters.dart';

final routeRepositoryProvider = Provider<RouteRepository>((ref) {
  return RouteRepository(ref.watch(routesClientProvider));
});

class RouteRepository {
  final RoutesClient _routesClient;

  RouteRepository(this._routesClient);

  /// One page of routes, filtered and sorted.
  ///
  /// La portée est dans [filters] : `teamSlug` nul interroge toutes les
  /// équipes visibles, sinon la liste est celle de cette équipe.
  Future<PageResult<RouteDto>> fetchRoutes({
    required RouteFilters filters,
    int page = 0,
    int size = kDefaultPageSize,
    ListViewMode? view,
  }) async {
    final String? search = _search(filters);
    final String? teamSlug = filters.teamSlug;
    final response = teamSlug == null
        ? await _routesClient.listAllRoutes(
            page: page,
            size: size,
            hilliness: filters.hilliness,
            maxDistance: filters.maxDistance,
            maxElevationGain: filters.maxElevationGain,
            minDistance: filters.minDistance,
            minElevationGain: filters.minElevationGain,
            minRole: filters.minRole,
            nearLat: filters.hasProximity ? filters.nearLat : null,
            nearLon: filters.hasProximity ? filters.nearLon : null,
            nearRadius: filters.hasProximity
                ? filters.effectiveNearRadius
                : null,
            nearType: filters.hasProximity ? filters.nearType : null,
            search: search,
            sortBy: filters.sortBy,
            sortDir: filters.sortDir,
            surfaceType: filters.surfaceType,
            view: view,
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
            nearLat: filters.hasProximity ? filters.nearLat : null,
            nearLon: filters.hasProximity ? filters.nearLon : null,
            nearRadius: filters.hasProximity
                ? filters.effectiveNearRadius
                : null,
            nearType: filters.hasProximity ? filters.nearType : null,
            search: search,
            sortBy: filters.sortBy,
            sortDir: filters.sortDir,
            surfaceType: filters.surfaceType,
            view: view,
            windDirection: filters.windDirection,
          );

    return PageResult(items: response.routes, total: response.total);
  }

  /// Number of routes matching [filters], without fetching them.
  ///
  /// Passe par `…/routes/count` et non par une page de taille 1 : le serveur
  /// ne lit alors **aucun** parcours, et le compte ne peut pas diverger de la
  /// liste puisqu'il accepte exactement les mêmes filtres.
  Future<int> countRoutes(RouteFilters filters) async {
    final String? search = _search(filters);
    final String? teamSlug = filters.teamSlug;
    final CountResponse response = teamSlug == null
        ? await _routesClient.countAllRoutes(
            hilliness: filters.hilliness,
            maxDistance: filters.maxDistance,
            maxElevationGain: filters.maxElevationGain,
            minDistance: filters.minDistance,
            minElevationGain: filters.minElevationGain,
            minRole: filters.minRole,
            nearLat: filters.hasProximity ? filters.nearLat : null,
            nearLon: filters.hasProximity ? filters.nearLon : null,
            nearRadius: filters.hasProximity
                ? filters.effectiveNearRadius
                : null,
            nearType: filters.hasProximity ? filters.nearType : null,
            search: search,
            surfaceType: filters.surfaceType,
            windDirection: filters.windDirection,
          )
        : await _routesClient.countRoutes(
            teamSlug: teamSlug,
            hilliness: filters.hilliness,
            maxDistance: filters.maxDistance,
            maxElevationGain: filters.maxElevationGain,
            minDistance: filters.minDistance,
            minElevationGain: filters.minElevationGain,
            nearLat: filters.hasProximity ? filters.nearLat : null,
            nearLon: filters.hasProximity ? filters.nearLon : null,
            nearRadius: filters.hasProximity
                ? filters.effectiveNearRadius
                : null,
            nearType: filters.hasProximity ? filters.nearType : null,
            search: search,
            surfaceType: filters.surfaceType,
            windDirection: filters.windDirection,
          );
    return response.total;
  }

  /// La boîte englobant les parcours de [filters], pour cadrer la carte
  /// **avant** son premier rendu. `null` quand aucun parcours ne correspond.
  Future<PdlMapBox?> fetchBounds(RouteFilters filters) async {
    final String? search = _search(filters);
    final String? teamSlug = filters.teamSlug;
    final RouteBoundsResponse response = teamSlug == null
        ? await _routesClient.getAllRoutesBounds(
            hilliness: filters.hilliness,
            maxDistance: filters.maxDistance,
            maxElevationGain: filters.maxElevationGain,
            minDistance: filters.minDistance,
            minElevationGain: filters.minElevationGain,
            minRole: filters.minRole,
            nearLat: filters.hasProximity ? filters.nearLat : null,
            nearLon: filters.hasProximity ? filters.nearLon : null,
            nearRadius: filters.hasProximity
                ? filters.effectiveNearRadius
                : null,
            nearType: filters.hasProximity ? filters.nearType : null,
            search: search,
            surfaceType: filters.surfaceType,
            windDirection: filters.windDirection,
          )
        : await _routesClient.getRoutesBounds(
            teamSlug: teamSlug,
            hilliness: filters.hilliness,
            maxDistance: filters.maxDistance,
            maxElevationGain: filters.maxElevationGain,
            minDistance: filters.minDistance,
            minElevationGain: filters.minElevationGain,
            nearLat: filters.hasProximity ? filters.nearLat : null,
            nearLon: filters.hasProximity ? filters.nearLon : null,
            nearRadius: filters.hasProximity
                ? filters.effectiveNearRadius
                : null,
            nearType: filters.hasProximity ? filters.nearType : null,
            search: search,
            surfaceType: filters.surfaceType,
            windDirection: filters.windDirection,
          );

    final BoundsDto? bounds = response.bounds;
    if (bounds == null) return null;
    return PdlMapBox(
      minLon: bounds.minLon,
      minLat: bounds.minLat,
      maxLon: bounds.maxLon,
      maxLat: bounds.maxLat,
    );
  }

  /// Get route details
  Future<RouteDetailDto> getRoute(String teamSlug, String routeSlug) {
    return _routesClient.getRoute(teamSlug: teamSlug, routeSlug: routeSlug);
  }

  /// Le détail d'un parcours, géométrie **bornée**.
  ///
  /// `simplify` (tolérance de Douglas-Peucker en mètres) et `points` (plafond
  /// de sommets) existent au contrat et n'étaient pas envoyés : un parcours de
  /// 150 km descendait plusieurs mégaoctets de coordonnées pour dessiner un
  /// tracé de 300 px de large. L'appelant choisit sa finesse — 15 m / 1 500
  /// points pour un aperçu multi-tracés, 5 m / 3 000 pour la fiche.
  Future<RouteDetailDto> getRouteDetail(
    String teamSlug,
    String routeSlug, {
    double? simplify,
    int? points,
  }) {
    return _routesClient.getRoute(
      teamSlug: teamSlug,
      routeSlug: routeSlug,
      simplify: simplify,
      points: points,
    );
  }

  static String? _search(RouteFilters filters) {
    final String? search = filters.search?.trim();
    return (search == null || search.isEmpty) ? null : search;
  }
}
