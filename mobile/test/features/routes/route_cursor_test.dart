import 'dart:ui';

import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/core/pdl/map/pdl_map_controller.dart';
import 'package:pedalons/core/theme/pdl_tokens.dart';
import 'package:pedalons/features/routes/domain/track_slope_colors.dart';
import 'package:pedalons/features/routes/presentation/widgets/route_cursor_controller.dart';

/// S13-2 et S13-3 — la colorisation par pente et le réticule bidirectionnel.
///
/// Le cas de test 2 du §5.3 : « la distance affichée coïncide avec
/// `ElevationProfileDto.distance` à ±0,5 % ». Ici c'est la géométrie qui fait
/// foi des deux côtés — le lien est **toujours** une distance cumulée, jamais
/// un index de point, parce que le profil est échantillonné à ~300 points et la
/// géométrie en compte jusqu'à 3 000.
void main() {
  /// Une ligne droite est-ouest à latitude ~0, avec altitude et cumul :
  /// `[lon, lat, alt, m]`. Chaque segment fait 1 000 m.
  List<List<double>> straight({int segments = 10, double risePerSegment = 0}) {
    const double metersPerDegree = 111320.0;
    return <List<double>>[
      for (int i = 0; i <= segments; i++)
        <double>[
          i * 1000.0 / metersPerDegree,
          0,
          100 + i * risePerSegment,
          i * 1000.0,
        ],
    ];
  }

  group('colorisation par pente', () {
    test('un plat prend la teinte de départ de l\'échelle', () {
      final List<Color> colors = slopeColorsForLines(<List<List<double>>>[
        straight(),
      ]);
      expect(colors, hasLength(10));
      expect(colors.first, slopeColor(0));
    });

    test('une pente à 18 % ou plus sature au violet', () {
      // 180 m de dénivelé sur 1 000 m = 18 %.
      final List<Color> colors = slopeColorsForLines(<List<List<double>>>[
        straight(risePerSegment: 180),
      ]);
      expect(colors.first, slopeColor(18));
      expect(slopeColor(40), slopeColor(18), reason: 'l\'échelle est bornée');
    });

    test('une descente est peinte comme la montée de même raideur', () {
      final List<Color> up = slopeColorsForLines(<List<List<double>>>[
        straight(risePerSegment: 80),
      ]);
      final List<Color> down = slopeColorsForLines(<List<List<double>>>[
        straight(risePerSegment: -80),
      ]);
      // C'est la difficulté du relief qu'on montre, pas son sens.
      expect(down.first, up.first);
    });

    test('un segment sans altitude prend la teinte neutre', () {
      final List<Color> colors = slopeColorsForLines(<List<List<double>>>[
        <List<double>>[
          <double>[0, 0],
          <double>[0.01, 0],
        ],
      ]);
      expect(colors.single, slopeNeutral);
    });

    test('il y a exactement une couleur par segment', () {
      final List<List<List<double>>> lines = <List<List<double>>>[
        straight(segments: 7),
        straight(segments: 3),
      ];
      expect(
        slopeColorsForLines(lines),
        hasLength(segmentCount(lines)),
        reason: 'PdlMapTrack.segmentColors indexe les segments un à un',
      );
    });
  });

  group('réticule', () {
    late RouteCursorController cursor;

    setUp(() {
      cursor = RouteCursorController(
        lines: <List<List<double>>>[straight(risePerSegment: 20)],
      );
    });

    tearDown(() => cursor.dispose());

    test('la distance totale colle à la géométrie à ±0,5 %', () {
      expect(cursor.totalDistance, closeTo(10000, 10000 * 0.005));
    });

    test('profil → carte : une distance devient une position', () {
      final PdlMapPoint? start = cursor.positionFor(0);
      final PdlMapPoint? end = cursor.positionFor(cursor.totalDistance);
      expect(start, isNotNull);
      expect(end, isNotNull);
      expect(start!.lon, closeTo(0, 1e-9));
      expect(end!.lon, greaterThan(start.lon));
    });

    test('carte → profil : un tap près du tracé rend la bonne distance', () {
      // Le point à mi-parcours, décalé de quelques mètres sur le côté.
      final PdlMapPoint mid = cursor.positionFor(5000)!;
      final double? d = cursor.distanceNear(mid.lon, mid.lat + 0.00005);
      expect(d, isNotNull);
      expect(d!, closeTo(5000, 5000 * 0.005));
    });

    test('un tap loin du tracé n\'accroche rien', () {
      expect(cursor.distanceNear(10, 45), isNull);
    });

    test('le réticule s\'efface, et l\'effacement se propage', () {
      int notifications = 0;
      cursor.distance.addListener(() => notifications++);

      cursor.moveTo(1234);
      expect(cursor.distance.value, 1234);
      cursor.clear();
      expect(cursor.distance.value, isNull);
      expect(notifications, 2);
    });
  });
}
