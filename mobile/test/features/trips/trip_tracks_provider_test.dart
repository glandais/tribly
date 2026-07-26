import 'dart:async';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/features/routes/data/route_repository.dart';
import 'package:pedalons/features/trips/providers/trip_detail_provider.dart';
import 'package:pedalons/features/trips/providers/trip_tracks_provider.dart';

import 'trip_fixtures.dart';

/// S24-3 — le chargement des tracés d'un voyage.
///
/// Faute d'endpoint de carte multi-entités, le tracé global coûte un appel par
/// étape. Trois garde-fous se vérifient ici : la concurrence est **plafonnée à
/// quatre**, le nombre d'étapes chargées est **plafonné à douze** et le
/// dépassement est visible dans l'état, et deux étapes qui partagent un
/// parcours ne coûtent **qu'un** appel.
class _CountingRouteRepository implements RouteRepository {
  _CountingRouteRepository({this.failFor = const <String>{}});

  final Set<String> failFor;

  final List<String> requested = <String>[];
  int inFlight = 0;
  int peakInFlight = 0;

  /// Retient les appels jusqu'à ce que le test libère, pour mesurer le pic.
  Completer<void>? gate;

  @override
  Future<RouteDetailDto> getRouteDetail(
    String teamSlug,
    String routeSlug, {
    double? simplify,
    int? points,
  }) async {
    requested.add(routeSlug);
    inFlight++;
    peakInFlight = inFlight > peakInFlight ? inFlight : peakInFlight;
    try {
      if (gate != null) await gate!.future;
      if (failFor.contains(routeSlug)) throw StateError('boom');
      return RouteDetailDto(
        id: 'r-$routeSlug',
        slug: routeSlug,
        team: kFixtureTeam,
        name: routeSlug,
        media: kEmptyMedia,
        distance: 50000,
        elevationGain: 800,
        elevationLoss: 800,
        surfaceType: 'GRAVEL',
        visibility: 'PUBLIC',
        createdBy: const PublicUserDto(id: 'u9', displayName: 'Créatrice'),
        createdAt: '2026-01-01T10:00:00Z',
        updatedAt: '2026-01-01T10:00:00Z',
        deleted: false,
        tracks: const <TrackDto>[],
        waypoints: const <WaypointDto>[],
      );
    } finally {
      inFlight--;
    }
  }

  @override
  dynamic noSuchMethod(Invocation invocation) =>
      throw UnimplementedError('${invocation.memberName} inattendu');
}

TripDto _tripWithStages(int count) => fixtureTrip(
  stages: <TripStageDto>[
    for (int i = 1; i <= count; i++)
      fixtureStage(
        index: i,
        stageCount: count,
        route: fixtureStageRoute(slug: 'etape-$i', name: 'Étape $i'),
      ),
  ],
);

void main() {
  const TripKey key = TripKey(teamSlug: 'n-peloton', tripSlug: 'gtmc-bromance');

  Future<(ProviderContainer, _CountingRouteRepository)> boot(
    TripDto trip, {
    Set<String> failFor = const <String>{},
    bool hold = false,
  }) async {
    final _CountingRouteRepository repo = _CountingRouteRepository(
      failFor: failFor,
    );
    // La porte se pose **avant** que le contrôleur ne démarre : le chargement
    // part dès que le détail arrive, donc au milieu de ce `boot`.
    if (hold) repo.gate = Completer<void>();
    final ProviderContainer container = ProviderContainer(
      overrides: [
        routeRepositoryProvider.overrideWithValue(repo),
        tripDetailProvider(key).overrideWith((Ref ref) async => trip),
      ],
    );
    addTearDown(container.dispose);
    container.listen(
      tripTracksProvider(key),
      (TripTracksState? previous, TripTracksState next) {},
      fireImmediately: true,
    );
    await container.read(tripDetailProvider(key).future);
    return (container, repo);
  }

  test('jamais plus de quatre requêtes en vol', () async {
    final (ProviderContainer container, _CountingRouteRepository repo) =
        await boot(_tripWithStages(10), hold: true);

    // Les quatre ouvriers démarrent, puis butent tous sur la porte : dix
    // étapes sont en file, quatre seulement sont en vol.
    await Future<void>.delayed(const Duration(milliseconds: 10));
    expect(repo.inFlight, kTripTrackConcurrency);
    expect(repo.requested.length, kTripTrackConcurrency);

    repo.gate!.complete();
    await Future<void>.delayed(const Duration(milliseconds: 20));

    expect(repo.requested.length, 10, reason: 'la file se vide entièrement');
    expect(repo.peakInFlight, kTripTrackConcurrency);
  });

  test('au-delà de douze étapes, le plafond est chargé et signalé', () async {
    final (ProviderContainer container, _CountingRouteRepository repo) =
        await boot(_tripWithStages(18));
    await Future<void>.delayed(const Duration(milliseconds: 20));

    final TripTracksState state = container.read(tripTracksProvider(key));
    expect(state.total, 18);
    expect(state.requested, kTripTrackStageCap);
    expect(state.truncated, isTrue, reason: 'jamais silencieux');
    expect(repo.requested.length, kTripTrackStageCap);
  });

  test('deux étapes sur le même parcours ne coûtent qu\'un appel', () async {
    final RouteDto shared = fixtureStageRoute(slug: 'aller-retour');
    final (
      ProviderContainer container,
      _CountingRouteRepository repo,
    ) = await boot(
      fixtureTrip(
        stages: <TripStageDto>[
          fixtureStage(index: 1, route: shared),
          fixtureStage(index: 2, route: shared),
        ],
      ),
    );
    await Future<void>.delayed(const Duration(milliseconds: 20));

    expect(repo.requested, <String>['aller-retour']);
    expect(container.read(tripTracksProvider(key)).total, 1);
  });

  test('un parcours en échec est retenu, pas perdu', () async {
    final (ProviderContainer container, _CountingRouteRepository repo) =
        await boot(_tripWithStages(3), failFor: <String>{'etape-2'});
    await Future<void>.delayed(const Duration(milliseconds: 20));

    final TripTracksState state = container.read(tripTracksProvider(key));
    expect(state.failed, <String>{'etape-2'});
    expect(state.geometries.keys, containsAll(<String>['etape-1', 'etape-3']));
    expect(state.isLoading, isFalse);
  });

  test('les étapes partent dans l\'ordre du voyage', () async {
    final (
      ProviderContainer container,
      _CountingRouteRepository repo,
    ) = await boot(
      fixtureTrip(
        stages: <TripStageDto>[
          fixtureStage(index: 3, route: fixtureStageRoute(slug: 'etape-3')),
          fixtureStage(index: 1, route: fixtureStageRoute(slug: 'etape-1')),
          fixtureStage(index: 2, route: fixtureStageRoute(slug: 'etape-2')),
        ],
      ),
    );
    await Future<void>.delayed(const Duration(milliseconds: 20));

    expect(repo.requested, <String>['etape-1', 'etape-2', 'etape-3']);
  });
}
