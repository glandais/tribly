import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/core/preferences/user_preferences_provider.dart';
import 'package:pedalons/core/theme/pedalons_theme.dart';
import 'package:pedalons/features/rides/presentation/widgets/ride_weather_summary_line.dart';
import 'package:pedalons/features/rides/presentation/widgets/ride_weather_widgets.dart';
import 'package:pedalons/keys.dart';

import '../../support/localization.dart';

/// La ligne météo des cartes de sortie : plage min → max, vent, pluie, et
/// rien quand il n'y a rien à dire.
void main() {
  TestWidgetsFlutterBinding.ensureInitialized();
  setUpAll(loadTestTranslations);

  Future<void> pump(
    WidgetTester tester,
    RideWeatherSummaryDto? summary, {
    bool finished = false,
    UnitSystem units = UnitSystem.metric,
  }) async {
    await tester.pumpWidget(
      ProviderScope(
        overrides: [unitSystemProvider.overrideWithValue(units)],
        child: MaterialApp(
          theme: PedalonsTheme.build(Brightness.light),
          home: Scaffold(
            body: RideWeatherSummaryLine(summary: summary, finished: finished),
          ),
        ),
      ),
    );
  }

  const RideWeatherSummaryDto ok = RideWeatherSummaryDto(
    status: 'OK',
    condition: 'CLEAR',
    daylight: true,
    temperature: 13,
    temperatureMin: 11.6,
    temperatureMax: 18.2,
    maxPrecipitationProbability: 20,
    wind: WindDto(speed: 18, direction: 315, compass: 'NW'),
  );

  testWidgets('la plage, le vent et la pluie', (WidgetTester tester) async {
    await pump(tester, ok);

    expect(find.byKey(keys.ride.weatherSummary), findsOneWidget);
    expect(find.text('12–18 °C'), findsOneWidget);
    expect(find.text('Vent NO 18 km/h'), findsOneWidget);
    expect(find.text('Pluie 20 %'), findsOneWidget);
  });

  testWidgets('l\'alerte pluie l\'emporte sur la probabilité maximale', (
    WidgetTester tester,
  ) async {
    await pump(
      tester,
      ok.copyWith(
        rainAlert: const WeatherRainAlertDto(
          probability: 60,
          time: '2099-07-29T10:00:00Z',
          condition: 'RAIN',
        ),
      ),
    );

    expect(find.textContaining('Pluie 60 % vers'), findsOneWidget);
    expect(find.text('Pluie 20 %'), findsNothing);
  });

  testWidgets('STALE se signale, OK non', (WidgetTester tester) async {
    final SemanticsHandle semantics = tester.ensureSemantics();
    await pump(tester, ok);
    expect(find.byKey(keys.ride.weatherSummaryStale), findsNothing);

    await pump(tester, ok.copyWith(status: 'STALE'));
    expect(find.byKey(keys.ride.weatherSummary), findsOneWidget);
    expect(find.text('12–18 °C'), findsOneWidget);
    expect(find.byKey(keys.ride.weatherSummaryStale), findsOneWidget);
    expect(find.bySemanticsLabel(RegExp('Prévision ancienne')), findsOneWidget);
    semantics.dispose();
  });

  testWidgets('NOT_YET_AVAILABLE donne la date', (WidgetTester tester) async {
    await pump(
      tester,
      const RideWeatherSummaryDto(
        status: 'NOT_YET_AVAILABLE',
        availableFrom: '2099-07-22T12:00:00Z',
      ),
    );

    expect(find.textContaining('Météo dès le 22 juillet'), findsOneWidget);
  });

  testWidgets('en impérial', (WidgetTester tester) async {
    await pump(tester, ok, units: UnitSystem.imperial);

    expect(find.text('53–65 °F'), findsOneWidget);
    expect(find.textContaining('mph'), findsOneWidget);
  });

  testWidgets('rien pour un statut inconnu, une absence ou une sortie finie', (
    WidgetTester tester,
  ) async {
    await pump(tester, ok.copyWith(status: 'SOMETHING'));
    expect(find.byKey(keys.ride.weatherSummary), findsNothing);

    await pump(tester, null);
    expect(find.byKey(keys.ride.weatherSummary), findsNothing);

    await pump(tester, ok, finished: true);
    expect(find.byKey(keys.ride.weatherSummary), findsNothing);
  });

  // RULES.md : utilisable à des tailles de police accrues. Dans une `Wrap`
  // étroite, chaque morceau doit passer à la ligne plutôt que déborder.
  group('police ×2 sur une carte étroite', () {
    Future<void> pumpNarrow(WidgetTester tester, Widget child) =>
        tester.pumpWidget(
          ProviderScope(
            overrides: [
              unitSystemProvider.overrideWithValue(UnitSystem.metric),
            ],
            child: MaterialApp(
              theme: PedalonsTheme.build(Brightness.light),
              home: MediaQuery(
                data: const MediaQueryData(textScaler: TextScaler.linear(2)),
                child: Scaffold(
                  body: Center(child: SizedBox(width: 120, child: child)),
                ),
              ),
            ),
          ),
        );

    testWidgets('le vent de la ligne de résumé ne déborde pas', (
      WidgetTester tester,
    ) async {
      await pumpNarrow(tester, const RideWeatherSummaryLine(summary: ok));

      expect(find.text('Vent NO 18\u00a0km/h'), findsOneWidget);
      expect(tester.takeException(), isNull);
    });

    testWidgets('la légende d\'exposition ne déborde pas', (
      WidgetTester tester,
    ) async {
      await pumpNarrow(
        tester,
        const WeatherExposureLegend(
          exposure: WindExposureDto(head: 15000, cross: 22000, tail: 31000),
          units: UnitSystem.metric,
        ),
      );

      expect(find.textContaining('Travers'), findsOneWidget);
      expect(tester.takeException(), isNull);
    });
  });
}
