import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/core/pdl/pdl.dart';
import 'package:pedalons/core/preferences/user_preferences_provider.dart';
import 'package:pedalons/core/theme/pedalons_theme.dart';
import 'package:pedalons/features/rides/data/ride_repository.dart';
import 'package:pedalons/features/rides/presentation/pages/ride_weather_page.dart';
import 'package:pedalons/features/rides/providers/ride_detail_provider.dart';
import 'package:pedalons/features/rides/providers/ride_group_selection_provider.dart';
import 'package:pedalons/features/teams/data/team_repository.dart';
import 'package:pedalons/keys.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../support/localization.dart';
import 'ride_fixtures.dart';

/// « Météo du parcours » : le sélecteur de groupe, la frise, l'alerte,
/// l'exposition, et les replis — statut d'étape `NO_LOCATION`, valeurs
/// d'enum inconnues, système impérial.
class _Repository implements RideRepository {
  _Repository(this.ride, this.weather);

  final RideDto ride;
  final RideWeatherDto weather;

  @override
  Future<RideDto> getRide(String teamSlug, String rideSlug) async => ride;

  @override
  Future<RideWeatherDto> getRideWeather(String t, String r) async => weather;

  @override
  dynamic noSuchMethod(Invocation invocation) => super.noSuchMethod(invocation);
}

class _Teams implements TeamRepository {
  @override
  Future<List<TeamDetailDto>> getMyTeams() async => <TeamDetailDto>[];

