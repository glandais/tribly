import 'package:flutter/widgets.dart';

class _TeamsPageKey extends ValueKey<String> {
  const _TeamsPageKey(String value) : super('teamsPage_$value');
}

class TeamsPageKeys {
  final discoverButton = const _TeamsPageKey('discoverButton');
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
  final joinButton = const _TeamPageKey('joinButton');
  final leaveButton = const _TeamPageKey('leaveButton');
  final inviteOnlyButton = const _TeamPageKey('inviteOnlyButton');
  final leaveConfirmButton = const _TeamPageKey('leaveConfirmButton');
}
