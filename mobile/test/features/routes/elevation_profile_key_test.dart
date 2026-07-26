import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/features/routes/data/elevation_profile_repository.dart';

void main() {
  ElevationProfileKey forWidth(double width) => ElevationProfileKey.forWidth(
    teamSlug: 'n-peloton',
    routeSlug: 'col-du-galibier',
    logicalWidth: width,
  );

  group('ElevationProfileKey.forWidth', () {
    test('demande deux échantillons par pixel logique', () {
      // 200 px → 400 demandés, mais le plafond est à 300.
      expect(forWidth(200).samples, 300);
      // 50 px → 100, arrondi au multiple de 20 supérieur.
      expect(forWidth(50).samples, 100);
    });

    test('borne à [60, 300]', () {
      expect(forWidth(1).samples, 60);
      expect(forWidth(0).samples, 60);
      expect(forWidth(4000).samples, 300);
    });

    test('quantifie pour qu\'une largeur voisine partage la même entrée', () {
      // Trois largeurs d'appareil proches ne doivent pas produire trois
      // profils : sans quantification, chaque pixel de différence créerait
      // sa propre entrée de cache pour un même parcours.
      expect(forWidth(101).samples, forWidth(105).samples);
      expect(forWidth(101).samples, forWidth(110).samples);
      expect(forWidth(101).samples, 220);
    });

    test('la clé distingue le parcours ET la finesse', () {
      expect(forWidth(50), forWidth(50));
      expect(forWidth(50), isNot(forWidth(120)));
      expect(
        forWidth(50),
        isNot(
          ElevationProfileKey(
            teamSlug: 'n-peloton',
            routeSlug: 'autre',
            samples: forWidth(50).samples,
          ),
        ),
      );
    });

    test('la clé est utilisable comme clé de map', () {
      final Map<ElevationProfileKey, int> cache = <ElevationProfileKey, int>{
        forWidth(50): 1,
      };
      expect(cache[forWidth(50)], 1);
    });
  });
}
