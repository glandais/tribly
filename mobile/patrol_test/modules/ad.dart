import 'module.dart';

/// The ad page, and what it offers depending on who reads it.
final class Ad extends Module {
  Ad(super.$);

  Future<void> waitUntilShown() async {
    await $(keys.ad.title).waitUntilVisible();
  }

  Future<bool> offersContact() async {
    if (isShown(keys.ad.contactButton)) return true;
    // Below the description and the location: scroll before concluding.
    try {
      await scrolledTo(keys.ad.contactButton);
      return true;
    } on Object {
      return false;
    }
  }

  bool get hasMoreMenu => isShown(keys.ad.moreButton);

  /// Opens the `⋯` menu: what it offers is read with the [Moderation] module.
  Future<void> openMoreMenu() async {
    await $(keys.ad.moreButton).tap();
  }
}
