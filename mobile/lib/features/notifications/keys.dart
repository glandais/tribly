import 'package:flutter/widgets.dart';

class _NotificationsKey extends ValueKey<String> {
  const _NotificationsKey(String value) : super('notifications_$value');
}

class NotificationsKeys {
  final bell = const _NotificationsKey('bell');
  final markAllReadButton = const _NotificationsKey('markAllReadButton');

  /// Le sélecteur « Toutes | Non lues », et chacun de ses deux segments.
  final filter = const _NotificationsKey('filter');
  final filterAll = const _NotificationsKey('filterAll');
  final filterUnread = const _NotificationsKey('filterUnread');

  /// La boîte vide sous le filtre « Non lues », et sa sortie « Toutes ».
  final unreadEmptyState = const _NotificationsKey('unreadEmptyState');
  final showAllButton = const _NotificationsKey('showAllButton');

  ValueKey<String> tile(String notificationId) =>
      _NotificationsKey('tile_$notificationId');

  /// L'interrupteur « je reçois ses annonces » d'une équipe, dans le profil.
  ValueKey<String> teamSwitch(String teamSlug) =>
      _NotificationsKey('teamSwitch_$teamSlug');

  /// Le bandeau qui propose d'activer le push, en tête de la boîte.
  final pushActivationBanner = const _NotificationsKey('pushActivationBanner');
}
