import 'package:flutter/widgets.dart';

class _HomeKey extends ValueKey<String> {
  const _HomeKey(String value) : super('home_$value');
}

class HomeKeys {
  /// La carte « Ma prochaine sortie », portant le slug de la sortie qu'elle
  /// nomme.
  ValueKey<String> nextRideCard(String rideSlug) =>
      _HomeKey('nextRide_$rideSlug');

  /// « Se désinscrire » sur la carte « Ma prochaine sortie ».
  final nextRideLeaveButton = const _HomeKey('nextRideLeave');

  /// La carte compacte qui remplace le bloc quand rien n'est à venir.
  final noNextRideCard = const _HomeKey('noNextRide');

  /// Une carte du carrousel « À venir », sortie ou voyage.
  ValueKey<String> upcomingCard(String slug) => _HomeKey('upcomingCard_$slug');

  /// « Rejoindre » d'une sortie à groupe unique (ouvre le détail en
  /// `autoJoin`).
  ValueKey<String> upcomingJoinButton(String slug) =>
      _HomeKey('upcomingJoin_$slug');

  /// « Choisir un groupe » d'une sortie à plusieurs groupes.
  ValueKey<String> upcomingChooseGroupButton(String slug) =>
      _HomeKey('upcomingChooseGroup_$slug');

  /// Le badge « Inscrit » d'une carte du carrousel.
  ValueKey<String> upcomingRegisteredBadge(String slug) =>
      _HomeKey('upcomingRegistered_$slug');
}
