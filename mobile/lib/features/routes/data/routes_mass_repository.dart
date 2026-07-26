import 'dart:ui' show Color;

import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../../core/pagination/pagination.dart';
import '../../../core/pdl/map/pdl_map_controller.dart';
import '../../../core/pdl/map/pdl_mass_layer.dart';
import '../domain/route_filters.dart';
import 'route_repository.dart';

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
  RoutesMassRepository(this._client, this._routes);

  final RoutesClient _client;

  /// La liste passe par le dépôt de l'écran et non par le client : c'est ce
  /// qui garantit que la carte et la liste montrent **le même jeu de
  /// résultats**, filtres, portée et tri compris.
  final RouteRepository _routes;

  /// Les lignes de liste déjà rencontrées, par `routeSlug`.
  ///
  /// C'est ce qui permet à la carte flottante d'une sélection d'afficher le
  /// nom, la vignette et les deux chiffres **sans un appel de plus** : le
  /// parcours a déjà été lu pour dessiner son tracé.
  final Map<String, RouteDto> _rowsBySlug = <String, RouteDto>{};

  /// La ligne de liste d'un tracé rendu, ou `null` s'il n'a jamais été chargé.
  RouteDto? row(String routeSlug) => _rowsBySlug[routeSlug];

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
  /// d'ici. [filters] est le jeu de la vue Liste, portée comprise — c'est ce
  /// qui interdit à la carte de montrer autre chose que la liste.
  PdlMassFetcher fetcher({
    required Color color,
    required RouteFilters filters,
  }) {
    return (PdlMassRequest request) async {
      // La zone visible **remplace** la proximité du jeu de filtres : c'est
      // elle qu'on regarde. Le rayon « Autour de moi » a déjà servi, en
      // recadrant la caméra.
      final PageResult<RouteDto> page = await _routes.fetchRoutes(
        filters: filters.copyWith(
          nearLat: request.centerLat,
          nearLon: request.centerLon,
          nearRadius: request.radiusMeters,
        ),
        size: request.limit,
        view: ListViewMode.compact,
      );

      final List<PdlMapTrack> tracks = <PdlMapTrack>[];
      final List<RouteDto> rows = page.items;

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
          _rowsBySlug[row.slug] = row;
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
  (Ref ref) => RoutesMassRepository(
    ref.watch(routesClientProvider),
    ref.watch(routeRepositoryProvider),
  ),
);
