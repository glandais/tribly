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

  /// « Envoyer vers le compteur » sur la carte « Ma prochaine sortie ».
  final nextRideSendToDevice = const _HomeKey('nextRideSendToDevice');

  /// Le bloc « Cette semaine ».
  final weekSection = const _HomeKey('weekSection');

  /// Une ligne de « Cette semaine », par le slug de la sortie ou de l'étape.
  ValueKey<String> weekEvent(String entitySlug) =>
      _HomeKey('weekEvent_$entitySlug');

  /// La carte qui remplace l'agenda quand la semaine est vide.
  final weekEmpty = const _HomeKey('weekEmpty');

  /// Le bloc « Mes équipes ».
  final teamsSection = const _HomeKey('teamsSection');

  /// Une équipe de « Mes équipes », par son slug.
  ValueKey<String> teamRow(String teamSlug) => _HomeKey('teamRow_$teamSlug');

  /// « Trouver une équipe », quand l'utilisateur n'est membre d'aucune.
  final findTeamButton = const _HomeKey('findTeam');
}
