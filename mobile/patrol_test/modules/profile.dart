import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:patrol/patrol.dart';

import 'module.dart';

final class Profile extends Module {
  Profile(super.$);

  Future<void> logOut() async {
    await $(keys.profile.logoutButton).scrollTo().tap();
  }

  Future<void> waitUntilShown() async {
    await $(keys.profile.participationsUpcomingRow).waitUntilVisible();
  }

  // ── Participations ──────────────────────────────────────────────────────

  /// The badge of « Mes sorties à venir », once its count has loaded.
  Future<int> upcomingCount() async {
    await $(keys.profile.participationsUpcomingCount).waitUntilExists();
    return int.parse(_upcomingBadgeText);
  }

  /// Waits until the badge of « Mes sorties à venir » reads [expected].
  Future<void> waitUntilUpcomingCountIs(
    int expected, {
    Duration timeout = const Duration(seconds: 10),
  }) async {
    final deadline = DateTime.now().add(timeout);
    while (!$(keys.profile.participationsUpcomingCount).exists ||
        _upcomingBadgeText != '$expected') {
      if (DateTime.now().isAfter(deadline)) {
        final shown = $(keys.profile.participationsUpcomingCount).exists
            ? _upcomingBadgeText
            : 'no badge';
        throw TestFailure(
          'upcoming participations badge not $expected after $timeout '
          '(shows $shown)',
        );
      }
      await $.pump(const Duration(milliseconds: 200));
    }
  }

  Future<void> openUpcomingParticipations() async {
    await (await scrolledTo(keys.profile.participationsUpcomingRow)).tap();
  }

  /// Waits for the publication [slug] in « Mes participations ».
  Future<void> waitUntilParticipationIsListed(String slug) async {
    await $(keys.profile.participationCard(slug)).waitUntilVisible();
  }

  ///
  /// Tapped on its media band, near the top: the card's centre may fall on its team line, which
  /// opens the team instead.
  Future<void> openParticipation(String slug) async {
    final card = await scrolledTo(keys.profile.participationCard(slug));
    final box = $.tester.getRect(card.first);
    await $.tester.tapAt(Offset(box.center.dx, box.top + 30));
    await $.pump(const Duration(milliseconds: 300));
  }

  String get _upcomingBadgeText =>
      $(keys.profile.participationsUpcomingCount).$(Text).text ?? '';

  // ── Deleting the account ────────────────────────────────────────────────

  /// Taps « Supprimer mon compte ». The app first asks the server what it would do to the
  /// member's teams: wait for its answer with [waitUntilDeletionIsBlocked] or
  /// [waitUntilConfirmationIsShown].
  Future<void> deleteAccount() async {
    await (await scrolledTo(keys.profile.deleteAccountButton)).tap();
  }

  /// The banner that refuses the deletion, naming the teams that block it. Waited for as present
  /// in the tree: it comes under the button, possibly below the fold.
  Future<void> waitUntilDeletionIsBlocked() async {
    await $(keys.profile.deletionBlockedBanner).waitUntilExists();
  }

  Future<void> waitUntilConfirmationIsShown() async {
    await $(keys.profile.confirmDestructiveButton).waitUntilVisible();
  }

  bool get isDeletionBlocked => isShown(keys.profile.deletionBlockedBanner);

  /// Whether the blocked banner names [teamName].
  bool deletionBlockedNames(String teamName) =>
      shows(keys.profile.deletionBlockedBanner, teamName);

  bool get isConfirmationShown =>
      isShown(keys.profile.confirmDestructiveButton);

  /// Whether the confirmation sheet's message says [text].
  bool confirmationSays(String text) =>
      shows(keys.profile.confirmDestructiveMessage, text);

  Future<void> confirm() async {
    await $(keys.profile.confirmDestructiveButton).tap();
  }
}
