import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/features/routes/data/route_tile_urls.dart';
import 'package:pedalons/features/routes/domain/route_filters.dart';

const String _base = 'https://pedalons.example';

void main() {
  group('RouteTileUrls', () {
    /// MapLibre substitue lui-même `{z}/{x}/{y}` : les accolades
    /// percent-encodées produiraient des 404 silencieux.
    test('les gabarits de tuile survivent verbatim', () {
      final String url = RouteTileUrls.allRoutes(
        apiBaseUrl: _base,
        token: 'abc',
      );

      expect(url, contains('/api/routes/tiles/{z}/{x}/{y}.mvt'));
      expect(url, isNot(contains('%7B')));
      expect(url, isNot(contains('%7D')));
    });

    test('sans filtre, seul le jeton est en query', () {
      expect(
        RouteTileUrls.allRoutes(apiBaseUrl: _base, token: 'abc'),
        '$_base/api/routes/tiles/{z}/{x}/{y}.mvt?t=abc',
      );
    });

    test('une base terminée par un slash ne double pas le séparateur', () {
      expect(
        RouteTileUrls.allRoutes(apiBaseUrl: '$_base/', token: 'abc'),
        startsWith('$_base/api/routes/'),
      );
    });

    test('le slug d\'équipe est encodé', () {
      final String url = RouteTileUrls.teamRoutes(
        apiBaseUrl: _base,
        teamSlug: 'les copains',
        token: 'abc',
      );

      expect(url, contains('/api/teams/les%20copains/routes/tiles/'));
    });

    test('le jeton est encodé', () {
      final String url = RouteTileUrls.allRoutes(
        apiBaseUrl: _base,
        token: 'a+b/c=',
      );

      expect(url, endsWith('t=a%2Bb%2Fc%3D'));
    });

    /// La référence est `frontend/src/components/map/mapConstants.ts` : même
    /// sous-ensemble, mêmes omissions, mêmes valeurs. Une divergence ici ferait
    /// diverger la carte web et la carte mobile sur les mêmes données.
    test('les filtres se sérialisent comme sur le web', () {
      final String url = RouteTileUrls.allRoutes(
        apiBaseUrl: _base,
        token: 'abc',
        filters: const RouteFilters(
          search: 'col',
          minDistance: 10000,
          maxDistance: 80000,
          minElevationGain: 500,
          maxElevationGain: 2000,
          hilliness: Hilliness.hilly,
          surfaceType: SurfaceType.gravel,
          windDirection: WindDirection.north,
          minRole: MinRole.member,
        ),
      );

      expect(url, contains('search=col'));
      expect(url, contains('minDistance=10000'));
      expect(url, contains('maxDistance=80000'));
      expect(url, contains('minElevationGain=500'));
      expect(url, contains('maxElevationGain=2000'));
      expect(url, contains('hilliness=${Hilliness.hilly.json}'));
      expect(url, contains('surfaceType=GRAVEL'));
      expect(url, contains('windDirection=${WindDirection.north.json}'));
      expect(url, contains('minRole=MEMBER'));
    });

    /// Les doubles entiers sortent sans décimale : `10000.0` serait accepté par
    /// le backend mais ferait une URL différente de celle du web, donc un cache
    /// de tuiles distinct pour rien.
    test('les distances entières sortent sans décimale', () {
      final String url = RouteTileUrls.allRoutes(
        apiBaseUrl: _base,
        token: 'abc',
        filters: const RouteFilters(minDistance: 10000),
      );

      expect(url, contains('minDistance=10000'));
      expect(url, isNot(contains('10000.0')));
    });

    test('le tri et la pagination ne passent jamais dans une tuile', () {
      final String url = RouteTileUrls.allRoutes(
        apiBaseUrl: _base,
        token: 'abc',
        filters: const RouteFilters(
          sortBy: RouteSortBy.distance,
          sortDir: SortDirection.asc,
        ),
      );

      expect(url, isNot(contains('sortBy')));
      expect(url, isNot(contains('sortDir')));
    });

    test('un filtre nul ou vide est sauté', () {
      final String url = RouteTileUrls.allRoutes(
        apiBaseUrl: _base,
        token: 'abc',
        filters: const RouteFilters(search: ''),
      );

      expect(url, isNot(contains('search=')));
    });

    /// Deux jeux identiques doivent produire la **même** chaîne : sinon
    /// MapLibre voit deux sources différentes et retélécharge tout.
    test('la sérialisation est stable', () {
      const RouteFilters filters = RouteFilters(
        search: 'col',
        surfaceType: SurfaceType.mtb,
      );

      expect(
        RouteTileUrls.allRoutes(
          apiBaseUrl: _base,
          token: 'abc',
          filters: filters,
        ),
        RouteTileUrls.allRoutes(
          apiBaseUrl: _base,
          token: 'abc',
          filters: filters,
        ),
      );
    });
  });
}
