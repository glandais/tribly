import 'package:flutter/widgets.dart';

class _NotificationsKey extends ValueKey<String> {
  const _NotificationsKey(String value) : super('notifications_$value');
}

class NotificationsKeys {
  final bell = const _NotificationsKey('bell');
  final markAllReadButton = const _NotificationsKey('markAllReadButton');

  ValueKey<String> tile(String notificationId) =>
      _NotificationsKey('tile_$notificationId');

  /// L'interrupteur « je reçois ses annonces » d'une équipe, dans le profil.
  ValueKey<String> teamSwitch(String teamSlug) =>
      _NotificationsKey('teamSwitch_$teamSlug');
}
