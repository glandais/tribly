import 'module.dart';

/// The home bell, the inbox, and the per-team switches of the profile.
final class Notifications extends Module {
  Notifications(super.$);

  /// The bell's badge once it shows one: the number it reads. The bell polls `unread-count`
  /// as the session starts, then once a minute.
  Future<String> waitForBellBadge() async {
    await $(keys.notifications.bell).waitUntilVisible();
    final badge = $(keys.notifications.bell).$(RegExp(r'^\d+\+?$'));
    await badge.waitUntilExists();
    return badge.text!;
  }

  Future<void> openInboxFromBell() async {
    await $(keys.notifications.bell).tap();
    await $(keys.notifications.markAllReadButton).waitUntilVisible();
  }

  Future<void> waitUntilEntryIsShown(String notificationId) async {
    await scrolledTo(keys.notifications.tile(notificationId));
  }

  /// Whether the entry reads [text] — its title, subject, team or date line.
  bool entryShows(String notificationId, String text) =>
      shows(keys.notifications.tile(notificationId), text);

  Future<void> openEntry(String notificationId) async {
    await $(keys.notifications.tile(notificationId)).tap();
  }

  /// Profile → the team's switch « receive its announcements », toggled.
  Future<void> toggleTeamInProfile(String teamSlug) async {
    await (await scrolledTo(keys.notifications.teamSwitch(teamSlug))).tap();
  }
}
