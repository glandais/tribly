import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:maplibre/maplibre.dart';
import 'package:pedalons/core/pdl/map/pdl_map_controller.dart';
import 'package:pedalons/core/pdl/map/pdl_mass_layer.dart';

PdlMapTrack _track(String id) => PdlMapTrack(
  id: id,
  color: const Color(0xFF228BE6),
  lines: const <List<List<double>>>[],
);

const PdlMapBox _box = PdlMapBox(
  minLon: 3.0,
  minLat: 45.7,
  maxLon: 3.2,
  maxLat: 45.9,
);

void main() {
  group('PdlMassRequest', () {
    test('le centre est celui de la boîte', () {
      const PdlMassRequest r = PdlMassRequest(box: _box, limit: 120);
      expect(r.centerLon, closeTo(3.1, 1e-12));
      expect(r.centerLat, closeTo(45.8, 1e-12));
    });

    test('le rayon est la demi-diagonale, pas la demi-largeur', () {
      const PdlMassRequest r = PdlMassRequest(box: _box, limit: 120);
      // 0,1° de latitude ≈ 11,1 km ; 0,1° de longitude à 45,8° ≈ 7,75 km ;
      // la diagonale vaut donc ~13,6 km, et elle est plus longue que chacun
      // des deux côtés — c'est tout ce qui compte.
      expect(r.radiusMeters, greaterThan(11100));
      expect(r.radiusMeters, lessThan(14500));
    });
  });

  group('PdlMassLayerController', () {
    test('le premier cadrage charge, débouncé', () async {
      int calls = 0;
      final PdlMassLayerController controller = PdlMassLayerController(
        debounce: const Duration(milliseconds: 10),
        fetcher: (PdlMassRequest r) async {
          calls++;
          return PdlMassResult(tracks: <PdlMapTrack>[_track('a')], total: 1);
        },
      );
      addTearDown(controller.dispose);

      controller.onCameraIdle(_box);
      expect(calls, 0, reason: 'le débounce n\'a pas encore expiré');
      await Future<void>.delayed(const Duration(milliseconds: 40));
      expect(calls, 1);
      expect(controller.tracks.single.id, 'a');
      expect(controller.loadedBox, _box);
      expect(controller.isStale, isFalse);
    });

    test('après le premier chargement, bouger n\'appelle plus rien', () async {
      int calls = 0;
      final PdlMassLayerController controller = PdlMassLayerController(
        debounce: const Duration(milliseconds: 10),
        fetcher: (PdlMassRequest r) async {
          calls++;
          return const PdlMassResult.empty();
        },
      );
      addTearDown(controller.dispose);

      controller.onCameraIdle(_box);
      await Future<void>.delayed(const Duration(milliseconds: 40));
      expect(calls, 1);

      const PdlMapBox moved = PdlMapBox(
        minLon: 4.0,
        minLat: 46.7,
        maxLon: 4.2,
        maxLat: 46.9,
      );
      controller.onCameraIdle(moved);
      await Future<void>.delayed(const Duration(milliseconds: 40));
      expect(
        calls,
        1,
        reason: 'un déplacement arme la pilule, il ne charge pas',
      );
      expect(controller.isStale, isTrue);

      await controller.searchHere();
      expect(calls, 2);
      expect(controller.loadedBox, moved);
      expect(controller.isStale, isFalse);
    });

    test('autoReload recharge à chaque immobilisation', () async {
      int calls = 0;
      final PdlMassLayerController controller = PdlMassLayerController(
        autoReload: true,
        debounce: const Duration(milliseconds: 10),
        fetcher: (PdlMassRequest r) async {
          calls++;
          return const PdlMassResult.empty();
        },
      );
      addTearDown(controller.dispose);

      controller.onCameraIdle(_box);
      await Future<void>.delayed(const Duration(milliseconds: 40));
      controller.onCameraIdle(
        const PdlMapBox(minLon: 4, minLat: 46, maxLon: 4.2, maxLat: 46.2),
      );
      await Future<void>.delayed(const Duration(milliseconds: 40));
      expect(calls, 2);
    });

    test('une réponse périmée est abandonnée', () async {
      final List<Completer<PdlMassResult>> gates = <Completer<PdlMassResult>>[];
      final PdlMassLayerController controller = PdlMassLayerController(
        debounce: Duration.zero,
        fetcher: (PdlMassRequest r) {
          final Completer<PdlMassResult> gate = Completer<PdlMassResult>();
          gates.add(gate);
          return gate.future;
        },
      );
      addTearDown(controller.dispose);

      controller.onCameraIdle(_box);
      await Future<void>.delayed(Duration.zero);
      // Une seconde recherche part avant que la première ne réponde.
      final Future<void> second = controller.searchHere();
      await Future<void>.delayed(Duration.zero);
      expect(gates.length, 2);

      // La première répond en dernier : sa réponse doit être jetée.
      gates[1].complete(PdlMassResult(tracks: <PdlMapTrack>[_track('récent')]));
      await second;
      gates[0].complete(PdlMassResult(tracks: <PdlMapTrack>[_track('périmé')]));
      await Future<void>.delayed(Duration.zero);

      expect(controller.tracks.single.id, 'récent');
    });

    test('un échec est retenu sans emporter la carte', () async {
      final PdlMassLayerController controller = PdlMassLayerController(
        debounce: Duration.zero,
        fetcher: (PdlMassRequest r) async => throw StateError('boum'),
      );
      addTearDown(controller.dispose);

      controller.onCameraIdle(_box);
      await Future<void>.delayed(const Duration(milliseconds: 20));
      expect(controller.error, isA<StateError>());
      expect(controller.loading, isFalse);
      expect(controller.tracks, isEmpty);
    });

    test('le plafond est rendu visible', () async {
      final PdlMassLayerController controller = PdlMassLayerController(
        limit: 2,
        debounce: Duration.zero,
        fetcher: (PdlMassRequest r) async => PdlMassResult(
          tracks: <PdlMapTrack>[_track('a'), _track('b')],
          truncated: true,
          total: 412,
        ),
      );
      addTearDown(controller.dispose);

      controller.onCameraIdle(_box);
      await Future<void>.delayed(const Duration(milliseconds: 20));
      expect(controller.truncated, isTrue);
      expect(controller.total, 412);
    });
  });

  group('PdlMassTiles — la bascule derrière son drapeau', () {
    test('le drapeau est fermé par défaut', () {
      expect(PdlMassTiles.enabled, isFalse);
    });

    test(
      'la source règle maxZoom — sans quoi la carte est vide au-delà de 2',
      () {
        final VectorSource source = PdlMassTiles.source(
          id: 'mass',
          tileUrlTemplate:
              'https://example.test/api/routes/tiles/{z}/{x}/{y}.mvt',
        );
        expect(source.maxZoom, 14);
        expect(source.minZoom, 0);
        expect(source.tiles, hasLength(1));
      },
    );

    test('sourceLayer se précise sur la couche, pas sur la source', () {
      final LineStyleLayer layer = PdlMassTiles.layer(
        id: 'mass-line',
        sourceId: 'mass',
        colorHex: '#228BE6',
      );
      expect(layer.sourceLayerId, PdlMassTiles.sourceLayerId);
      expect(layer.sourceId, 'mass');
      expect(layer.paint['line-color'], '#228BE6');
    });
  });
}
