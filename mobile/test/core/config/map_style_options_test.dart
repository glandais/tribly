import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/core/config/map_style_options.dart';

void main() {
  group('mapStyleGroupLabel', () {
    test('un groupe inconnu est rendu tel quel', () {
      // `group` est libre dans le contrat : le serveur peut en servir un que
      // cette version du client ne connaît pas. Le traduire afficherait sa clé
      // (`map.styleGroup.hillshade`), l'ignorer rangerait le fond sous la
      // section précédente — les deux mentent sur ce que le serveur a classé.
      expect(mapStyleGroupLabel('hillshade'), 'hillshade');
    });
  });
}
