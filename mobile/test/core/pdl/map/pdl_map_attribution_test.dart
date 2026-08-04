import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/core/pdl/pdl.dart';

/// Les chaînes réelles servies par `application.properties` et par les
/// documents de style des fournisseurs. Un test sur des exemples inventés
/// prouverait que la regexp marche sur des exemples inventés.
void main() {
  group('parsePdlCredit', () {
    test('un crédit sans balise ressort en un seul segment', () {
      // ESRI : une phrase entière, pas un lien.
      const String esri =
          'Tiles © Esri — Source: Esri, i-cubed, USDA, USGS, AEX, GeoEye, '
          'Getmapping, Aerogrid, IGN, IGP, UPR-EGP, and the GIS User Community';
      expect(parsePdlCredit(esri), <PdlCreditSegment>[
        const PdlCreditSegment(esri),
      ]);
    });

    test('le lien OpenStreetMap est isolé, entité décodée', () {
      // Tel que MapStyleService le pose sur la source du fond `osm`.
      final List<PdlCreditSegment> segments = parsePdlCredit(
        '&copy; <a href="https://www.openstreetmap.org/copyright" '
        'target="_blank">OpenStreetMap</a>',
      );
      expect(segments, <PdlCreditSegment>[
        const PdlCreditSegment('© '),
        const PdlCreditSegment(
          'OpenStreetMap',
          href: 'https://www.openstreetmap.org/copyright',
        ),
      ]);
    });

    test('CyclOSM donne deux liens séparés par le texte qui les joint', () {
      final List<PdlCreditSegment> segments = parsePdlCredit(
        '<a href="https://github.com/cyclosm/cyclosm-cartocss-style/releases" '
        'title="CyclOSM - Open Bicycle render">CyclOSM</a> | &copy; '
        '<a href="https://www.openstreetmap.org/copyright" '
        'target="_blank">OpenStreetMap</a>',
      );
      expect(segments.length, 3);
      expect(segments.first.text, 'CyclOSM');
      expect(
        segments.first.href,
        'https://github.com/cyclosm/cyclosm-cartocss-style/releases',
      );
      // Le `title` contient un tiret et des espaces : l'attribut ne doit pas
      // être confondu avec le `href`, ni terminer la balise trop tôt.
      expect(segments[1], const PdlCreditSegment(' | © '));
      expect(segments.last.text, 'OpenStreetMap');
    });

    test('le crédit IGN servi par le contrat porte ses deux liens', () {
      // `MapStyleDto.attribution` du fond `ign-vector` : le seul dont le
      // document de style ne déclare rien, d'où ce complément.
      final List<PdlCreditSegment> segments = parsePdlCredit(
        '<a target="_blank" href="https://www.ign.fr/">IGN</a> &mdash; '
        '<a target="_blank" href="https://www.geoportail.gouv.fr/">Geoportail</a>',
      );
      expect(segments.map((PdlCreditSegment s) => s.text).toList(), <String>[
        'IGN',
        ' — ',
        'Geoportail',
      ]);
      // `href` vient après `target` dans la chaîne : c'est bien l'URL qui est
      // capturée, pas le premier attribut venu.
      expect(segments.first.href, 'https://www.ign.fr/');
      expect(segments.last.href, 'https://www.geoportail.gouv.fr/');
    });

    test('Mapterhorn, dont le TileJSON cite en apostrophes simples', () {
      // Le MNT du relief est le seul fournisseur à citer son `href` ainsi. Un
      // découpage qui n'accepterait que les guillemets doubles laisserait le
      // texte visible mais le lien mort — le crédit resterait lisible, sans
      // mener à la page de licence qu'il annonce.
      final List<PdlCreditSegment> segments = parsePdlCredit(
        "<a href='https://mapterhorn.com/attribution'>© Mapterhorn</a>",
      );
      expect(segments, <PdlCreditSegment>[
        const PdlCreditSegment(
          '© Mapterhorn',
          href: 'https://mapterhorn.com/attribution',
        ),
      ]);
    });
  });
}
