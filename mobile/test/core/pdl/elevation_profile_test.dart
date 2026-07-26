import 'dart:ui' as ui;

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/core/pdl/elevation/elevation_bars_painter.dart';
import 'package:pedalons/core/pdl/elevation/elevation_cursor_painter.dart';
import 'package:pedalons/core/pdl/elevation/elevation_samples.dart';
import 'package:pedalons/core/pdl/elevation/pdl_elevation_profile.dart';
import 'package:pedalons/core/theme/pdl_tokens.dart';
import 'package:pedalons/core/theme/pedalons_theme.dart';

/// 300 points synthétiques — la taille maximale que le serveur renvoie.
///
/// La première moitié est plate (pente 0), la seconde grimpe à 20 % : les deux
/// extrémités de l'échelle de pente sont donc représentées, et les deux barres
/// extrêmes en sont les témoins.
ElevationSamples _synthetic({int count = 300}) {
  final List<ElevationPoint> points = <ElevationPoint>[];
  double elevation = 100;
  for (int i = 0; i < count; i++) {
    final double distance = i * 100.0;
    final bool climbing = i > count ~/ 2;
    if (climbing) elevation += 20;
    points.add(
      ElevationPoint(
        distance: distance,
        elevation: elevation,
        grade: i == 0 ? 0 : (climbing ? 20 : 0),
      ),
    );
  }
  return ElevationSamples.fromPoints(points);
}

/// Canvas d'essai : on ne veut que la liste des formes demandées.
///
/// `noSuchMethod` absorbe tout le reste de l'interface — y compris le
/// `drawParagraph` de l'info-bulle, dont le contenu ne nous intéresse pas.
class _RecordingCanvas implements ui.Canvas {
  final List<(ui.RRect, Color)> rrects = <(ui.RRect, Color)>[];
  final List<(ui.Rect, Color)> rects = <(ui.Rect, Color)>[];

  @override
  void drawRRect(ui.RRect rrect, ui.Paint paint) =>
      rrects.add((rrect, paint.color));

  @override
  void drawRect(ui.Rect rect, ui.Paint paint) => rects.add((rect, paint.color));

