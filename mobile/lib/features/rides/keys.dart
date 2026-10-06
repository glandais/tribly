import 'package:flutter/widgets.dart';

class _RideDetailKey extends ValueKey<String> {
  const _RideDetailKey(String value) : super('rideDetail_$value');
}

class RideDetailKeys {
  /// Le bandeau « Sortie annulée » sous l'identité.
  final cancelledBanner = const _RideDetailKey('cancelledBanner');

  /// Le badge « Terminée » d'une sortie passée.
  final finishedBadge = const _RideDetailKey('finishedBadge');

  /// La pastille de la carte des groupes, qui nomme le groupe sélectionné.
  final groupsMapPill = const _RideDetailKey('groupsMapPill');

  final loadError = const _RideDetailKey('loadError');

  /// « Réessayer » de l'état d'erreur.
  final loadErrorRetryButton = const _RideDetailKey('loadErrorRetryButton');

  /// Le `⋯` de l'app bar : signaler la sortie.
  final moreButton = const _RideDetailKey('moreButton');

  /// « Voir la liste » du bloc méta : les participants de toute la sortie.
  final participantsButton = const _RideDetailKey('participantsButton');

  final title = const _RideDetailKey('title');

  /// Le bandeau d'échec d'inscription de la section Groupes, quel qu'en soit
  /// le motif.
  final registrationFailure = const _RideDetailKey('registrationFailure');

  /// Le bouton du bandeau d'exclusivité : quitter l'autre groupe, puis entrer.
  final switchGroupButton = const _RideDetailKey('switchGroupButton');

  /// La carte météo compacte du détail.
  final weatherCard = const _RideDetailKey('weatherCard');

  /// « Voir la météo du parcours » de la carte météo.
  final weatherOpenButton = const _RideDetailKey('weatherOpenButton');

  /// « Météo indisponible » (statut `UNAVAILABLE`, ou échec de l'appel).
  final weatherUnavailable = const _RideDetailKey('weatherUnavailable');

  /// « Réessayer » de la météo indisponible.
  final weatherRetryButton = const _RideDetailKey('weatherRetryButton');

  /// « Prévision disponible à partir du… ».
  final weatherNotYetAvailable = const _RideDetailKey('weatherNotYetAvailable');

  /// Le mot aux organisateurs : il manque un lieu ou un parcours.
  final weatherNoLocation = const _RideDetailKey('weatherNoLocation');

  /// L'état neutre de l'écran « Météo du parcours » quand il n'y a rien à
  /// montrer (`OUT_OF_RANGE`, `NO_LOCATION` pour un membre, statut inconnu).
  final weatherEmpty = const _RideDetailKey('weatherEmpty');

  /// Le crédit des prévisions (CC BY 4.0) de la carte météo compacte.
  final weatherAttribution = const _RideDetailKey('weatherAttribution');

  /// « Prévisions pas encore disponibles le long du parcours » : étape sans
  /// prévision (`UNAVAILABLE`, `NOT_YET_AVAILABLE`, statut inconnu) ou sans point.
  final weatherLegUnavailable = const _RideDetailKey('weatherLegUnavailable');

  /// La ligne de résumé météo d'une carte de sortie.
  final weatherSummary = const _RideDetailKey('weatherSummary');

  /// La marque « Prévision ancienne » (`STALE`) de la ligne de résumé météo.
  final weatherSummaryStale = const _RideDetailKey('weatherSummaryStale');

  /// Un point de passage de la frise de l'écran « Météo du parcours ».
  ValueKey<String> weatherCheckpoint(int index) =>
      _RideDetailKey('weatherCheckpoint_$index');

  /// La puce d'un groupe dans le sélecteur de l'écran « Météo du parcours ».
  ValueKey<String> weatherGroupChip(String groupId) =>
      _RideDetailKey('weatherGroupChip_$groupId');

  ValueKey<String> group(String groupId) => _RideDetailKey('group_$groupId');

  ValueKey<String> groupLeader(String groupId) =>
      _RideDetailKey('groupLeader_$groupId');

  ValueKey<String> groupJoinButton(String groupId) =>
      _RideDetailKey('groupJoin_$groupId');

  ValueKey<String> groupLeaveButton(String groupId) =>
      _RideDetailKey('groupLeave_$groupId');

  /// « Télécharger le GPX » de la carte d'un groupe.
  ValueKey<String> groupExportGpx(String groupId) =>
      _RideDetailKey('groupExportGpx_$groupId');

  /// « Télécharger le FIT » de la carte d'un groupe.
  ValueKey<String> groupExportFit(String groupId) =>
      _RideDetailKey('groupExportFit_$groupId');

  /// « Envoyer vers un appareil » de la carte d'un groupe.
  ValueKey<String> groupSendToDevice(String groupId) =>
      _RideDetailKey('groupSendToDevice_$groupId');

  /// Le « Complet » désactivé d'un groupe plein.
  ValueKey<String> groupFullButton(String groupId) =>
      _RideDetailKey('groupFull_$groupId');
}
