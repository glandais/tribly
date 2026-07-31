import '../domain/route_filters.dart';

/// Les URL de tuiles vectorielles des parcours.
///
/// **Deux exigences, aucune n'est négociable.**
///
/// 1. Les gabarits `{z}`, `{x}` et `{y}` doivent survivre **verbatim** :
///    MapLibre les substitue lui-même. D'où une query construite à part et
///    concaténée — passer l'URL entière dans `Uri.parse().toString()`
///    percent-encoderait les accolades et produirait des 404 silencieux.
/// 2. La sérialisation des filtres est **identique à celle du web**. La
///    référence est `frontend/src/components/map/mapConstants.ts`
///    (`toRouteTileFilters` + `tilesQuery`) : même sous-ensemble de filtres,
///    mêmes omissions — le tri et la pagination, qu'une tuile n'a que faire —
///    et même règle « on saute ce qui est nul ou vide ». Diverger ici ferait
///    diverger la carte web et la carte mobile en silence, sur les mêmes
///    données.
abstract final class RouteTileUrls {
  /// La couche que `ST_AsMVT` nomme côté backend.
  static const String sourceLayer = 'routes';

  /// Tuiles de tous les parcours accessibles.
  static String allRoutes({
    required String apiBaseUrl,
    required String token,
    RouteFilters? filters,
  }) => _url(apiBaseUrl, '/api/routes/tiles/{z}/{x}/{y}.mvt', token, filters);

  /// Tuiles des parcours d'une équipe.
  static String teamRoutes({
    required String apiBaseUrl,
    required String teamSlug,
    required String token,
    RouteFilters? filters,
  }) => _url(
    apiBaseUrl,
    '/api/teams/${Uri.encodeComponent(teamSlug)}/routes/tiles/{z}/{x}/{y}.mvt',
    token,
    filters,
  );

  static String _url(
    String apiBaseUrl,
    String path,
    String token,
    RouteFilters? filters,
  ) {
    final String base = apiBaseUrl.endsWith('/')
        ? apiBaseUrl.substring(0, apiBaseUrl.length - 1)
        : apiBaseUrl;
    return '$base$path?${_query(token, filters)}';
  }

  static String _query(String token, RouteFilters? filters) {
    // Une `Map` ordonnée : deux jeux de filtres identiques doivent produire la
    // **même chaîne**, sinon MapLibre voit deux sources différentes et
    // retélécharge tout.
    final Map<String, String> params = <String, String>{};

    if (filters != null) {
      _put(params, 'search', filters.search);
      _put(params, 'minDistance', filters.minDistance);
      _put(params, 'maxDistance', filters.maxDistance);
      _put(params, 'minElevationGain', filters.minElevationGain);
      _put(params, 'maxElevationGain', filters.maxElevationGain);
      _put(params, 'hilliness', filters.hilliness?.json);
      _put(params, 'surfaceType', filters.surfaceType?.json);
      _put(params, 'windDirection', filters.windDirection?.json);
      _put(params, 'minRole', filters.minRole?.json);
    }

    // Le jeton en dernier : il change à chaque renouvellement, et le garder au
    // bout rend un diff d'URL lisible dans les journaux.
    params['t'] = token;

    return params.entries
        .map(
          (MapEntry<String, String> e) =>
              '${Uri.encodeQueryComponent(e.key)}=${Uri.encodeQueryComponent(e.value)}',
        )
        .join('&');
  }

  /// Saute le nul et la chaîne vide, comme `tilesQuery` côté web. Les doubles
  /// entiers sortent sans décimale — `10000` et non `10000.0`, que le backend
  /// accepte mais qui ferait diverger l'URL de celle du web.
  static void _put(Map<String, String> params, String key, Object? value) {
    if (value == null) return;
    final String text = value is double && value == value.roundToDouble()
        ? value.toInt().toString()
        : value.toString();
    if (text.isEmpty) return;
    params[key] = text;
  }
}
