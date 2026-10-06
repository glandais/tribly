import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/config/locale_context.dart';
import 'package:pedalons/config/paths.dart';
import 'package:pedalons/features/teams/presentation/team_web_paths.dart';

/// Les pages du site qu'ouvre le tableau de bord viennent de
/// `contracts/routes.yaml` (docs/LEDGER_*.md MOB-54), dans la langue de l'app.
/// Deux d'entre elles portent en plus un état que le web passe en état de
/// routeur, qu'une URL ne transporte pas — le site lit donc ces paramètres
/// (`CreateRidePage`, `TeamMembersPage`).
void main() {
  tearDown(() => setCurrentLocale('fr'));

  test('« Créer depuis un modèle » nomme le modèle dans l\'URL', () {
    setCurrentLocale('en');
    expect(
      TeamWebPaths.rideNewFromTemplate('velo-club', 'sortie du dimanche'),
      '/teams/velo-club/rides/new?template=sortie+du+dimanche',
    );
    setCurrentLocale('fr');
    expect(
      TeamWebPaths.rideNewFromTemplate('velo-club', 'sortie du dimanche'),
      '/equipes/velo-club/sorties/nouvelle?template=sortie+du+dimanche',
    );
  });

  test('« Inviter » ouvre le dialogue d\'invitation', () {
    setCurrentLocale('en');
    expect(
      TeamWebPaths.adminMembersInvite('velo-club'),
      '/teams/velo-club/admin/members?invite=1',
    );
    setCurrentLocale('fr');
    expect(
      TeamWebPaths.adminMembersInvite('velo-club'),
      '/equipes/velo-club/admin/membres?invite=1',
    );
  });

  test('chaque page du tableau de bord est une page du site sans écran', () {
    // `appScreen: false` dans le contrat : le lien part dans le navigateur,
    // jamais sur le routeur de l'app.
    for (final String id in <String>[
      'rideNew',
      'rideEdit',
      'postNew',
      'postEdit',
      'tripEdit',
      'rideTemplates',
      'teamAdminReports',
      'teamAdminMembers',
      'teamSettings',
    ]) {
      expect(webOnlyRouteIds, contains(id));
    }
  });
}
