import 'dart:math' as math;

import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/core/pdl/map/pdl_map_camera.dart';
import 'package:pedalons/core/pdl/map/pdl_map_controller.dart';

/// Le cadrage d'une carte est le seul morceau de `core/pdl/map` qu'on puisse
/// vérifier sans simulateur — et c'est justement celui qui se trompait en
/// silence quand il était délégué à la vue native.
void main() {
  // Un parcours nantais : nettement plus large que haut.
  const PdlMapBox wide = PdlMapBox(
    minLon: -1.75,
    minLat: 47.15,
    maxLon: -1.45,
    maxLat: 47.30,
  );
  const Size phone = Size(402, 874);

  group('projection', () {
    test('les bornes du monde tombent sur le carré unité', () {
      expect(pdlMercatorX(-180), closeTo(0, 1e-12));
      expect(pdlMercatorX(180), closeTo(1, 1e-12));
      expect(pdlMercatorY(kWebMercatorMaxLat), closeTo(0, 1e-9));
      expect(pdlMercatorY(-kWebMercatorMaxLat), closeTo(1, 1e-9));
      expect(pdlMercatorY(0), closeTo(0.5, 1e-12));
    });

    test('y croît vers le sud, comme l’axe de l’écran', () {
      expect(pdlMercatorY(48), lessThan(pdlMercatorY(47)));
    });

    test('aller-retour', () {
      expect(pdlLonFromMercatorX(pdlMercatorX(-1.55)), closeTo(-1.55, 1e-9));
      expect(pdlLatFromMercatorY(pdlMercatorY(47.22)), closeTo(47.22, 1e-9));
    });
  });

  group('pdlCameraForBox', () {
    test('sans marge, la boîte tient tout juste dans la vue', () {
      final PdlMapCamera camera = pdlCameraForBox(wide, size: phone)!;

      // Le zoom est celui qui fait tenir la boîte : à ce zoom, la boîte occupe
      // exactement une des deux dimensions, et pas plus que l'autre.
      final double scale = 512 * math.pow(2, camera.zoom).toDouble();
      final double widthPx =
          (pdlMercatorX(wide.maxLon) - pdlMercatorX(wide.minLon)) * scale;
      final double heightPx =
          (pdlMercatorY(wide.minLat) - pdlMercatorY(wide.maxLat)) * scale;

      expect(widthPx, lessThanOrEqualTo(phone.width + 0.001));
      expect(heightPx, lessThanOrEqualTo(phone.height + 0.001));
      // Plus large que haut : c'est la largeur qui commande.
      expect(widthPx, closeTo(phone.width, 0.001));
    });

    test('sans marge, la caméra vise le centre de la boîte', () {
      final PdlMapCamera camera = pdlCameraForBox(wide, size: phone)!;
      expect(camera.lon, closeTo(-1.6, 1e-9));
      expect(
        pdlMercatorY(camera.lat),
        closeTo(
          (pdlMercatorY(wide.maxLat) + pdlMercatorY(wide.minLat)) / 2,
          1e-12,
        ),
      );
    });

    test(
      'une marge basse pousse le tracé vers le haut sans toucher au zoom',
      () {
        // C'est le cas de la fiche parcours : une feuille couvre la moitié
        // basse. Le parcours étant plus large que haut, la largeur reste la
        // dimension contraignante — donc le zoom ne doit pas bouger, seul le
        // centre.
        final PdlMapCamera plain = pdlCameraForBox(wide, size: phone)!;
        final PdlMapCamera withSheet = pdlCameraForBox(
          wide,
          size: phone,
          padding: const EdgeInsets.only(bottom: 437),
        )!;

        // Le zoom est **inchangé** : la hauteur utile qui reste (437 px) est
        // encore plus que ce que la boîte demande à ce zoom-là. C'est le point
        // à ne pas confondre — réserver la place d'une feuille ne dézoome pas
        // un parcours large, sinon on le perdrait sur les côtés.
        expect(withSheet.zoom, closeTo(plain.zoom, 1e-12));
        // La caméra descend vers le sud, ce qui remonte la boîte à l'écran.
        expect(withSheet.lat, lessThan(plain.lat));
        expect(withSheet.lon, closeTo(plain.lon, 1e-9));
      },
    );

    test('des marges symétriques ne décalent pas le centre', () {
      final PdlMapCamera camera = pdlCameraForBox(
        wide,
        size: phone,
        padding: const EdgeInsets.all(50),
      )!;
      expect(camera.lon, closeTo(-1.6, 1e-9));
      expect(camera.zoom, lessThan(pdlCameraForBox(wide, size: phone)!.zoom));
    });

    test('l’échelle de la plateforme décale le zoom d’exactement un cran', () {
      final PdlMapCamera at512 = pdlCameraForBox(wide, size: phone)!;
      final PdlMapCamera at256 = pdlCameraForBox(
        wide,
        size: phone,
        worldPixelsAtZoom0: 256,
      )!;
      expect(at256.zoom, closeTo(at512.zoom + 1, 1e-9));
    });

    test('une boîte dégénérée ne fait pas plonger la caméra', () {
      const PdlMapBox point = PdlMapBox(
        minLon: -1.55,
        minLat: 47.21,
        maxLon: -1.55,
        maxLat: 47.21,
      );
      final PdlMapCamera camera = pdlCameraForBox(
        point.expandedToMinSpan(),
        size: phone,
      )!;
      expect(camera.zoom, lessThan(20));
      expect(camera.lon, closeTo(-1.55, 1e-6));
    });

    test('une vue entièrement mangée par les marges ne rend rien', () {
      expect(
        pdlCameraForBox(
          wide,
          size: phone,
          padding: const EdgeInsets.symmetric(horizontal: 250),
        ),
        isNull,
      );
    });
  });
}
