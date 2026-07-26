import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/features/routes/domain/route_filters.dart';
import 'package:pedalons/features/routes/providers/route_list_provider.dart';

void main() {
  group('portée', () {
    test('elle compte dans le badge du bouton de filtres', () {
      // §3.1 : en vue Carte les chips disparaissent, le compteur est le seul
      // témoin restant — il doit donc compter la portée.
      expect(const RouteFilters().activeCount, 0);
      expect(const RouteFilters(teamSlug: 'n-peloton').activeCount, 1);
      expect(const RouteFilters(minRole: MinRole.admin).activeCount, 1);
      expect(
        const RouteFilters(
          teamSlug: 'n-peloton',
          surfaceType: SurfaceType.gravel,
        ).activeCount,
        2,
      );
    });

    test('la recherche ne compte pas, elle a son propre champ', () {
      expect(const RouteFilters(search: 'ventoux').activeCount, 0);
    });

    test('« tout réinitialiser » garde la portée', () {
      const RouteFilters filters = RouteFilters(
        teamSlug: 'n-peloton',
        search: 'ventoux',
        surfaceType: SurfaceType.gravel,
      );
      final RouteFilters cleared = filters.cleared;
      expect(cleared.teamSlug, 'n-peloton');
      expect(cleared.search, isNull);
      expect(cleared.surfaceType, isNull);
    });
  });

  group('proximité', () {
    test('une latitude seule n\'est pas une position', () {
      expect(const RouteFilters(nearLat: 47.2).hasProximity, isFalse);
      expect(
        const RouteFilters(nearLat: 47.2, nearLon: -1.5).hasProximity,
        isTrue,
      );
    });

    test('la retirer efface la position et le rayon', () {
      const RouteFilters armed = RouteFilters(
        nearLat: 47.2,
        nearLon: -1.5,
        nearRadius: 50000,
      );
      final RouteFilters off = armed.without(RouteFilterField.proximity);
      expect(off.hasProximity, isFalse);
      expect(off.nearRadius, isNull);
    });
  });

  group('ordre d\'ajout', () {
    test('il suit les filtres posés, et oublie ceux qu\'on retire', () {
      final RouteFilters a = const RouteFilters().copyWith(
        surfaceType: SurfaceType.gravel,
      );
      final RouteFilters b = a.copyWith(hilliness: Hilliness.hilly);
      expect(b.activeFieldsNewestFirst, <RouteFilterField>[
        RouteFilterField.hilliness,
        RouteFilterField.surfaceType,
      ]);

      final RouteFilters c = b.without(RouteFilterField.surfaceType);
      expect(c.activeFieldsNewestFirst, <RouteFilterField>[
        RouteFilterField.hilliness,
      ]);
    });

    test('il ne participe pas à l\'identité du jeu de résultats', () {
      // Deux mêmes filtres posés dans deux ordres désignent la même liste et
      // doivent partager le même notifier : sinon changer l'ordre relancerait
      // une pagination complète pour rien.
      final RouteFilters ab = const RouteFilters()
          .copyWith(surfaceType: SurfaceType.gravel)
          .copyWith(hilliness: Hilliness.hilly);
      final RouteFilters ba = const RouteFilters()
          .copyWith(hilliness: Hilliness.hilly)
          .copyWith(surfaceType: SurfaceType.gravel);
      expect(ab, ba);
      expect(ab.hashCode, ba.hashCode);
      expect(ab.activeFieldsNewestFirst, isNot(ba.activeFieldsNewestFirst));
    });
  });

  group('densité', () {
    test('201 résultats basculent d\'office en compact', () {
      expect(effectiveRouteDensity(null, 200), RouteListDensity.cards);
      expect(effectiveRouteDensity(null, 201), RouteListDensity.compact);
    });

    test('le choix manuel écrase la règle', () {
      expect(
        effectiveRouteDensity(RouteListDensity.cards, 5000),
        RouteListDensity.cards,
      );
      expect(
        effectiveRouteDensity(RouteListDensity.compact, 3),
        RouteListDensity.compact,
      );
    });

    test('un total inconnu retombe sur les vignettes', () {
      expect(effectiveRouteDensity(null, null), RouteListDensity.cards);
    });
  });
}
