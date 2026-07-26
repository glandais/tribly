import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/core/geo/location_service.dart';

/// Ce qui compte dans `LocationFailureBanner` est la **décision** : quelle
/// issue proposer pour quel échec. Le rendu du bandeau lui-même est couvert
/// par les tests de `PdlBanner`.
///
/// Ces règles sont testées ici plutôt que dans un test de widget parce
/// qu'elles sont pures : les monter dans un arbre n'ajouterait qu'une
/// dépendance à l'initialisation d'easy_localization, sans rien prouver de
/// plus.
void main() {
  test('les quatre échecs ont chacun leur message', () {
    // Un message commun à deux causes enverrait l'utilisateur au mauvais
    // endroit : les réglages du système et ceux de l'application ne sont pas
    // le même écran.
    final Set<String> keys = LocationFailure.values
        .map(LocationService.messageKey)
        .toSet();
    expect(keys, hasLength(LocationFailure.values.length));
  });

  test('seuls les deux échecs sans recours renvoient aux réglages', () {
    // Redemander après un refus définitif est inutile : sur iOS le dialogue
    // ne s'affiche même plus et l'appel échoue en silence.
    expect(
      LocationService.needsSettings(LocationFailure.deniedForever),
      isTrue,
    );
    expect(
      LocationService.needsSettings(LocationFailure.serviceDisabled),
      isTrue,
    );
    // Ces deux-là valent la peine d'être retentés.
    expect(LocationService.needsSettings(LocationFailure.denied), isFalse);
    expect(LocationService.needsSettings(LocationFailure.unavailable), isFalse);
  });

  test('un échec ne porte jamais de position', () {
    for (final LocationFailure f in LocationFailure.values) {
      expect(LocationResult.failed(f).position, isNull);
      expect(LocationResult.failed(f).isSuccess, isFalse);
    }
  });
}
