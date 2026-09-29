import 'package:flutter/widgets.dart';
import 'package:pedalons/core/pdl/pdl.dart';

import 'module.dart';

/// « Contacter le vendeur » on an ad: the sheet, its outcomes, and what they leave on the page.
final class AdContact extends Module {
  AdContact(super.$);

  /// Scrolls to « Contacter le vendeur » and opens its sheet.
  Future<void> openSheet() async {
    await (await scrolledTo(keys.ad.contactButton)).tap();
    await $(keys.adContact.messageField).waitUntilVisible();
  }

  Future<void> typeMessage(String message) async {
    await $(keys.adContact.messageField).enterText(message);
  }

  /// Whether « Envoyer » (or « Réessayer ») would send — the button is not interactive otherwise.
  bool get sendIsEnabled =>
      ($(keys.adContact.sendButton).evaluate().single.widget as PdlButton)
          .enabled;

  bool sendButtonSays(String label) => shows(keys.adContact.sendButton, label);

  Future<void> send() async {
    await $(keys.adContact.sendButton).tap();
  }

  /// The sheet closed on its own — a sent message, or a seller who can't be contacted.
  Future<void> waitUntilSheetIsClosed() async {
    await waitUntilGone(
      keys.adContact.messageField,
      timeout: const Duration(seconds: 20),
    );
  }

  /// The sheet's own error banner — a quota spent, a relay down.
  Future<void> waitUntilErrorIsShown() async {
    await $(keys.adContact.error).waitUntilVisible();
  }

  bool errorSays(String text) => shows(keys.adContact.error, text);

  /// What the message field holds: the draft a failure must keep.
  String get draft =>
      ($(keys.adContact.messageField).$(EditableText).evaluate().first.widget
              as EditableText)
          .controller
          .text;

  Future<void> waitUntilSentIsShown() async {
    await (await scrolledTo(keys.ad.contactSent)).waitUntilVisible();
  }

  Future<void> waitUntilOptedOutIsShown() async {
    await (await scrolledTo(keys.ad.contactOptedOut)).waitUntilVisible();
  }

  bool get offersContact => isShown(keys.ad.contactButton);

  bool get showsSent => isShown(keys.ad.contactSent);
}
