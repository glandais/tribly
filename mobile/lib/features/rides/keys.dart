import 'package:flutter/widgets.dart';

class _RideDetailKey extends ValueKey<String> {
  const _RideDetailKey(String value) : super('rideDetail_$value');
}

class RideDetailKeys {
  final loadError = const _RideDetailKey('loadError');
  final title = const _RideDetailKey('title');

  /// Le bandeau d'échec d'inscription de la section Groupes, quel qu'en soit
  /// le motif.
  final registrationFailure = const _RideDetailKey('registrationFailure');

  /// Le bouton du bandeau d'exclusivité : quitter l'autre groupe, puis entrer.
  final switchGroupButton = const _RideDetailKey('switchGroupButton');

  ValueKey<String> group(String groupId) => _RideDetailKey('group_$groupId');

  ValueKey<String> groupLeader(String groupId) =>
      _RideDetailKey('groupLeader_$groupId');

  ValueKey<String> groupJoinButton(String groupId) =>
      _RideDetailKey('groupJoin_$groupId');

  ValueKey<String> groupLeaveButton(String groupId) =>
      _RideDetailKey('groupLeave_$groupId');

  /// Le « Complet » désactivé d'un groupe plein.
  ValueKey<String> groupFullButton(String groupId) =>
      _RideDetailKey('groupFull_$groupId');
}
