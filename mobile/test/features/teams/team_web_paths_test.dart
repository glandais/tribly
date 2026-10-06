import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/features/teams/presentation/team_web_paths.dart';

/// Les deux pages du site que le tableau de bord ouvre avec un état : le web
/// le passe en état de routeur, qu'une URL ne transporte pas — le site lit
/// donc ces paramètres (`CreateRidePage`, `TeamMembersPage`).
void main() {
  test('« Créer depuis un modèle » nomme le modèle dans l\'URL', () {
    expect(
      TeamWebPaths.rideNewFromTemplate('velo-club', 'sortie du dimanche'),
      '/teams/velo-club/rides/new?template=sortie+du+dimanche',
    );
  });

  test('« Inviter » ouvre le dialogue d\'invitation', () {
    expect(
      TeamWebPaths.adminMembersInvite('velo-club'),
      '/teams/velo-club/admin/members?invite=1',
    );
  });
}
