// ─────────────────────────────────────────────────────────────────────────────
// F-TE-9 — la couche de masse, et le blocage qui la rend impossible en l'état.
//
// Le contrat est explicite sur `/api/routes/tiles/{z}/{x}/{y}.mvt` :
//
//   « fetched directly by the map renderer, so it authenticates with the
//     session cookie rather than a bearer token »
//
// Le mobile n'a pas de cookie de session. Il porte un JWT, injecté par
// `AuthInterceptor` sur **Dio** — et MapLibre ne passe pas par Dio : il va
// chercher ses tuiles par sa propre pile HTTP native, à laquelle aucun
// intercepteur Dart n'est branché. Les tuiles reviendraient donc en 401, et la
// carte de masse **n'est pas réalisable en l'état** (§1.0.3-8).
//
// Ce qui est livré à la place : le **repli GeoJSON de proximité**, qui interroge
// la liste paginée des parcours filtrée par `nearLat` / `nearLon` /
// `nearRadius` — trois paramètres déjà supportés par l'API, et qui passent par
// Dio, donc authentifiés. Plafond assumé : quelques centaines de tracés dans la
// vue courante, contre 2 585 côté web. Le plafond est **rendu visible**
// ([PdlMassResult.truncated]) : une carte tronquée en silence se lit comme une
// carte complète.
//
// Ce qui lèverait le blocage, et que le §5.2 du plan liste comme évolution
// d'API : une **URL de tuile signée à durée courte**, du type
// `/api/routes/tiles/{z}/{x}/{y}.mvt?t=<jeton>`, obtenue par un appel Dio
// authentifié et renouvelée avant expiration. Le jour où elle existe, la
// bascule est déjà écrite : [PdlMassTiles], derrière son drapeau
// [PdlMassTiles.enabled]. Rien d'autre à changer que la fabrique d'URL.
// ─────────────────────────────────────────────────────────────────────────────

import 'dart:async';
import 'dart:math' as math;

import 'package:flutter/foundation.dart';
import 'package:maplibre/maplibre.dart';

import 'pdl_map_controller.dart';

/// Ce qu'une carte demande à sa source de masse : la zone visible, et le
/// plafond qu'elle accepte.
@immutable
class PdlMassRequest {
  const PdlMassRequest({required this.box, required this.limit});

  final PdlMapBox box;

  /// Nombre maximal de tracés à rendre. Au-delà, la source **doit** couper et
  /// le signaler.
  final int limit;

  /// Centre de la zone visible — `nearLat` / `nearLon`.
  double get centerLat => (box.minLat + box.maxLat) / 2;
  double get centerLon => (box.minLon + box.maxLon) / 2;

  /// Rayon couvrant la zone visible, en mètres — `nearRadius`.
  ///
  /// La demi-diagonale, pas la demi-largeur : un rayon plus court laisserait
  /// les coins de l'écran vides, ce qui se lit comme « il n'y a rien ici ».
  double get radiusMeters =>
      _haversine(centerLon, centerLat, box.maxLon, box.maxLat);
}

/// Ce qu'une source de masse rend.
@immutable
class PdlMassResult {
  const PdlMassResult({
    required this.tracks,
    this.truncated = false,
    this.total,
  });

  const PdlMassResult.empty()
    : tracks = const <PdlMapTrack>[],
      truncated = false,
      total = 0;

  final List<PdlMapTrack> tracks;

  /// Vrai quand la zone contient plus de tracés que le plafond. L'écran **doit**
  /// le dire à l'utilisateur (« 412 parcours ici, 120 affichés »).
  final bool truncated;

  /// Nombre total de tracés dans la zone, quand la source le connaît.
  final int? total;
}

/// La source de masse. Elle vit dans `features/`, pas ici : `core/pdl` ne parle
/// à aucune API. Voir `features/routes/data/routes_mass_repository.dart`.
typedef PdlMassFetcher = Future<PdlMassResult> Function(PdlMassRequest request);

/// Le repli GeoJSON de proximité, piloté par la caméra.
///
/// Branché sur `PdlMap.onCameraIdle`, il redemande les tracés de la zone
/// visible, en débounçant et en **abandonnant la réponse d'une requête
/// périmée** : sur une carte qu'on déplace, les réponses reviennent dans le
/// désordre, et sans ce garde-fou la carte affiche la zone d'où l'on vient.
class PdlMassLayerController extends ChangeNotifier {
  PdlMassLayerController({
    required this.fetcher,
    this.limit = 120,
    this.debounce = const Duration(milliseconds: 400),
    this.autoReload = false,
  });

