import 'dart:ui' show Color;

import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../../core/pdl/map/pdl_map_controller.dart';
import '../../../core/pdl/map/pdl_mass_layer.dart';

/// Le repli GeoJSON de proximité, côté données.
///
/// **Pourquoi ici et pas dans `core/pdl/map`** : le contrat du module
/// (`core/pdl/README.md`, §1.0.3-2 du plan) interdit à `core/pdl` de connaître
/// le moindre DTO généré, et `grep -rn "api/generated" lib/core/pdl` doit
/// rester vide. `pdl_mass_layer.dart` n'expose donc qu'un [PdlMassFetcher] —
/// une fonction — et c'est ce fichier-ci, dans le feature, qui parle à l'API.
///
/// **Le coût, dit franchement.** L'API ne sert aucune géométrie en liste :
/// `RouteDto` porte le nom, les distances et une vignette, jamais la
/// polyligne. Dessiner N parcours impose donc N appels de détail
/// (`GET …/routes/{slug}`) — un `N+1` assumé, borné de trois façons :
///
/// * `simplify` et `points` sur chaque détail : le serveur applique
///   Douglas-Peucker et rend une ligne allégée, largement suffisante à un
///   niveau de zoom régional ;
/// * un plafond de tracés ([PdlMassLayerController.limit]) et un parallélisme
///   borné, pour ne pas ouvrir 120 sockets d'un coup ;
/// * un cache mémoire par `(teamSlug, routeSlug)` : déplacer la carte et
///   revenir ne retélécharge rien.
///
/// C'est le prix du blocage documenté en tête de `pdl_mass_layer.dart`. Une
/// URL de tuile signée le ferait disparaître entièrement.
class RoutesMassRepository {
  RoutesMassRepository(this._client);

  final RoutesClient _client;

  /// Les géométries déjà téléchargées, par `teamSlug/routeSlug`.
  final Map<String, List<List<List<double>>>> _geometryCache =
      <String, List<List<List<double>>>>{};

  /// Nombre d'appels de détail simultanés. Six : au-delà, le gain de latence
  /// est mangé par la contention réseau sur un lien mobile.
  static const int _concurrency = 6;

  /// Tolérance de simplification, en mètres. À l'échelle d'une carte de masse,
  /// 25 m est invisible et divise le nombre de points par un ordre de grandeur.
  static const double _simplifyMeters = 25;

  /// Plafond de points par tracé, en plus de la simplification.
  static const int _maxPoints = 300;

  /// Construit le [PdlMassFetcher] attendu par `PdlMassLayerController`.
  ///
  /// [color] est la couleur de trait : elle vient du thème de l'écran, jamais
  /// d'ici. [teamSlug] restreint à une équipe ; `null` interroge tous les
  /// parcours accessibles.
  PdlMassFetcher fetcher({
    required Color color,
    String? teamSlug,
    NearType nearType = NearType.startOrEnd,
    String? search,
    SurfaceType? surfaceType,
  }) {
    return (PdlMassRequest request) async {
      final RouteListResponse page = teamSlug == null
          ? await _client.listAllRoutes(
              page: 0,
              size: request.limit,
              nearLat: request.centerLat,
              nearLon: request.centerLon,
              nearRadius: request.radiusMeters,
              nearType: nearType,
              search: search,
              surfaceType: surfaceType,
              view: ListViewMode.compact,
            )
          : await _client.listRoutes(
              teamSlug: teamSlug,
              page: 0,
              size: request.limit,
              nearLat: request.centerLat,
              nearLon: request.centerLon,
              nearRadius: request.radiusMeters,
              nearType: nearType,
              search: search,
              surfaceType: surfaceType,
              view: ListViewMode.compact,
            );

      final List<PdlMapTrack> tracks = <PdlMapTrack>[];
      final List<RouteDto> rows = page.routes;

      for (int start = 0; start < rows.length; start += _concurrency) {
        final int end = (start + _concurrency).clamp(0, rows.length);
        final List<List<List<List<double>>>?> batch = await Future.wait(
          <Future<List<List<List<double>>>?>>[
            for (final RouteDto r in rows.sublist(start, end)) _geometryOf(r),
          ],
        );
        for (int i = 0; i < batch.length; i++) {
          final List<List<List<double>>>? lines = batch[i];
          if (lines == null || lines.isEmpty) continue;
          final RouteDto row = rows[start + i];
          tracks.add(
            PdlMapTrack(
              id: row.slug,
              lines: lines,
              color: color,
              label: row.name,
            ),
          );
        }
      }

      return PdlMassResult(
        tracks: tracks,
        // Le plafond est **dit**, pas subi : une carte tronquée en silence se
        // lit comme une carte complète.
        truncated: page.total > rows.length,
        total: page.total,
      );
    };
  }

  Future<List<List<List<double>>>?> _geometryOf(RouteDto row) async {
    final String key = '${row.team.slug}/${row.slug}';
    final List<List<List<double>>>? cached = _geometryCache[key];
    if (cached != null) return cached;
    try {
      final RouteDetailDto detail = await _client.getRoute(
        teamSlug: row.team.slug,
        routeSlug: row.slug,
        simplify: _simplifyMeters,
        points: _maxPoints,
      );
      final List<List<List<double>>> lines = <List<List<double>>>[
        for (final TrackDto t in detail.tracks) t.line.coordinates,
      ];
      _geometryCache[key] = lines;
      return lines;
    } catch (_) {
      // Un parcours illisible ne doit pas emporter la carte entière : il
      // manque, les autres s'affichent.
      return null;
    }
  }
}

final routesMassRepositoryProvider = Provider<RoutesMassRepository>(
  (Ref ref) => RoutesMassRepository(ref.watch(routesClientProvider)),
);
