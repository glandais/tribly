import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:patrol/patrol.dart';

import 'module.dart';

/// The profile tab: its overview — one shortcut per sub-page, each with its status line — and the
/// way into and out of the sub-pages. What happens inside a sub-page is in [ProfileSettings].
final class Profile extends Module {
  Profile(super.$);

  // ── Sub-pages ───────────────────────────────────────────────────────────

  /// The identity card → « Mon compte » (photo, display name, account deletion).
  Future<void> openAccount() =>
      _open(keys.profile.identityCard, keys.profile.displayNameField);

  /// « Préférences »: units, time zone, theme, language.
  Future<void> openPreferences() =>
      _open(keys.profile.preferencesRow, keys.profile.languageRow);

  /// « Notifications »: one row per type, the team switches, the inbox link.
  Future<void> openNotificationSettings() =>
      _open(keys.profile.notificationsRow, keys.notifications.openInboxRow);

  /// « Appareils et services »: the connected GPS services and the paired devices.
  Future<void> openDevices() async {
    await (await scrolledTo(keys.profile.devicesRow)).tap();
    await $.pump(const Duration(milliseconds: 500));
  }

  /// « Connexion et sécurité »: passkeys, « Déconnecter tous les appareils ».
  Future<void> openSecurity() =>
      _open(keys.profile.securityRow, keys.profile.logoutAllButton);

  /// « Confidentialité »: the contact switch, blocked users, error reports, data export.
  Future<void> openPrivacy() =>
      _open(keys.profile.privacyRow, keys.profile.contactableSwitch);

  /// « Aide et à propos »: the companion apps, « Signaler un problème », legal pages, versions.
  Future<void> openHelp() => _open(keys.profile.helpRow, keys.profile.appsRow);

  /// The sub-page's back arrow, back to the overview.
  Future<void> backToOverview() async {
    await $(keys.profile.backButton).tap();
    await waitUntilShown();
  }

  Future<void> _open(Key row, Key shownOnPage) async {
    await (await scrolledTo(row)).tap();
    await $(shownOnPage).waitUntilExists();
  }

  Future<void> logOut() async {
    await $(keys.profile.logoutButton).scrollTo().tap();
  }

  Future<void> waitUntilShown() async {
    await $(keys.profile.participationsUpcomingRow).waitUntilVisible();
  }

  // ── Participations ──────────────────────────────────────────────────────

  /// The badge of « Mes sorties » (the upcoming ones), once the profile summary has loaded.
  Future<int> upcomingCount() async {
    await $(keys.profile.participationsUpcomingCount).waitUntilExists();
    return int.parse(_upcomingBadgeText);
  }

  /// Waits until the badge of « Mes sorties » reads [expected].
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

  /// Waits for the publication [slug] in « Mes sorties ».
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

  /// Taps « Supprimer le compte », in « Mon compte » ([openAccount]). The app first asks the server what it would do to the
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
