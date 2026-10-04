import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';

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

  /// « Notifications » of the profile (`Profile.openNotificationSettings`) → the team's switch
  /// « receive its announcements », toggled.
  Future<void> toggleTeamInProfile(String teamSlug) async {
    await (await scrolledTo(keys.notifications.teamSwitch(teamSlug))).tap();
  }

  // ── The bell ────────────────────────────────────────────────────────────

  bool get bellShowsBadge => $(keys.notifications.bell).$(_badge).exists;

  /// Waits until the bell carries no badge any more.
  Future<void> waitUntilBellBadgeIsGone({
    Duration timeout = const Duration(seconds: 10),
  }) async {
    await $(keys.notifications.bell).waitUntilVisible();
    final deadline = DateTime.now().add(timeout);
    while (bellShowsBadge) {
      if (DateTime.now().isAfter(deadline)) {
        throw TestFailure('the bell still shows a badge after $timeout');
      }
      await $.pump(const Duration(milliseconds: 200));
    }
  }

  static final RegExp _badge = RegExp(r'^\d+\+?$');

  // ── The inbox filter ────────────────────────────────────────────────────

  /// « Toutes », the first segment of the filter.
  Future<void> showAll() => _tapFilterSegment(keys.notifications.filterAll);

  /// « Non lues », the second segment.
  Future<void> showUnreadOnly() =>
      _tapFilterSegment(keys.notifications.filterUnread);

  Future<void> _tapFilterSegment(Key segment) async {
    await $(segment).tap();
    await $.pump(const Duration(milliseconds: 500));
  }

  bool isEntryShown(String notificationId) =>
      isShown(keys.notifications.tile(notificationId));

  /// « Tout marquer lu », in the inbox's app bar.
  Future<void> markAllRead() async {
    await $(keys.notifications.markAllReadButton).tap();
    await $.pump(const Duration(milliseconds: 500));
  }

  /// The empty state of the « Non lues » filter — « Aucune non lue » — once it shows.
  Future<void> waitUntilUnreadEmptyStateIsShown({
    Duration timeout = const Duration(seconds: 10),
  }) async {
    await $(
      keys.notifications.unreadEmptyState,
    ).waitUntilVisible(timeout: timeout);
  }

  /// The empty state's way out: « Toutes ».
  Future<void> tapShowAllFromEmptyState() async {
    await $(keys.notifications.showAllButton).tap();
  }
}
