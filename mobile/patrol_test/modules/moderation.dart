import 'package:pedalons/api/generated/export.dart';

import 'module.dart';

/// The `⋯` menu of a content, the report sheet, blocking, and the blocked users page.
final class Moderation extends Module {
  Moderation(super.$);

  /// Waits for the open `⋯` menu, whatever its first row is.
  Future<void> waitUntilMenuIsShown() async {
    await waitUntilAnyIsShown([
      keys.moderation.reportAction,
      keys.moderation.blockAction,
      keys.moderation.deleteAction,
    ]);
  }

  bool get menuOffersReport => isShown(keys.moderation.reportAction);

  bool get menuOffersBlock => isShown(keys.moderation.blockAction);

  bool get menuOffersDelete => isShown(keys.moderation.deleteAction);

  /// From the open menu: « Signaler », a reason, « Envoyer ».
  Future<void> report({ReportReason reason = ReportReason.spam}) async {
    await $(keys.moderation.reportAction).tap();
    await $(keys.moderation.reason(reason)).tap();
    await $(keys.moderation.sendReportButton).tap();
    await waitUntilGone(keys.moderation.sendReportButton);
  }

  /// From the open menu: « Bloquer {nom} », then the confirmation.
  Future<void> blockAuthor() async {
    await $(keys.moderation.blockAction).tap();
    await $(keys.profile.confirmDestructiveButton).tap();
    await waitUntilGone(keys.profile.confirmDestructiveButton);
  }

  /// Profile → « Utilisateurs bloqués » → « Débloquer » on [userId]'s row.
  Future<void> unblockFromProfile(String userId) async {
    await (await scrolledTo(keys.moderation.blockedUsersRow)).tap();
    await $(keys.moderation.unblockButton(userId)).tap();
    await waitUntilGone(keys.moderation.unblockButton(userId));
  }

  /// Leaves the current pushed page (the blocked users list, a detail page).
  Future<void> goBack() async {
    await $.tester.pageBack();
    await $.pump(const Duration(milliseconds: 500));
  }
}
