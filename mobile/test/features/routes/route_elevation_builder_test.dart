import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/core/geo/polyline_index.dart';
import 'package:pedalons/core/pdl/elevation/elevation_samples.dart';
import 'package:pedalons/features/routes/domain/route_elevation_builder.dart';

/// Le profil dérivé de la géométrie, qui remplace l'endpoint `/elevation-profile`.
///
/// L'enjeu n'est pas l'agrégation en barres — `ElevationSamples.fromPoints` la
/// couvre déjà et n'a pas changé — mais le **chaînage**. Le profil et le
/// curseur de la carte doivent lire la même abscisse : c'est ce que garantit le
/// passage par [PolylineIndex], et c'est ce que ces tests verrouillent.

/// Un tracé rectiligne de [count] sommets, en `[lon, lat, alt, cumul]`, dont
/// la mesure repart de zéro — comme le fait chaque tracé du serveur.
List<List<double>> _track({
  int count = 5,
  double spacingMeters = 1000,
  double startElevation = 100,
  double risePerPoint = 10,
  bool withElevation = true,
  bool withMeasure = true,
}) {
  // ~0,009° de longitude par kilomètre à 45° de latitude.
  final double degPerMeter = 0.009 / 1000 / 0.7071;
  return <List<double>>[
    for (int i = 0; i < count; i++)
      <double>[
        6.0 + i * spacingMeters * degPerMeter,
        45.0,
        if (withElevation) startElevation + i * risePerPoint,
        if (withElevation && withMeasure) i * spacingMeters,
      ],
  ];
}

void main() {
  group('elevationPointsFromLines', () {
    test('un tracé simple donne un point par sommet, distances en mètres', () {
      final List<ElevationPoint>? points = elevationPointsFromLines(
        <List<List<double>>>[_track(count: 5)],
      );

      expect(points, isNotNull);
      expect(points!, hasLength(5));
      expect(points.first.distance, 0);
      expect(points.last.distance, 4000);
      expect(points.first.elevation, 100);
      expect(points.last.elevation, 140);
    });

    test('le premier point n\'a pas de pente, les suivants oui', () {
      final List<ElevationPoint> points = elevationPointsFromLines(
        <List<List<double>>>[_track(count: 4)],
      )!;

      // Le premier sommet ne termine aucun segment.
      expect(points.first.grade, isNull);
      // 10 m de montée pour 1000 m : 1 %.
      expect(points[1].grade, closeTo(1.0, 0.001));
      expect(points[3].grade, closeTo(1.0, 0.001));
    });

    test('deux sommets confondus ne fabriquent pas une pente absurde', () {
      final List<ElevationPoint> points = elevationPointsFromLines(
        <List<List<double>>>[
          <List<double>>[
            <double>[6.0, 45.0, 100, 0],
            // 10 cm plus loin, 5 m plus haut : le rapport exploserait.
            <double>[6.0, 45.0, 105, 0.1],
            <double>[6.01, 45.0, 110, 1000],
          ],
        ],
      )!;

      expect(points[1].grade, isNull);
      expect(points[2].grade, isNotNull);
    });

    test('un tracé sans altitude n\'a pas de profil du tout', () {
      expect(
        elevationPointsFromLines(<List<List<double>>>[
          _track(withElevation: false),
        ]),
        isNull,
      );
    });

    test('un tracé d\'un seul sommet n\'a pas de profil', () {
      expect(
        elevationPointsFromLines(<List<List<double>>>[_track(count: 1)]),
        isNull,
      );
    });

    test('sans mesure M, la distance retombe sur la haversine', () {
      final List<ElevationPoint> points = elevationPointsFromLines(
        <List<List<double>>>[_track(count: 3, withMeasure: false)],
      )!;

      expect(points, hasLength(3));
      // Le repli haversine ne rend pas exactement le pas demandé, mais reste
      // du bon ordre de grandeur et strictement croissant.
      expect(points.last.distance, greaterThan(1000));
      expect(points[1].distance, greaterThan(points.first.distance));
    });

    /// Le point de tout l'exercice. Le serveur concaténait les tracés d'un
    /// parcours **sans** pont entre eux ; `PolylineIndex` en insère un
    /// (haversine, parce que rien ne garantit que deux tracés partagent une
    /// origine de mesure). Les deux axes divergeaient donc sur un parcours
    /// multi-tracés. Dériver le profil de l'index les rend identiques par
    /// construction — et c'est cette identité qu'on verrouille ici.
    test('un parcours multi-tracés partage l\'axe du PolylineIndex', () {
      final List<List<List<double>>> lines = <List<List<double>>>[
        _track(count: 3),
        _track(count: 3, startElevation: 200),
      ];

      final List<ElevationPoint> points = elevationPointsFromLines(lines)!;
      final PolylineIndex index = PolylineIndex.fromLineStrings(lines);

      expect(points, hasLength(index.length));
      for (int i = 0; i < points.length; i++) {
        expect(
          points[i].distance,
          index.cumulativeDistances[i],
          reason: 'point $i',
        );
      }
      expect(points.last.distance, index.totalDistance);

      // Le second tracé repart de 0 en mesure ; sans rebasage il se replierait
      // sur le premier.
      expect(points[3].distance, greaterThan(points[2].distance));
    });
  });

  group('elevationSamplesFromLines', () {
    test('l\'axe du profil va jusqu\'à la longueur de la polyligne', () {
      final List<List<List<double>>> lines = <List<List<double>>>[
        _track(count: 100),
      ];
      final ElevationSamples samples = elevationSamplesFromLines(lines)!;
      final PolylineIndex index = PolylineIndex.fromLineStrings(lines);

      expect(samples.totalDistance, index.totalDistance);
      expect(samples.minElevation, 100);
      expect(samples.maxElevation, 100 + 99 * 10);
      // 99 segments : le plafond de barres n'est plus le facteur limitant.
      expect(samples.bars, hasLength(kElevationBarTarget));
    });

    test('sans altitude, pas d\'échantillons', () {
      expect(
        elevationSamplesFromLines(<List<List<double>>>[
          _track(withElevation: false),
        ]),
        isNull,
      );
    });
  });
}
