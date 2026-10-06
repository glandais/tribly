// ─────────────────────────────────────────────────────────────────────────────
// F-TE-9 — la couche de masse, et comment son blocage a été levé.
//
// **L'état antérieur, parce qu'il explique la forme de ce qui suit.** Les
// tuiles `/api/routes/tiles/{z}/{x}/{y}.mvt` s'authentifiaient par le cookie de
// session, que le mobile n'a pas : il porte un JWT injecté par
// `AuthInterceptor` sur **Dio**, et MapLibre ne passe pas par Dio — il va
// chercher ses tuiles par sa propre pile HTTP native, à laquelle aucun
// intercepteur Dart n'est branché, et le paquet n'expose ni `transformRequest`
// ni en-tête personnalisable. Les tuiles revenaient donc en 401. Le repli livré
// était un GeoJSON de proximité, plafonné à 120 tracés et payé d'un `N+1`
// d'appels de détail, contre 2 585 tracés côté web.
//
// **Ce qui a levé le blocage** : un jeton signé court, passé en paramètre de
// requête — `POST /api/tiles/token`, puis
// `/api/routes/tiles/{z}/{x}/{y}.mvt?t=<jeton>`. Le jeton est frappé par un
// appel Dio authentifié et renouvelé avant expiration ; il n'autorise que les
// tuiles, et rien d'autre (le backend le garantit par une liaison nommée
// JAX-RS, pas par convention — voir `TileTokenService`). Côté mobile, la chaîne
// est : `features/routes/data/tile_token_repository.dart` (le jeton) →
// `features/routes/data/route_tile_urls.dart` (l'URL) →
// `PdlMapController.setMassTileUrl` (la pose).
//
// **Ce fichier n'expose donc plus que les tuiles.** Le repli GeoJSON, son
// plafond et sa pilule de troncature ont été supprimés : les garder aurait
// signifié maintenir deux rendus de la même carte, dont un qu'on ne veut plus
// voir.
//
// **La sélection au tap** ne passe pas par `queryLayers` : `QueriedLayer` ne
// porte que des identifiants de couche, et sur une source vectorielle tous les
// parcours vivent dans **une** couche. Elle passe par
// `PdlMapController.massFeatureAt`, bâti sur `featuresAtPoint`, qui rend les
// propriétés de l'entité — exactement ce que fait la carte web avec
// `event.features[0].properties`. Les propriétés disponibles sont celles du
// type `route_mvt_row` du backend : `slug`, `name`, `team_slug`, `distance`,
// `elevation_gain`.
// ─────────────────────────────────────────────────────────────────────────────

import 'dart:convert';
import 'dart:io';

import 'package:flutter/foundation.dart';
import 'package:maplibre/maplibre.dart';
import 'package:path_provider/path_provider.dart';

/// Les tuiles vectorielles de parcours servies par le backend.
abstract final class PdlMassTiles {
  /// La couche que `ST_AsMVT` nomme côté backend.
  static const String sourceLayerId = 'routes';

  /// La source.
  ///
  /// **`maxZoom` est obligatoire** : ne pas le régler produit une carte vide
  /// au-delà du zoom 2 (§1.3.2-2). 14 est le niveau maximal auquel le backend
  /// génère ses tuiles ; MapLibre sur-zoome celles-ci au-delà.
  ///
  /// Et `sourceLayer` se précise sur la **couche**, pas sur la source — la
  /// propriété homonyme de `VectorSource` est dépréciée et sans effet.
  static VectorSource source({
    required String id,
    required String tileUrlTemplate,
  }) => VectorSource(
    id: id,
    tiles: <String>[tileUrlTemplate],
    minZoom: 0,
    maxZoom: 14,
    volatile: true,
  );

  /// La source telle que la plateforme courante sait la poser.
  ///
  /// **Android ne sait pas poser [source]** : `maplibre_android` 0.3.6 ne lit
  /// que `VectorSource.url` (`source.url!`, un `TypeError` sur une source
  /// définie par `tiles`) — l'écran des parcours levait à chaque ouverture
  /// (feedback #8). On lui donne donc l'URL d'un TileJSON écrit dans le cache
  /// de l'app, que le SDK lit par son chargeur `file://` ; le document porte la
  /// même plage de zoom que [source]. iOS lit `tiles` correctement et garde
  /// [source].
  ///
  /// À retirer quand le greffon construira un `VectorSource` sur `TileSet`
  /// (la liaison JNI `VectorSource.new$4` existe déjà).
  static Future<VectorSource> platformSource({
    required String id,
    required String tileUrlTemplate,
  }) async {
    if (defaultTargetPlatform != TargetPlatform.android) {
      return source(id: id, tileUrlTemplate: tileUrlTemplate);
    }
    final Directory dir = await getApplicationCacheDirectory();
    // Un fichier par gabarit, donc par jeton : le SDK ne relit jamais un
    // document qu'il croit déjà connaître. Les précédents sont supprimés.
    final String name =
        'pdl-mass-${tileUrlTemplate.hashCode.toUnsigned(32).toRadixString(16)}.json';
    final File file = File('${dir.path}/$name');
    if (!file.existsSync()) {
      await for (final FileSystemEntity old in dir.list()) {
        final String oldName = old.uri.pathSegments.last;
        if (oldName.startsWith('pdl-mass-') && oldName.endsWith('.json')) {
          await old.delete();
        }
      }
      await file.writeAsString(tileJson(tileUrlTemplate));
    }
    return VectorSource(id: id, url: file.uri.toString(), volatile: true);
  }

  /// Le TileJSON équivalent à [source], pour les SDK qui ne prennent qu'une URL.
  static String tileJson(String tileUrlTemplate) => jsonEncode(<String, Object>{
    'tilejson': '3.0.0',
    'tiles': <String>[tileUrlTemplate],
    'minzoom': 0,
    'maxzoom': 14,
    'vector_layers': <Map<String, Object>>[
      <String, Object>{'id': sourceLayerId, 'fields': <String, String>{}},
    ],
  });

  /// La couche de tracés adossée à [source].
  static LineStyleLayer layer({
    required String id,
    required String sourceId,
    required String colorHex,
  }) => LineStyleLayer(
    id: id,
    sourceId: sourceId,
    sourceLayerId: sourceLayerId,
    layout: const <String, Object>{'line-cap': 'round', 'line-join': 'round'},
    paint: <String, Object>{
      'line-color': colorHex,
      'line-width': <Object>[
        'interpolate',
        <Object>['linear'],
        <Object>['zoom'],
        6,
        1.0,
        10,
        2.0,
        14,
        4.0,
      ],
      'line-opacity': 0.7,
    },
  );
}
