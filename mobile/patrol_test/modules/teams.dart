import 'module.dart';
import 'navigation.dart';

/// Finding a team, and joining or leaving it from its header.
final class Teams extends Module {
  Teams(super.$);

  late final _navigation = Navigation($);

  /// Teams tab → « Découvrir » → search for [name] → the team's card.
  Future<void> openFromDiscovery({
    required String slug,
    required String name,
  }) async {
    await _navigation.goToTeams();
    await $(keys.teams.discoverButton).tap();
    await $(keys.teamsDiscover.searchField).enterText(name);
    await $(keys.teamsDiscover.card(slug)).tap();
    await waitUntilTeamIsShown();
  }

  /// Searches discovery for [name] and tells whether [slug]'s card came up.
  Future<bool> discoveryFinds({
    required String slug,
    required String name,
  }) async {
    await _navigation.goToTeams();
    await $(keys.teams.discoverButton).tap();
    await $(keys.teamsDiscover.searchField).enterText(name);
    // The search is debounced, then answered by the API.
    await $.pump(const Duration(seconds: 2));
    await $.pumpAndTrySettle();
    return isShown(keys.teamsDiscover.card(slug));
  }

  /// The team's header, with its membership action, whichever it is.
  Future<void> waitUntilTeamIsShown() async {
    await waitUntilAnyIsShown([
      keys.team.joinButton,
      keys.team.leaveButton,
      keys.team.inviteOnlyButton,
    ]);
  }

  Future<void> join() async {
    await $(keys.team.joinButton).tap();
    await $(keys.team.leaveButton).waitUntilVisible();
  }

  Future<void> leave() async {
    await $(keys.team.leaveButton).tap();
    await $(keys.team.leaveConfirmButton).tap();
    await $(keys.team.joinButton).waitUntilVisible();
  }

  bool get offersJoin => isShown(keys.team.joinButton);

  bool get offersLeave => isShown(keys.team.leaveButton);

  bool get saysInviteOnly => isShown(keys.team.inviteOnlyButton);

  /// Taps « Sur invitation », a disabled button: nothing may happen.
  Future<void> tapInviteOnly() async {
    await $(keys.team.inviteOnlyButton).tap();
    await $.pump(const Duration(seconds: 1));
  }

  /// Waits for the team's error state, and returns how long it took to come — see
  /// [Ride.waitUntilLoadErrorIsShown].
  Future<Duration> waitUntilLoadErrorIsShown({
    Duration timeout = const Duration(seconds: 60),
  }) async {
    final start = DateTime.now();
    await $(keys.team.loadError).waitUntilExists(timeout: timeout);
    return DateTime.now().difference(start);
  }
}
