/// Les pages **du site** qu'ouvre le tableau de bord d'un organisateur.
///
/// Créer ou modifier une sortie, une publication ou un voyage, traiter un
/// signalement, gérer les membres et les réglages de l'équipe : l'application
/// n'a d'écran pour aucun de ces gestes. Le tableau de bord les propose
/// néanmoins — parité avec le web —, et les ouvre dans le navigateur intégré
/// par `openWebPage`.
///
/// Ces routes sont `web` seulement dans `contracts/routes.yaml`, donc absentes
/// de `paths.generated.dart`. Les chemins sont écrits ici dans leur variante
/// anglaise, que le site sert quelle que soit la langue — même choix que
/// `NotificationDisplay.webPath` pour la file de signalements.
abstract final class TeamWebPaths {
  static String rideNew(String teamSlug) => '/teams/$teamSlug/rides/new';

  /// Une sortie à créer depuis le modèle [templateSlug] : `CreateRidePage` lit
  /// `?template=` et charge le modèle (le web, lui, le passe en état de
  /// routeur, qu'une URL ne transporte pas).
  static String rideNewFromTemplate(String teamSlug, String templateSlug) =>
      '/teams/$teamSlug/rides/new?template=${Uri.encodeQueryComponent(templateSlug)}';

  static String rideEdit(String teamSlug, String rideSlug) =>
      '/teams/$teamSlug/rides/$rideSlug/edit';

  static String postNew(String teamSlug) => '/teams/$teamSlug/posts/new';

  static String postEdit(String teamSlug, String postSlug) =>
      '/teams/$teamSlug/posts/$postSlug/edit';

  static String tripEdit(String teamSlug, String tripSlug) =>
      '/teams/$teamSlug/trips/$tripSlug/edit';

  static String rideTemplates(String teamSlug) =>
      '/teams/$teamSlug/admin/ride-templates';

  static String reports(String teamSlug) => '/teams/$teamSlug/admin/reports';

  static String adminMembers(String teamSlug) =>
      '/teams/$teamSlug/admin/members';

  /// La page des membres, dialogue d'invitation ouvert : `TeamMembersPage` lit
  /// `?invite=1` (le web passe `OPEN_INVITE_STATE` en état de routeur).
  static String adminMembersInvite(String teamSlug) =>
      '/teams/$teamSlug/admin/members?invite=1';

  static String settings(String teamSlug) => '/teams/$teamSlug/admin/settings';
}