  final PdlMassFetcher fetcher;

  /// Le plafond du repli. 120 tracés, c'est déjà ~120 appels de détail (voir
  /// le dépôt côté feature) : au-delà, l'écran devient hostile bien avant que
  /// MapLibre ne peine.
  final int limit;

  final Duration debounce;

  /// Quand faux — le défaut, et le comportement de l'écran 21 — un déplacement
  /// de caméra **ne recharge rien** : il arme « Rechercher dans cette zone ».
  /// C'est un choix de données mobiles autant que d'ergonomie.
  final bool autoReload;

  List<PdlMapTrack> _tracks = const <PdlMapTrack>[];
  bool _loading = false;
  bool _truncated = false;
  int? _total;
  Object? _error;
  PdlMapBox? _pendingBox;
  PdlMapBox? _loadedBox;
  Timer? _timer;
  int _generation = 0;

  List<PdlMapTrack> get tracks => _tracks;
  bool get loading => _loading;

  /// Vrai quand la zone contient plus de tracés que [limit].
  bool get truncated => _truncated;
  int? get total => _total;
  Object? get error => _error;

  /// La zone effectivement rendue, ou `null` avant le premier chargement.
  PdlMapBox? get loadedBox => _loadedBox;

  /// Vrai quand la caméra a bougé depuis le dernier chargement : c'est ce qui
  /// fait apparaître la pilule « Rechercher dans cette zone ».
  bool get isStale => _pendingBox != null && _pendingBox != _loadedBox;

  void onCameraIdle(PdlMapBox box) {
    _pendingBox = box;
    if (_loadedBox == null || autoReload) {
      _schedule(box);
    } else {
      notifyListeners();
    }
  }

  /// « Rechercher dans cette zone ».
  Future<void> searchHere() {
    final PdlMapBox? box = _pendingBox ?? _loadedBox;
    if (box == null) return Future<void>.value();
    _timer?.cancel();
    return _load(box);
  }

  void _schedule(PdlMapBox box) {
    _timer?.cancel();
    _timer = Timer(debounce, () => _load(box));
  }

  Future<void> _load(PdlMapBox box) async {
    final int generation = ++_generation;
    _loading = true;
    _error = null;
    notifyListeners();
    try {
      final PdlMassResult result = await fetcher(
        PdlMassRequest(box: box, limit: limit),
      );
      if (generation != _generation) return; // réponse périmée
      _tracks = result.tracks;
      _truncated = result.truncated;
      _total = result.total;
      _loadedBox = box;
    } catch (e) {
      if (generation != _generation) return;
      _error = e;
    } finally {
      if (generation == _generation) {
        _loading = false;
        notifyListeners();
      }
    }
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }
}

/// La bascule vers les vraies tuiles vectorielles, **derrière son drapeau**.
///
/// Elle est écrite, testée pour ce qu'elle peut l'être hors appareil, et
/// inactive : `PdlMassTiles.enabled` vaut faux tant que
/// `--dart-define=PDL_MASS_TILES=true` n'est pas passé, et l'activer sans une
/// URL de tuile authentifiable ne produit qu'une carte vide et des 401.
abstract final class PdlMassTiles {
  /// Le drapeau. Compilé, donc élidé du binaire quand il est faux.
  static const bool enabled = bool.fromEnvironment('PDL_MASS_TILES');

  /// La couche de la tuile vectorielle servie par le backend.
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

const double _earthRadiusMeters = 6371000.0;
const double _degToRad = math.pi / 180.0;

double _haversine(double lon1, double lat1, double lon2, double lat2) {
  final double dLat = (lat2 - lat1) * _degToRad;
  final double dLon = (lon2 - lon1) * _degToRad;
  final double a =
      math.sin(dLat / 2) * math.sin(dLat / 2) +
      math.cos(lat1 * _degToRad) *
          math.cos(lat2 * _degToRad) *
          math.sin(dLon / 2) *
          math.sin(dLon / 2);
  return _earthRadiusMeters * 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a));
}
