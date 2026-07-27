import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/legacy.dart';

import '../../../api/generated/export.dart';
import '../../routes/data/route_repository.dart';
import 'trip_detail_provider.dart';

/// Au-delà de ce nombre d'étapes, on ne charge que les premières.
///
/// Il n'existe **pas d'endpoint de carte multi-entités** (§5.2) : le tracé
/// global d'un voyage coûte un `getRoute` par étape. Douze appels sont déjà
/// beaucoup ; trente sur un réseau mobile seraient une page qui ne finit pas de
/// charger. Le plafond est **signalé à l'utilisateur**, jamais silencieux.
const int kTripTrackStageCap = 12;

/// Requêtes simultanées pour le profil altimétrique (`trip_elevation_provider.dart`).
///
/// Les tracés, eux, tiennent en **un seul appel groupé** (`getRoutesBulk`) — ce
/// plafond ne les concerne plus. Il reste défini ici parce que le profil
/// partage le plafond d'étapes [kTripTrackStageCap] et continue d'appeler
/// `getRouteElevationProfile` par étape (budget de points réparti
/// proportionnellement à la distance, incompatible avec le pas unique de
/// `elevationSamples` du lot — voir le commentaire de tête de ce fichier-là).
const int kTripTrackConcurrency = 4;

/// Simplification agressive : le tracé global est vu à l'échelle d'une région,
/// pas d'un virage. Vingt-cinq mètres de tolérance sont invisibles à ce
/// cadrage, et le plafond de sommets borne le pire cas — sept étapes de
/// 3 000 points feraient sinon 21 000 points pour 350 px de carte.
const double kTripTrackSimplify = 25;
const int kTripTrackPoints = 600;

/// Ce que la carte d'un voyage sait, à un instant donné.
@immutable
class TripTracksState {
  const TripTracksState({
    this.geometries = const <String, RouteDetailDto>{},
    this.failed = const <String>{},
    this.requested = 0,
    this.total = 0,
  });

  /// Les géométries chargées, par `routeSlug`. **Dédoublonnées** : deux étapes
  /// qui partagent un parcours coûtent un appel, pas deux.
  final Map<String, RouteDetailDto> geometries;

  /// Les parcours dont l'appel a échoué. Ils manquent à la carte comme au
  /// profil, et c'est dit — un trou signalé vaut mieux qu'un tracé amputé qui
  /// se fait passer pour complet.
  final Set<String> failed;

  /// Le nombre de parcours qu'on charge : `total`, ou le plafond.
  final int requested;

  /// Le nombre de parcours distincts qu'ont les étapes du voyage.
  final int total;

  bool get truncated => total > requested;

  bool get isLoading => geometries.length + failed.length < requested;

  TripTracksState copyWith({
    Map<String, RouteDetailDto>? geometries,
    Set<String>? failed,
    int? requested,
    int? total,
  }) => TripTracksState(
    geometries: geometries ?? this.geometries,
    failed: failed ?? this.failed,
    requested: requested ?? this.requested,
    total: total ?? this.total,
  );
}

/// Les géométries des étapes d'un voyage, chargées **progressivement**.
///
/// L'écran ne bloque pas sur les N appels : la vignette statique tient le hero
/// pendant que la carte interactive se remplit étape par étape, dans l'ordre.
final tripTracksProvider = StateNotifierProvider.autoDispose
    .family<TripTracksController, TripTracksState, TripKey>((
      Ref ref,
      TripKey key,
    ) {
      final TripTracksController controller = TripTracksController(ref, key);
      ref.listen<AsyncValue<TripDto>>(tripDetailProvider(key), (
        AsyncValue<TripDto>? _,
        AsyncValue<TripDto> next,
      ) {
        final TripDto? trip = next.value;
        if (trip != null) controller.loadFor(trip);
      }, fireImmediately: true);
      return controller;
    });

class TripTracksController extends StateNotifier<TripTracksState> {
  TripTracksController(this._ref, this._key) : super(const TripTracksState());

  final Ref _ref;
  final TripKey _key;

  /// Les slugs déjà demandés — le `ref.listen` refire à chaque invalidation du
  /// détail (après une inscription, par exemple), et retélécharger sept
  /// géométries parce qu'un compteur de participants a bougé serait absurde.
  final Set<String> _requestedSlugs = <String>{};

  void loadFor(TripDto trip) {
    final List<String> ordered = <String>[];
    for (final TripStageDto stage in trip.orderedStages) {
      final String? slug = stage.route?.slug;
      if (slug != null && !ordered.contains(slug)) ordered.add(slug);
    }

    final List<String> wanted = ordered.take(kTripTrackStageCap).toList();
    state = state.copyWith(total: ordered.length, requested: wanted.length);

    final List<String> todo = wanted
        .where((String slug) => !_requestedSlugs.contains(slug))
        .toList();
    if (todo.isEmpty) return;
    _requestedSlugs.addAll(todo);
    unawaited(_load(todo));
  }

  /// Charge [slugs] en **un seul appel groupé**, au lieu d'un `getRouteDetail`
  /// par étape.
  ///
  /// Un slug absent de `response.routes` (inconnu, ou illisible par
  /// l'appelant — le lot ne fait jamais 404 pour ça) rejoint [failed] comme
  /// l'aurait fait un appel individuel en échec ; un échec du lot entier (réseau,
  /// équipe introuvable) y met tous les slugs demandés.
  Future<void> _load(List<String> slugs) async {
    try {
      final RoutesBulkResponse response = await _ref
          .read(routeRepositoryProvider)
          .getRoutesBulk(
            _key.teamSlug,
            slugs,
            simplify: kTripTrackSimplify,
            points: kTripTrackPoints,
          );
      if (!mounted) return;
      final Map<String, RouteDetailDto> loaded = <String, RouteDetailDto>{
        for (final RouteDetailDto route in response.routes) route.slug: route,
      };
      final Set<String> missing = slugs
          .where((String slug) => !loaded.containsKey(slug))
          .toSet();
      state = state.copyWith(
        geometries: <String, RouteDetailDto>{...state.geometries, ...loaded},
        failed: <String>{...state.failed, ...missing},
      );
    } catch (_) {
      if (!mounted) return;
      state = state.copyWith(failed: <String>{...state.failed, ...slugs});
    }
  }

  /// Réessaie ce qui a échoué.
  void retryFailed() {
    final List<String> failed = state.failed.toList();
    if (failed.isEmpty) return;
    state = state.copyWith(failed: const <String>{});
    unawaited(_load(failed));
  }
}
