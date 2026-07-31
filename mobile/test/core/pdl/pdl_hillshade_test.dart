import 'dart:ui';

import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/core/pdl/pdl.dart';

void main() {
  const PdlHillshade shading = PdlHillshade(
    url: 'https://tiles.example/tilejson.json',
    shadowColor: Color(0xFF727C83),
    highlightColor: Color(0xFFFFFFE4),
  );

  group('PdlHillshade', () {
    test('deux instances identiques sont égales', () {
      // `PdlMap.didUpdateWidget` compare **par valeur** : les écrans en
      // reconstruisent une à chaque `build`, et comparer par identité reposerait
      // toutes les couches à chaque image — la carte scintillerait et la caméra
      // serait bousculée.
      expect(
        shading,
        const PdlHillshade(
          url: 'https://tiles.example/tilejson.json',
          shadowColor: Color(0xFF727C83),
          highlightColor: Color(0xFFFFFFE4),
        ),
      );
    });

    test('changer de source ou de teinte casse l\'égalité', () {
      expect(
        shading ==
            const PdlHillshade(
              url: 'https://autre.example/tilejson.json',
              shadowColor: Color(0xFF727C83),
              highlightColor: Color(0xFFFFFFE4),
            ),
        isFalse,
      );
      expect(
        shading ==
            const PdlHillshade(
              url: 'https://tiles.example/tilejson.json',
              shadowColor: Color(0xFF000000),
              highlightColor: Color(0xFFFFFFE4),
            ),
        isFalse,
      );
    });
  });
}
