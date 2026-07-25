import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/config/router.dart';

/// The ancestors returned here are what the deep link handler pushes below the
/// target page, so a link opened from outside the app always has a back stack.
void main() {
  group('ancestorsForDeepLink', () {
    test('legal pages fall back to the app home', () {
      expect(ancestorsForDeepLink('/confidentialite'), ['/']);
      expect(ancestorsForDeepLink('/privacy'), ['/']);
      expect(ancestorsForDeepLink('/cgu'), ['/']);
      expect(ancestorsForDeepLink('/terms'), ['/']);
    });

    test(
      'device verification pages keep their query string out of the match',
      () {
        expect(ancestorsForDeepLink('/garmin?code=ABC123'), ['/']);
        expect(ancestorsForDeepLink('/karoo'), ['/']);
      },
    );

    // Team-scoped chains start at the teams tab, not at home: stacking two main
    // shell tabs would leave the shell state — and so the highlighted tab — on
    // the first one.
    test('team pages go back to the teams list, in the link locale', () {
      expect(ancestorsForDeepLink('/equipes/velo-club'), ['/equipes']);
      expect(ancestorsForDeepLink('/teams/velo-club'), ['/teams']);
      expect(ancestorsForDeepLink('/equipes/velo-club/a-propos'), ['/equipes']);
      expect(ancestorsForDeepLink('/equipes/velo-club/parcours'), ['/equipes']);
    });

    test('detail pages rebuild their full hierarchy', () {
      expect(
        ancestorsForDeepLink('/equipes/velo-club/sorties/rando-du-dimanche'),
        ['/equipes', '/equipes/velo-club'],
      );
      expect(
        ancestorsForDeepLink('/equipes/velo-club/parcours/col-du-galibier'),
        ['/equipes', '/equipes/velo-club', '/equipes/velo-club/parcours'],
      );
      expect(
        ancestorsForDeepLink('/teams/velo-club/trips/alpes/stages/etape-1'),
        ['/teams', '/teams/velo-club', '/teams/velo-club/trips/alpes'],
      );
    });

    test('pages that carry their own navigation need no ancestor', () {
      expect(ancestorsForDeepLink('/'), isEmpty);
      expect(ancestorsForDeepLink('/equipes'), isEmpty);
      expect(ancestorsForDeepLink('/profil'), isEmpty);
      expect(ancestorsForDeepLink('/calendrier'), isEmpty);
      expect(ancestorsForDeepLink('/connexion'), isEmpty);
    });
  });
}
