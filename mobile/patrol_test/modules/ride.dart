import 'module.dart';

/// The ride page: its groups, their leaders, and joining one.
final class Ride extends Module {
  Ride(super.$);

  Future<void> waitUntilShown() async {
    await $(keys.ride.title).waitUntilVisible();
  }

  /// Waits for the page's error state, and returns how long it took to come.
  ///
  /// The error is placed, not tapped: Patrol's « visible » (hit-testable at its centre) doesn't
  /// hold for an empty state, so it is waited for as present in the tree.
  Future<Duration> waitUntilLoadErrorIsShown({
    Duration timeout = const Duration(seconds: 60),
  }) async {
    final start = DateTime.now();
    await $(keys.ride.loadError).waitUntilExists(timeout: timeout);
    return DateTime.now().difference(start);
  }

  /// The ride's name as the page shows it, or null when no ride is shown.
  String? get title =>
      $(keys.ride.title).exists ? $(keys.ride.title).text : null;

  /// The leader line of the group, or null when the card has none.
  Future<bool> showsLeader(String groupId, String name) async {
    await scrolledTo(keys.ride.group(groupId));
    return shows(keys.ride.groupLeader(groupId), name);
  }

  Future<bool> hasLeaderLine(String groupId) async {
    await scrolledTo(keys.ride.group(groupId));
    return isShown(keys.ride.groupLeader(groupId));
  }

  /// Whether [text] appears anywhere on the group's card.
  bool groupShows(String groupId, String text) =>
      shows(keys.ride.group(groupId), text);

  Future<void> join(String groupId) async {
    await (await scrolledTo(keys.ride.groupJoinButton(groupId))).tap();
    await $(keys.ride.groupLeaveButton(groupId)).waitUntilVisible();
  }

  /// Taps « Rejoindre » on [groupId] while registered elsewhere, then takes the banner's way out:
  /// leave the other group and join this one.
  Future<void> switchTo(String groupId) async {
    await (await scrolledTo(keys.ride.groupJoinButton(groupId))).tap();
    await (await scrolledTo(keys.ride.switchGroupButton)).tap();
    await $(keys.ride.groupLeaveButton(groupId)).waitUntilVisible();
  }

  bool offersJoin(String groupId) =>
      isShown(keys.ride.groupJoinButton(groupId));

  bool offersLeave(String groupId) =>
      isShown(keys.ride.groupLeaveButton(groupId));

  /// Taps « Rejoindre » on [groupId] and waits for nothing: the answer may be a refusal.
  Future<void> tapJoin(String groupId) async {
    await (await scrolledTo(keys.ride.groupJoinButton(groupId))).tap();
  }

  /// Waits until [groupId] is shown as joined — its « Quitter » is there.
  Future<void> waitUntilRegisteredIn(
    String groupId, {
    Duration timeout = const Duration(seconds: 15),
  }) async {
    await $(
      keys.ride.groupLeaveButton(groupId),
    ).waitUntilExists(timeout: timeout);
  }

  /// Waits until the card of [groupId] shows the disabled « Complet ».
  Future<void> waitUntilFull(String groupId) async {
    await $(keys.ride.groupFullButton(groupId)).waitUntilExists();
  }

  bool offersFull(String groupId) =>
      isShown(keys.ride.groupFullButton(groupId));

  /// Waits for the registration failure banner, and tells whether it names [groupName].
  Future<bool> waitUntilFailureNames(String groupName) async {
    await $(keys.ride.registrationFailure).waitUntilExists();
    return shows(keys.ride.registrationFailure, groupName);
  }

  bool get showsFailure => isShown(keys.ride.registrationFailure);

  /// Lets the refetch that follows a registration settle.
  Future<void> settle() async {
    await $.pump(const Duration(seconds: 2));
    await $.pumpAndTrySettle();
  }
}
