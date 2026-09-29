import 'package:flutter/widgets.dart';

import '../../api/generated/export.dart';

class _ProfilePageKey extends ValueKey<String> {
  const _ProfilePageKey(String value) : super('profilePage_$value');
}

class ProfilePageKeys {
  final logoutButton = const _ProfilePageKey('logoutButton');

  /// Le bouton qui confirme une action destructive (`confirmDestructive`).
  final confirmDestructiveButton = const _ProfilePageKey(
    'confirmDestructiveButton',
  );

  /// Le texte de la feuille de confirmation destructive — la conséquence,
  /// équipes supprimées avec le compte comprises.
  final confirmDestructiveMessage = const _ProfilePageKey(
    'confirmDestructiveMessage',
  );

  /// « Supprimer mon compte », dans la zone de danger.
  final deleteAccountButton = const _ProfilePageKey('deleteAccountButton');

  /// Le bandeau qui refuse la suppression en nommant les équipes qui la
  /// bloquent (seul administrateur d'une équipe qui a d'autres membres).
  final deletionBlockedBanner = const _ProfilePageKey('deletionBlockedBanner');

  /// « Mes sorties à venir » et « Historique », et leurs compteurs.
  final participationsUpcomingRow = const _ProfilePageKey(
    'participationsUpcomingRow',
  );
  final participationsUpcomingCount = const _ProfilePageKey(
    'participationsUpcomingCount',
  );
  final participationsHistoryRow = const _ProfilePageKey(
    'participationsHistoryRow',
  );

  /// Une publication de la liste « Mes participations », par son slug.
  Key participationCard(String slug) =>
      _ProfilePageKey('participationCard_$slug');

  /// Réglages : l'interrupteur « Être contacté par les membres ».
  final contactableSwitch = const _ProfilePageKey('contactableSwitch');

  /// « Vos données » : le bouton de demande et la ligne d'état de l'export.
  final dataExportButton = const _ProfilePageKey('dataExportButton');
  final dataExportStatus = const _ProfilePageKey('dataExportStatus');

  /// Identité : le champ « Nom affiché » et son « Enregistrer ».
  final displayNameField = const _ProfilePageKey('displayNameField');
  final displayNameSave = const _ProfilePageKey('displayNameSave');

  /// La ligne « Langue », et une langue de sa feuille, par son code.
  final languageRow = const _ProfilePageKey('languageRow');
  Key languageOption(String code) => _ProfilePageKey('languageOption_$code');

  /// « Déconnecter tous les appareils ».
  final logoutAllButton = const _ProfilePageKey('logoutAllButton');

  /// Un segment du sélecteur de thème, par sa valeur.
  Key themeSegment(ThemePreference theme) =>
      _ProfilePageKey('themeSegment_${theme.name}');

  /// Un segment du sélecteur d'unités, par sa valeur, et l'exemple chiffré.
  Key unitSegment(UnitSystem unit) =>
      _ProfilePageKey('unitSegment_${unit.name}');
  final unitsExample = const _ProfilePageKey('unitsExample');

  /// « À propos » : les lignes « Applications » et « Signaler un problème ».
  final appsRow = const _ProfilePageKey('appsRow');
  final reportProblemRow = const _ProfilePageKey('reportProblemRow');
}
