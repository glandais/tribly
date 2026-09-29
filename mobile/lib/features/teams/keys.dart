import 'package:flutter/widgets.dart';

class _TeamsPageKey extends ValueKey<String> {
  const _TeamsPageKey(String value) : super('teamsPage_$value');
}

class TeamsPageKeys {
  final discoverButton = const _TeamsPageKey('discoverButton');

  /// La carte d'une équipe de « Mes équipes ».
  ValueKey<String> teamCard(String teamSlug) =>
      _TeamsPageKey('teamCard_$teamSlug');

  /// « Vous avez des invitations en attente », en tête de la liste.
  final pendingInvitationsCard = const _TeamsPageKey('pendingInvitations');

  /// « Accepter », sur la ligne de l'invitation à [teamSlug].
  ValueKey<String> invitationAcceptButton(String teamSlug) =>
      _TeamsPageKey('invitationAccept_$teamSlug');
}

class _TeamsDiscoverKey extends ValueKey<String> {
  const _TeamsDiscoverKey(String value) : super('teamsDiscover_$value');
}

class TeamsDiscoverKeys {
  final searchField = const _TeamsDiscoverKey('searchField');

  ValueKey<String> card(String teamSlug) => _TeamsDiscoverKey('card_$teamSlug');
}

class _TeamPageKey extends ValueKey<String> {
  const _TeamPageKey(String value) : super('teamPage_$value');
}

class TeamPageKeys {
  final loadError = const _TeamPageKey('loadError');

  /// « Réessayer » de l'état d'erreur.
  final loadErrorRetryButton = const _TeamPageKey('loadErrorRetryButton');

  final joinButton = const _TeamPageKey('joinButton');
  final leaveButton = const _TeamPageKey('leaveButton');
  final inviteOnlyButton = const _TeamPageKey('inviteOnlyButton');
  final leaveConfirmButton = const _TeamPageKey('leaveConfirmButton');

  /// La ligne d'une page libre, dans la section « À propos ».
  ValueKey<String> aboutPageRow(String pageSlug) =>
      _TeamPageKey('aboutPageRow_$pageSlug');

  /// L'écran d'une page libre : son titre, son corps.
  final customPageTitle = const _TeamPageKey('customPageTitle');
  final customPageBody = const _TeamPageKey('customPageBody');

  /// La ligne d'un membre dans la section « Membres », par l'identifiant de
  /// l'utilisateur.
  ValueKey<String> memberRow(String userId) =>
      _TeamPageKey('memberRow_$userId');

  /// La recherche de la section « Membres ».
  final membersSearchField = const _TeamPageKey('membersSearchField');
}
