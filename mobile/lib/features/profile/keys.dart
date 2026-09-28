import 'package:flutter/widgets.dart';

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
}
