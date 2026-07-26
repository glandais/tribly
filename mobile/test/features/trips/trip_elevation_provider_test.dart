import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/api/pedalons_api_client.dart';
import 'package:pedalons/core/pdl/elevation/elevation_samples.dart';
import 'package:pedalons/features/trips/providers/trip_detail_provider.dart';
import 'package:pedalons/features/trips/providers/trip_elevation_provider.dart';

import 'trip_fixtures.dart';

/// S24-4 — le profil global d'un voyage.
///
/// Il n'existe pas d'endpoint de profil multi-parcours : celui du voyage est la
/// concaténation de N profils d'étape. Ce qui se vérifie ici est ce qui rend
/// cette concaténation honnête — le budget d'échantillons réparti au prorata de
/// la distance, le décalage de chaque segment par la distance cumulée, et le
/// **trou signalé** quand une étape manque, plutôt qu'un profil raccourci qui
/// se ferait passer pour complet.
class _FakeRoutesClient implements RoutesClient {
  _FakeRoutesClient({this.failFor = const <String>{}});

  final Set<String> failFor;

  /// Le nombre d'échantillons demandé par parcours.
  final Map<String, int> samplesAsked = <String, int>{};

  @override
  Future<ElevationProfileDto> getRouteElevationProfile({
    required String routeSlug,
    required String teamSlug,
    int? samples = 300,
    // ignore: strict_raw_type
  }) async {
    samplesAsked[routeSlug] = samples ?? -1;
    if (failFor.contains(routeSlug)) throw StateError('boom');

    // Un profil plat de quatre points sur la distance de l'étape.
    final double distance = _distances[routeSlug]!;
    return ElevationProfileDto(
      routeId: 'r-$routeSlug',
      slug: routeSlug,
      distance: distance,
      minElevation: 100,
      maxElevation: 200,
      samples: 4,
      points: <ElevationPointDto>[
        for (int i = 0; i < 4; i++)
          ElevationPointDto(
            distance: distance * i / 3,
            elevation: 100 + i * 10,
            grade: 1,
          ),
      ],
    );
  }

  static const Map<String, double> _distances = <String, double>{
    'etape-1': 100000,
    'etape-2': 50000,
    'etape-3': 50000,
  };

  @override
  dynamic noSuchMethod(Invocation invocation) =>
      throw UnimplementedError('${invocation.memberName} inattendu');
}

void main() {
  const TripKey key = TripKey(teamSlug: 'n-peloton', tripSlug: 'gtmc-bromance');

  TripDto trip() => fixtureTrip(
    totalDistance: 200000,
    stages: <TripStageDto>[
      fixtureStage(
        index: 1,
        route: fixtureStageRoute(slug: 'etape-1', distance: 100000),
      ),
      fixtureStage(
        index: 2,
        route: fixtureStageRoute(slug: 'etape-2', distance: 50000),
      ),
      fixtureStage(
        index: 3,
        route: fixtureStageRoute(slug: 'etape-3', distance: 50000),
      ),
    ],
  );

  Future<(TripElevation?, _FakeRoutesClient)> load({
    Set<String> failFor = const <String>{},
  }) async {
    final _FakeRoutesClient client = _FakeRoutesClient(failFor: failFor);
    final ProviderContainer container = ProviderContainer(
      overrides: [
        routesClientProvider.overrideWithValue(client),
        tripDetailProvider(key).overrideWith((Ref ref) async => trip()),
      ],
    );
    addTearDown(container.dispose);
    final TripElevation? result = await container.read(
      tripElevationProvider(key).future,
    );
    return (result, client);
  }

  test('le budget se répartit au prorata de la distance', () async {
    final (TripElevation? profile, _FakeRoutesClient client) = await load();

    // 240 points pour 200 km : 120 pour l'étape de 100 km, 60 pour chacune des
    // deux de 50 km.
    expect(client.samplesAsked['etape-1'], 120);
    expect(client.samplesAsked['etape-2'], 60);
    expect(client.samplesAsked['etape-3'], 60);
    expect(profile, isNotNull);
  });

  test('chaque segment est décalé de la distance cumulée', () async {
    final (TripElevation? profile, _) = await load();
    final List<ElevationPoint> points = profile!.samples.points;

    expect(points.first.distance, 0);
    // Les points sont strictement croissants : sans décalage, l'étape 2
    // repartirait de zéro et le profil se replierait sur lui-même.
    for (int i = 1; i < points.length; i++) {
      expect(
        points[i].distance,
        greaterThan(points[i - 1].distance),
        reason: 'point $i',
      );
    }
    expect(points.last.distance, closeTo(200000, 1));
    expect(profile.samples.totalDistance, 200000);
    expect(profile.hasGaps, isFalse);
  });

  test(
    'une étape en échec devient un trou signalé, pas un raccourci',
    () async {
      final (TripElevation? profile, _) = await load(
        failFor: <String>{'etape-2'},
      );

      expect(profile!.hasGaps, isTrue);
      expect(profile.gaps, <String>['J2']);
      // L'axe avance quand même de la distance de l'étape manquante : l'étape 3
      // reste à sa place, entre 150 et 200 km.
      expect(profile.samples.points.last.distance, closeTo(200000, 1));
      expect(profile.samples.totalDistance, 200000);
    },
  );

  test('un voyage sans étape routée n\'a pas de profil', () async {
    final ProviderContainer container = ProviderContainer(
      overrides: [
        routesClientProvider.overrideWithValue(_FakeRoutesClient()),
        tripDetailProvider(key).overrideWith(
          (Ref ref) async => fixtureTrip(
            stages: <TripStageDto>[fixtureStage(index: 1)],
            totalDistance: null,
          ),
        ),
      ],
    );
    addTearDown(container.dispose);

    expect(await container.read(tripElevationProvider(key).future), isNull);
  });
}
