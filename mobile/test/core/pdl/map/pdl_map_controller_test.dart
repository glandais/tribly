import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/core/pdl/map/pdl_map_controller.dart';

void main() {
  group('PdlMapBox.ofTracks', () {
    test('englobe tous les tracés', () {
      final PdlMapBox? box = PdlMapBox.ofTracks(<PdlMapTrack>[
        PdlMapTrack(
          id: 'a',
          color: const Color(0xFF000000),
          lines: <List<List<double>>>[
            <List<double>>[
              <double>[2.0, 45.0],
              <double>[3.0, 46.0],
            ],
          ],
        ),
        PdlMapTrack(
          id: 'b',
          color: const Color(0xFF000000),
          lines: <List<List<double>>>[
            <List<double>>[
              <double>[1.0, 47.0],
            ],
          ],
        ),
      ]);

      expect(box, isNotNull);
      expect(box!.minLon, 1.0);
      expect(box.maxLon, 3.0);
      expect(box.minLat, 45.0);
      expect(box.maxLat, 47.0);
    });

    test('rend null quand il n\'y a aucune coordonnée', () {
      expect(PdlMapBox.ofTracks(const <PdlMapTrack>[]), isNull);
      expect(
        PdlMapBox.ofTracks(<PdlMapTrack>[
          PdlMapTrack(
            id: 'vide',
            color: const Color(0xFF000000),
            lines: const <List<List<double>>>[<List<double>>[]],
          ),
        ]),
        isNull,
      );
    });

    test('l\'aller-retour par LngLatBounds conserve les quatre bords', () {
      const PdlMapBox box = PdlMapBox(
        minLon: 1.5,
        minLat: 44.5,
        maxLon: 3.5,
        maxLat: 46.5,
      );
      expect(PdlMapBox.fromLngLatBounds(box.toLngLatBounds()), box);
    });
  });

  group('bornes kilométriques', () {
    test('le pas suit la longueur du parcours', () {
      expect(pdlKmMarkerIntervalKm(5), 1);
      expect(pdlKmMarkerIntervalKm(15), 2);
      expect(pdlKmMarkerIntervalKm(30), 5);
      expect(pdlKmMarkerIntervalKm(120), 10);
    });

    /// Une ligne est-ouest sur l'équateur : un degré de longitude y vaut
    /// ~111,19 km, ce qui donne des distances vérifiables à la main.
    List<List<double>> equatorLine(int points, double stepDeg) =>
        <List<double>>[
          for (int i = 0; i < points; i++) <double>[i * stepDeg, 0.0],
        ];

    test('une borne tous les kilomètres sur un parcours de 5 km', () {
      // 0,001° ≈ 111,19 m ; 50 pas ≈ 5,56 km.
      final List<PdlKmMarker> markers = computePdlKmMarkers(
        <List<List<double>>>[equatorLine(51, 0.001)],
      );
      expect(markers.length, 5);
      expect(markers.first.label, '1');
      expect(markers.last.label, '5');
      // La première borne tombe à ~1 km, soit ~0,00899°.
      expect(markers.first.lon, closeTo(0.00899, 0.0002));
      expect(markers.first.lat, closeTo(0, 1e-9));
    });

    test('la distance servie l\'emporte sur la distance calculée', () {
      // 5,56 km de géométrie, mais `RouteDetailDto.distance` annonce 30 km :
      // le pas passe donc de 1 km à 5 km. Les bornes qui tombent au-delà de la
      // géométrie sont ramenées sur son dernier point par `PolylineIndex` —
      // c'est le comportement voulu, un tracé plus court que sa distance
      // déclarée n'est pas un cas normal.
      final List<PdlKmMarker> markers = computePdlKmMarkers(
        <List<List<double>>>[equatorLine(51, 0.001)],
        totalMeters: 30000,
      );
      expect(markers.map((PdlKmMarker m) => m.label), <String>[
        '5',
        '10',
        '15',
        '20',
        '25',
      ]);
    });

    test('la 4ᵉ composante G3DM sert de cumul quand elle est utilisable', () {
      final List<PdlKmMarker> markers = computePdlKmMarkers(
        <List<List<double>>>[
          <List<double>>[
            <double>[0, 0, 100, 0],
            <double>[0.01, 0, 100, 2000],
            <double>[0.02, 0, 100, 4000],
          ],
        ],
      );
      // 4 km de cumul, pas de 1 km → 3 bornes.
      expect(markers.map((PdlKmMarker m) => m.label), <String>['1', '2', '3']);
    });

    test('un tracé vide ne produit aucune borne', () {
      expect(computePdlKmMarkers(const <List<List<double>>>[]), isEmpty);
    });

    test('le formatage du libellé est délégué à l\'appelant', () {
      final List<PdlKmMarker> markers = computePdlKmMarkers(
        <List<List<double>>>[equatorLine(51, 0.001)],
        formatKm: (int km) => '$km km',
      );
      expect(markers.first.label, '1 km');
    });
  });

  group('PdlMapController', () {
    test('un tracé seul est implicitement sélectionné', () async {
      final PdlMapController controller = PdlMapController();
      addTearDown(controller.dispose);

      await controller.setContent(
        tracks: <PdlMapTrack>[
          PdlMapTrack(
            id: 'solo',
            color: const Color(0xFF228BE6),
            lines: const <List<List<double>>>[],
          ),
        ],
      );
      expect(controller.selectedTrackId, 'solo');
    });

    test('plusieurs tracés n\'en sélectionnent aucun d\'office', () async {
      final PdlMapController controller = PdlMapController();
      addTearDown(controller.dispose);

      await controller.setContent(
        tracks: <PdlMapTrack>[
          PdlMapTrack(
            id: 'a',
            color: const Color(0xFF000000),
            lines: const <List<List<double>>>[],
          ),
          PdlMapTrack(
            id: 'b',
            color: const Color(0xFF000000),
            lines: const <List<List<double>>>[],
          ),
        ],
      );
      expect(controller.selectedTrackId, isNull);
    });

    test('une sélection qui disparaît du jeu de tracés est oubliée', () async {
      final PdlMapController controller = PdlMapController();
      addTearDown(controller.dispose);

      final List<PdlMapTrack> two = <PdlMapTrack>[
        PdlMapTrack(
          id: 'a',
          color: const Color(0xFF000000),
          lines: const <List<List<double>>>[],
        ),
        PdlMapTrack(
          id: 'b',
          color: const Color(0xFF000000),
          lines: const <List<List<double>>>[],
        ),
      ];
      await controller.setContent(tracks: two);
      await controller.select('b');
      expect(controller.selectedTrackId, 'b');

      await controller.setContent(tracks: <PdlMapTrack>[two.first]);
      // 'b' a disparu ; 'a' reste seul, donc implicitement sélectionné.
      expect(controller.selectedTrackId, 'a');
    });

    test('changer de sélection notifie une fois', () async {
      final PdlMapController controller = PdlMapController();
      addTearDown(controller.dispose);
      int notifications = 0;
      controller.addListener(() => notifications++);

      await controller.select('x');
      expect(notifications, 1);
      // Re-sélectionner la même chose ne notifie pas.
      await controller.select('x');
      expect(notifications, 1);
    });

    test('sans carte attachée, trackIdAt ne lève pas', () {
      final PdlMapController controller = PdlMapController();
      addTearDown(controller.dispose);
      expect(controller.trackIdAt(const Offset(10, 10)), isNull);
      expect(controller.visibleBox, isNull);
    });
  });
}
