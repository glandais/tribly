import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/tribly_api_client.dart';

final routeRepositoryProvider = Provider<RouteRepository>((ref) {
  return RouteRepository(ref.watch(routesClientProvider));
});

class RouteRepository {
  final RoutesClient _routesClient;

  RouteRepository(this._routesClient);

  /// Get routes for a team
  Future<List<RouteDto>> getTeamRoutes(
    String teamSlug, {
    int page = 0,
    int size = 20,
  }) async {
    final response = await _routesClient.listRoutes(
      teamSlug: teamSlug,
      page: page,
      size: size,
    );
    return response.routes;
  }

  /// Get route details
  Future<RouteDetailDto> getRoute(String teamSlug, String routeSlug) {
    return _routesClient.getRoute(teamSlug: teamSlug, routeSlug: routeSlug);
  }

  /// Get all public routes
  Future<List<RouteDto>> getPublicRoutes({
    int page = 0,
    int size = 20,
    String? search,
  }) async {
    final response = await _routesClient.listAllRoutes(
      page: page,
      size: size,
      search: search,
    );
    return response.routes;
  }
}