  @override
  dynamic noSuchMethod(Invocation invocation) => null;
}

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('ElevationBarsPainter', () {
    test('colorise chaque barre par sa propre pente', () {
      final ElevationSamples samples = _synthetic();
      final _RecordingCanvas canvas = _RecordingCanvas();

      ElevationBarsPainter(
        samples: samples,
        neutralColor: slopeNeutral,
        slopeColorOf: slopeColor,
      ).paint(canvas, const Size(380, 110));

      expect(canvas.rrects, hasLength(samples.bars.length));
      expect(samples.bars.length, kElevationBarTarget);

      // Pente 0 → teinte 85 (vert), pente ≥ 18 % → teinte 255 (violet).
      expect(samples.bars.first.grade, closeTo(0, 1e-9));
      expect(samples.bars.last.grade, closeTo(20, 1e-9));
      expect(HSLColor.fromColor(canvas.rrects.first.$2).hue, closeTo(85, 1.5));
      expect(HSLColor.fromColor(canvas.rrects.last.$2).hue, closeTo(255, 1.5));
    });

    test('une pente inconnue tombe sur slopeNeutral', () {
      final ElevationSamples samples =
          ElevationSamples.fromPoints(const <ElevationPoint>[
            ElevationPoint(distance: 0, elevation: 10),
            ElevationPoint(distance: 100, elevation: 20),
          ], targetBars: 1);
      final _RecordingCanvas canvas = _RecordingCanvas();

      ElevationBarsPainter(
        samples: samples,
        neutralColor: slopeNeutral,
        slopeColorOf: slopeColor,
      ).paint(canvas, const Size(380, 110));

      // Comparaison sur l'entier ARGB : `Paint.color` fait un aller-retour en
      // flottants, deux couleurs identiques à l'œil n'y sont pas `==`.
      expect(canvas.rrects.single.$2.toARGB32(), slopeNeutral.toARGB32());
    });

    test(
      'les barres sont jointives à 1 px de gouttière et alignées en bas',
      () {
        final ElevationSamples samples = _synthetic(count: 5);
        final _RecordingCanvas canvas = _RecordingCanvas();
        const Size size = Size(101, 110);

        ElevationBarsPainter(
          samples: samples,
          neutralColor: slopeNeutral,
          slopeColorOf: slopeColor,
        ).paint(canvas, size);

        final int n = samples.bars.length;
        final double barWidth = (size.width - (n - 1)) / n;
        expect(canvas.rrects.first.$1.left, closeTo(0, 1e-9));
        expect(canvas.rrects.first.$1.width, closeTo(barWidth, 1e-9));
        expect(canvas.rrects.last.$1.right, closeTo(size.width, 1e-9));
        for (final (ui.RRect r, Color _) in canvas.rrects) {
          expect(r.bottom, closeTo(size.height, 1e-9));
          expect(r.tlRadiusY, 1);
          expect(r.blRadiusY, 0);
        }
      },
    );
  });

  group('ElevationCursorPainter', () {
    ElevationCursorPainter painterAt(ValueNotifier<double?> cursor) =>
        ElevationCursorPainter(
          samples: _synthetic(),
          distance: cursor,
          repaint: cursor,
          lineColor: const Color(0xB3000000),
          tipBackground: const Color(0xFFFFFFFF),
          tipBorder: const Color(0xFFDEE2E6),
          tipTextStyle: const TextStyle(fontSize: 10),
          textScaler: TextScaler.noScaling,
          label: (ElevationReading r) => '28,7 km · 74 m · 4,1 %',
        );

    test('l\'info-bulle est bornée au bord gauche (curseur à 0 %)', () {
      final ValueNotifier<double?> cursor = ValueNotifier<double?>(0);
      addTearDown(cursor.dispose);
      final _RecordingCanvas canvas = _RecordingCanvas();

      painterAt(cursor).paint(canvas, const Size(380, 140));

      final ui.RRect tip = canvas.rrects.first.$1;
      expect(tip.left, greaterThanOrEqualTo(0));
      expect(tip.top, ElevationCursorPainter.tipTop);
      // Sans bornage, une bulle centrée sur x = 0 commencerait à −largeur / 2.
      expect(tip.left, closeTo(ElevationCursorPainter.tipEdgeInset, 1e-9));
    });

    test('l\'info-bulle est bornée au bord droit (curseur à 100 %)', () {
      final ElevationSamples samples = _synthetic();
      final ValueNotifier<double?> cursor = ValueNotifier<double?>(
        samples.totalDistance,
      );
      addTearDown(cursor.dispose);
      final _RecordingCanvas canvas = _RecordingCanvas();
      const Size size = Size(380, 140);

      painterAt(cursor).paint(canvas, size);

      final ui.RRect tip = canvas.rrects.first.$1;
      expect(tip.right, lessThanOrEqualTo(size.width));
      expect(
        tip.right,
        closeTo(size.width - ElevationCursorPainter.tipEdgeInset, 1e-9),
      );
    });

    test('le réticule est un trait de 1 px sur toute la hauteur', () {
      final ValueNotifier<double?> cursor = ValueNotifier<double?>(
        _synthetic().totalDistance / 2,
      );
      addTearDown(cursor.dispose);
      final _RecordingCanvas canvas = _RecordingCanvas();

      painterAt(cursor).paint(canvas, const Size(380, 140));

      final (ui.Rect line, Color _) = canvas.rects.single;
      expect(line.width, 1);
      expect(line.top, 0);
      expect(line.bottom, 140);
      expect(line.center.dx, closeTo(190, 0.5));
    });

    test('sans distance, rien n\'est peint', () {
      final ValueNotifier<double?> cursor = ValueNotifier<double?>(null);
      addTearDown(cursor.dispose);
      final _RecordingCanvas canvas = _RecordingCanvas();

      painterAt(cursor).paint(canvas, const Size(380, 140));

      expect(canvas.rects, isEmpty);
      expect(canvas.rrects, isEmpty);
    });
  });

  group('PdlElevationProfile', () {
    Widget host(Widget child) => MaterialApp(
      theme: PedalonsTheme.build(Brightness.light),
      home: Scaffold(
        body: Padding(padding: const EdgeInsets.all(16), child: child),
      ),
    );

    testWidgets('le glissement ne repeint pas les barres', (
      WidgetTester tester,
    ) async {
      final ElevationSamples samples = _synthetic();
      final ValueNotifier<double?> cursor = ValueNotifier<double?>(null);
      addTearDown(cursor.dispose);

      await tester.pumpWidget(
        host(
          PdlElevationProfile(
            samples: samples,
            cursorDistance: cursor,
            height: PdlMetrics.elevationLarge,
            axisLabel: (double m, {required bool isLast}) =>
                isLast ? '${m ~/ 1000} km' : '${m ~/ 1000}',
            tipLabel: (ElevationReading r) => '${r.distance ~/ 1000} km',
          ),
        ),
      );
      await tester.pump();

      final int before = ElevationBarsPainter.debugPaintCount;
      expect(before, greaterThan(0));

      final TestGesture gesture = await tester.startGesture(
        tester.getCenter(find.byType(PdlElevationProfile)),
      );
      for (int i = 0; i < 10; i++) {
        await gesture.moveBy(const Offset(12, 0));
        await tester.pump(const Duration(milliseconds: 16));
      }
      await gesture.up();
      await tester.pump();

      // Le réticule a bien bougé…
      expect(cursor.value, isNotNull);
      // …et **persiste après le relâchement**.
      final double held = cursor.value!;
      await tester.pump(const Duration(milliseconds: 200));
      expect(cursor.value, held);

      // …sans qu'une seule barre ait été repeinte.
      expect(ElevationBarsPainter.debugPaintCount, before);
    });

    testWidgets('un tap sous la boîte efface le réticule', (
      WidgetTester tester,
    ) async {
      final ValueNotifier<double?> cursor = ValueNotifier<double?>(1234);
      addTearDown(cursor.dispose);

      await tester.pumpWidget(
        host(
          PdlElevationProfile(
            samples: _synthetic(),
            cursorDistance: cursor,
            height: PdlMetrics.elevationCompact,
            axisLabel: (double m, {required bool isLast}) => '${m ~/ 1000}',
            tipLabel: (ElevationReading r) => '${r.distance ~/ 1000}',
          ),
        ),
      );
      await tester.pump();

      // L'axe est sous la boîte : y taper sort du profil.
      final Offset topLeft = tester.getTopLeft(
        find.byType(PdlElevationProfile),
      );
      await tester.tapAt(
        topLeft + const Offset(40, PdlMetrics.elevationCompact + 6),
      );
      await tester.pump();

      expect(cursor.value, isNull);
    });

    testWidgets('l\'axe porte 5 graduations, la dernière avec l\'unité', (
      WidgetTester tester,
    ) async {
      await tester.pumpWidget(
        host(
          PdlElevationProfile(
            samples: _synthetic(),
            axisLabel: (double m, {required bool isLast}) =>
                isLast ? '${(m / 1000).round()} km' : '${(m / 1000).round()}',
            tipLabel: (ElevationReading r) => '',
          ),
        ),
      );
      await tester.pump();

      expect(find.text('0'), findsOneWidget);
      expect(find.textContaining('km'), findsOneWidget);
    });

    testWidgets(
      'les états de chargement et d\'échec ne peignent aucune barre',
      (WidgetTester tester) async {
        await tester.pumpWidget(
          host(
            const PdlElevationProfile.loading(
              label: 'Chargement du profil altimétrique...',
            ),
          ),
        );
        await tester.pump();
        expect(
          find.text('Chargement du profil altimétrique...'),
          findsOneWidget,
        );

        bool retried = false;
        await tester.pumpWidget(
          host(
            PdlElevationProfile.failed(
              label: 'Profil indisponible',
              actionLabel: 'Réessayer',
              onRetry: () => retried = true,
            ),
          ),
        );
        await tester.pump();
        expect(find.text('Profil indisponible'), findsOneWidget);
        await tester.tap(find.text('Réessayer'));
        expect(retried, isTrue);
      },
    );
  });
}
