import 'package:flutter/services.dart';
import 'package:pedalons/core/adaptive/navigation_destination.dart';

import 'module.dart';

/// The calendar: the tab of all my teams, a team's own section, its agenda cards and the ICS
/// feed's subscription card.
final class Calendar extends Module {
  Calendar(super.$);

  Future<void> goToCalendar() async {
    await $(
      keys.navigation.tab(
        kAppDestinations.firstWhere((d) => d.label == 'nav.calendar'),
      ),
    ).tap();
  }

  /// Moves the calendar, which opens on the current month, to the month of [day].
  Future<void> showMonthOf(DateTime day) async {
    final now = DateTime.now();
    final months = (day.year - now.year) * 12 + day.month - now.month;
    for (var i = 0; i < months; i++) {
      await $(keys.calendar.nextMonthButton).tap();
    }
  }

  /// Waits until the agenda holds the card of [entitySlug], and brings it on screen.
  Future<void> waitUntilEventIsShown(String entitySlug) async {
    await $(
      keys.calendar.agendaCard(entitySlug),
    ).waitUntilExists(timeout: const Duration(seconds: 20));
    await scrolledTo(keys.calendar.agendaCard(entitySlug));
  }

  bool showsEvent(String entitySlug) =>
      isShown(keys.calendar.agendaCard(entitySlug));

  bool eventShows(String entitySlug, String text) =>
      shows(keys.calendar.agendaCard(entitySlug), text);

  bool showsRegistered(String entitySlug) =>
      isShown(keys.calendar.agendaRegisteredBadge(entitySlug));

  /// Whether the « Inscrit · groupe » badge reads [text] — a badge renders its label in
  /// capitals.
  bool registeredBadgeShows(String entitySlug, String text) => shows(
    keys.calendar.agendaRegisteredBadge(entitySlug),
    text.toUpperCase(),
  );

  Future<void> openEvent(String entitySlug) async {
    await (await scrolledTo(keys.calendar.agendaCard(entitySlug))).tap();
  }

  /// Taps the copy button of the subscription card, and returns what the clipboard received.
  Future<String?> copyFeedUrl() async {
    await (await scrolledTo(keys.calendar.subscriptionCopyButton)).tap();
    await $(keys.calendar.subscriptionNotice).waitUntilExists();
    return (await Clipboard.getData(Clipboard.kTextPlain))?.text;
  }

  /// « Régénérer », then the confirmation of the closed question.
  Future<void> regenerateFeed() async {
    await (await scrolledTo(keys.calendar.subscriptionRegenerateButton)).tap();
    await $(keys.calendar.subscriptionRegenerateConfirmButton).tap();
    await waitUntilGone(keys.calendar.subscriptionRegenerateConfirmButton);
  }

  bool noticeShows(String text) =>
      shows(keys.calendar.subscriptionNotice, text);
}