  @override
  dynamic noSuchMethod(Invocation invocation) => super.noSuchMethod(invocation);
}

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();
  setUpAll(loadTestTranslations);

  late SharedPreferences prefs;
  setUp(() async {
    SharedPreferences.setMockInitialValues(<String, Object>{});
    prefs = await SharedPreferences.getInstance();
  });

  final RideDto ride = fixtureRide(
    groups: <RideGroupDto>[
      fixtureGroup(id: 'g1', name: 'Rapide'),
      fixtureGroup(id: 'g2', name: 'Cool', sortOrder: 1),
    ],
  );
  const RideKey key = RideKey(teamSlug: 'n-peloton', rideSlug: 'np-665');

  RideWeatherDto weather({String status = 'OK', List<WeatherLegDto>? legs}) =>
      fixtureWeather(
        status: status,
        departure: fixtureConditions(),
        fetchedAt: '2099-07-29T17:00:00Z',
        legs:
            legs ??
            <WeatherLegDto>[
              fixtureLeg(
                groupId: 'g1',
                rainAlert: const WeatherRainAlertDto(
                  probability: 70,
                  time: '2099-07-29T20:05:00Z',
                  condition: 'RAIN',
                  distance: 15000,
                ),
              ),
              fixtureLeg(
                groupId: 'g2',
                speedIsDefault: true,
                relativeWind: 'CROSS',
              ),
            ],
      );

  late ProviderContainer container;

  Future<void> open(
    WidgetTester tester,
    RideWeatherDto dto, {
    UnitSystem units = UnitSystem.metric,
  }) async {
    tester.view.physicalSize = const Size(402 * 3, 2400 * 3);
    tester.view.devicePixelRatio = 3;
    addTearDown(tester.view.reset);

    container = ProviderContainer(
      retry: (int _, Object _) => null,
      overrides: [
        sharedPreferencesProvider.overrideWithValue(prefs),
        unitSystemProvider.overrideWithValue(units),
        rideRepositoryProvider.overrideWithValue(_Repository(ride, dto)),
        teamRepositoryProvider.overrideWithValue(_Teams()),
      ],
    );
    addTearDown(container.dispose);
    // Le détail est déjà chargé quand l'écran est poussé : il y lit le nom
    // des groupes.
    container.listen(rideDetailProvider(key), (_, _) {});
    await container.read(rideDetailProvider(key).future);

    await tester.pumpWidget(
      UncontrolledProviderScope(
        container: container,
        child: MaterialApp(
          theme: PedalonsTheme.build(Brightness.light),
          home: const RideWeatherPage(rideKey: key),
        ),
      ),
    );
    for (int i = 0; i < 4; i++) {
      await tester.pump(const Duration(milliseconds: 10));
    }
  }

  testWidgets('le départ, l\'alerte, l\'exposition et la frise', (
    WidgetTester tester,
  ) async {
    await open(tester, weather());

    expect(find.text('Météo du parcours'), findsOneWidget);
    expect(find.text('Météo au départ'), findsOneWidget);
    expect(find.text('14 °C'), findsWidgets);
    expect(find.textContaining('Pluie probable (70 %)'), findsOneWidget);
    expect(find.text('Exposition au vent'), findsOneWidget);
    expect(find.byType(PdlSegmentBar), findsOneWidget);
    expect(find.byKey(keys.ride.weatherCheckpoint(0)), findsOneWidget);
    expect(find.byKey(keys.ride.weatherCheckpoint(1)), findsOneWidget);
    expect(find.byKey(keys.ride.weatherCheckpoint(2)), findsOneWidget);
    // L'arrivée n'a pas encore de prévision : la frise le dit.
    expect(find.text('Prévision pas encore disponible ici'), findsOneWidget);
    expect(find.text('FACE'), findsOneWidget);
    expect(find.text('DOS'), findsOneWidget);
    expect(find.text('Prévisions : Open-Meteo.com'), findsOneWidget);
    expect(tester.takeException(), isNull);
  });

  testWidgets('le sélecteur change de groupe, et le partage avec le détail', (
    WidgetTester tester,
  ) async {
    await open(tester, weather());

    expect(find.byKey(keys.ride.weatherGroupChip('g1')), findsOneWidget);
    expect(find.byKey(keys.ride.weatherGroupChip('g2')), findsOneWidget);
    // Le premier groupe par défaut : son alerte pluie est là.
    expect(find.textContaining('Pluie probable'), findsOneWidget);
    expect(find.textContaining('vitesse par défaut'), findsNothing);

    await tester.tap(find.byKey(keys.ride.weatherGroupChip('g2')));
    await tester.pump();

    expect(container.read(selectedRideGroupProvider(key)), 'g2');
    expect(find.textContaining('Pluie probable'), findsNothing);
    // 25 km/h par défaut : dit à l'écran.
    expect(find.textContaining('vitesse par défaut'), findsOneWidget);
    expect(find.text('TRAVERS'), findsOneWidget);
  });

  testWidgets('STALE se signale par un bandeau', (WidgetTester tester) async {
    await open(tester, weather(status: 'STALE'));

    expect(
      find.textContaining('Prévision ancienne', findRichText: true),
      findsOneWidget,
    );
  });

  testWidgets('une étape sans parcours dit qu\'il n\'y a que le départ', (
    WidgetTester tester,
  ) async {
    await open(
      tester,
      weather(
        legs: <WeatherLegDto>[
          WeatherLegDto(
            status: 'NO_LOCATION',
            groupId: 'g1',
            startTime: '2099-07-29T19:30:00Z',
            averageSpeed: 25,
            speedIsDefault: true,
            distance: 0,
            arrivalTime: '2099-07-29T19:30:00Z',
            checkpoints: const <WeatherCheckpointDto>[],
            segments: const <WindSegmentDto>[],
            windExposure: const WindExposureDto(head: 0, cross: 0, tail: 0),
          ),
        ],
      ),
    );

    expect(
      find.textContaining('n\'a pas de parcours', findRichText: true),
      findsOneWidget,
    );
    expect(find.byType(PdlSegmentBar), findsNothing);
    expect(tester.takeException(), isNull);
  });

  for (final String legStatus in <String>[
    'UNAVAILABLE',
    'NOT_YET_AVAILABLE',
    'SOMETHING_NEW',
  ]) {
    testWidgets('une étape $legStatus : un bandeau, pas de frise', (
      WidgetTester tester,
    ) async {
      await open(
        tester,
        weather(
          legs: <WeatherLegDto>[
            fixtureLeg(
              status: legStatus,
              rainAlert: const WeatherRainAlertDto(
                probability: 70,
                time: '2099-07-29T20:05:00Z',
                condition: 'RAIN',
                distance: 15000,
              ),
            ),
          ],
        ),
      );

      expect(find.byKey(keys.ride.weatherLegUnavailable), findsOneWidget);
      expect(
        find.text(
          'Les prévisions le long du parcours ne sont pas encore disponibles.',
        ),
        findsOneWidget,
      );
      expect(find.byKey(keys.ride.weatherCheckpoint(0)), findsNothing);
      expect(find.textContaining('Pluie probable'), findsNothing);
      expect(find.byType(PdlSegmentBar), findsNothing);
      // Le départ reste affiché : la sortie, elle, a sa prévision.
      expect(find.text('Météo au départ'), findsOneWidget);
      expect(tester.takeException(), isNull);
    });
  }

  testWidgets('une étape OK sans aucun point : le même bandeau', (
    WidgetTester tester,
  ) async {
    await open(
      tester,
      weather(
        legs: <WeatherLegDto>[
          fixtureLeg().copyWith(checkpoints: const <WeatherCheckpointDto>[]),
        ],
      ),
    );

    expect(find.byKey(keys.ride.weatherLegUnavailable), findsOneWidget);
    expect(tester.takeException(), isNull);
  });

  testWidgets('les valeurs d\'enum inconnues se replient sans planter', (
    WidgetTester tester,
  ) async {
    final WeatherLegDto leg = fixtureLeg(relativeWind: 'SWIRL');
    await open(
      tester,
      fixtureWeather(
        status: 'OK',
        departure: fixtureConditions(condition: 'METEOR_SHOWER', compass: '?'),
        legs: <WeatherLegDto>[
          leg.copyWith(
            checkpoints: <WeatherCheckpointDto>[
              leg.checkpoints.first.copyWith(kind: 'SOMEWHERE'),
            ],
          ),
        ],
      ),
    );

    // Condition inconnue : un nuage et « Nuageux ».
    expect(find.text('Nuageux'), findsWidgets);
    // Point cardinal inconnu : le vent sans lui.
    expect(find.textContaining('Vent 18 km/h'), findsWidgets);
    // Vent relatif inconnu : ni badge ni libellé.
    expect(find.text('FACE'), findsNothing);
    expect(find.text('En route'), findsOneWidget);
    expect(tester.takeException(), isNull);
  });

  testWidgets('en impérial, des °F et des mph', (WidgetTester tester) async {
    await open(tester, weather(), units: UnitSystem.imperial);

    // 14,4 °C → 57,9 °F.
    expect(find.text('58 °F'), findsWidgets);
    expect(find.textContaining('mph'), findsWidgets);
    expect(find.textContaining('°C'), findsNothing);
  });

  testWidgets('UNAVAILABLE : un état d\'erreur et « Réessayer »', (
    WidgetTester tester,
  ) async {
    await open(tester, fixtureWeather(status: 'UNAVAILABLE'));

    expect(find.byKey(keys.ride.weatherUnavailable), findsOneWidget);
    expect(find.byKey(keys.ride.weatherRetryButton), findsOneWidget);
  });
}
