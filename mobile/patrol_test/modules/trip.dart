import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';

import 'module.dart';

/// A trip's page (screen 24) and its stages (screen 25).
final class Trip extends Module {
  Trip(super.$);

  Future<void> waitUntilShown() async {
    await $(keys.trip.title).waitUntilVisible();
  }

  /// Whether the trip is marked « Terminé ».
  bool get saysFinished => isShown(keys.trip.finishedBadge);

  /// Whether the « Voyage annulé » banner is shown.
  bool get saysCancelled => isShown(keys.trip.cancelledBanner);

  bool get offersJoin => isShown(keys.trip.joinButton);

  bool get offersLeave => isShown(keys.trip.leaveButton);

  Future<void> join() async {
    await $(keys.trip.joinButton).tap();
    await $(keys.trip.leaveButton).waitUntilVisible();
  }

  Future<void> leave() async {
    await $(keys.trip.leaveButton).tap();
    await $(keys.trip.joinButton).waitUntilVisible();
  }

  /// Whether the trip lists a card for the stage of [stageSlug].
  Future<bool> listsStage(String stageSlug) async {
    await scrolledTo(keys.trip.stageCard(stageSlug));
    return isShown(keys.trip.stageCard(stageSlug));
  }

  /// Whether the participants section names [name]. The section only exists once someone takes
  /// part, and fills from the refetch that follows a registration.
  Future<bool> participantsName(String name) async {
    await $.pumpAndTrySettle();
    await scrolledIntoView(keys.trip.participants);
    return shows(keys.trip.participants, name);
  }

  // ── Stages ──────────────────────────────────────────────────────────────

  /// Taps the card of the stage of [stageSlug], and waits for its page, titled [stageName].
  Future<void> openStage(String stageSlug, String stageName) async {
    await (await scrolledTo(keys.trip.stageCard(stageSlug))).tap();
    await waitUntilStageIs(stageName);
  }

  /// Taps the rail's pill at [index] — 0 is « Aperçu », then one per stage in order.
  ///
  /// The pills carry no key of the feature: `PdlStageRail` gives each a `ValueKey<int>` of its
  /// rank, found inside the rail's own key.
  ///
  /// The rail scrolls sideways to centre the selected pill, so the one asked for may be off
  /// screen: it is scrolled to within the rail first.
  Future<void> tapRail(int index) async {
    final pill = $(keys.trip.stageRail).$(ValueKey<int>(index));
    await pill.waitUntilExists();
    // The rail is a plain `Row` in a scroller: every pill is built, `ensureVisible` scrolls to it
    // whichever side it lies.
    await $.tester.ensureVisible(pill.first);
    await $.pump(const Duration(milliseconds: 400));
    await pill.tap();
  }

  /// Waits until the stage page shown is the one named [stageName] — the rail replaces the page,
  /// so the previous one may still be in the tree for a transition.
  Future<void> waitUntilStageIs(
    String stageName, {
    Duration timeout = const Duration(seconds: 15),
  }) async {
    final finder = find.byWidgetPredicate(
      (Widget widget) =>
          widget is Text &&
          widget.key == keys.trip.stageTitle &&
          widget.data == stageName,
    );
    await $(finder).waitUntilVisible(timeout: timeout);
    await $.pumpAndTrySettle();
  }

  /// Whether the stage page's badge reads « Étape [index] sur [total] » — its two numbers, in
  /// that order, whatever the wording around them.
  bool stagePositionIs(int index, int total) {
    if (!$(keys.trip.stagePosition).exists) return false;
    final label = $(keys.trip.stagePosition).$(Text).text ?? '';
    return RegExp('\\b$index\\b.*\\b$total\\b').hasMatch(label);
  }

  /// Pulls the trip's page down to refresh it: its detail is read again from the API.
  ///
  /// The indicator only arms for a drag that starts at the top of the page, so the page is brought
  /// back there first — which may already refresh it, harmlessly.
  Future<void> pullToRefresh() async {
    final title = find.byKey(keys.trip.title);
    await $(keys.trip.title).waitUntilVisible();
    await $.tester.drag(title, const Offset(0, 600));
    await $.pump(const Duration(milliseconds: 800));
    await $.tester.fling(title, const Offset(0, 400), 1000);
    await $.pump(const Duration(seconds: 2));
  }

  /// Waits for the « Voyage annulé » banner.
  Future<void> waitUntilCancelled() async {
    await $(keys.trip.cancelledBanner).waitUntilExists();
  }
}
