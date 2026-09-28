import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:patrol/patrol.dart';
import 'package:pedalons/core/adaptive/navigation_destination.dart';
import 'package:pedalons/features/home/presentation/widgets/upcoming_carousel.dart';

import 'module.dart';

/// The home tab: « Ma prochaine sortie » and the « À venir » carousel.
final class Home extends Module {
  Home(super.$);

  /// Taps the Home tab. From another tab it restores the home where it was left; from the home
  /// tab itself it pops back to its root.
  Future<void> goToHome() async {
    await $(
      keys.navigation.tab(
        kAppDestinations.firstWhere((d) => d.label == 'nav.home'),
      ),
    ).tap();
  }

  /// Waits until the home tab is on screen and can be touched: after a link that pops a page
  /// pushed above the tab shell, the page is still sliding away for a moment.
  Future<void> waitUntilShown() async {
    await $(
      keys.navigation.tab(
        kAppDestinations.firstWhere((d) => d.label == 'nav.home'),
      ),
    ).waitUntilVisible();
  }

  // ── Ma prochaine sortie ─────────────────────────────────────────────────

  /// Waits until « Ma prochaine sortie » names the ride of [rideSlug].
  Future<void> waitUntilNextRideIs(
    String rideSlug, {
    Duration timeout = const Duration(seconds: 20),
  }) async {
    await $(keys.home.nextRideCard(rideSlug)).waitUntilExists(timeout: timeout);
  }

  /// Waits until the block is replaced by the compact « no upcoming ride » card.
  Future<void> waitUntilNoNextRide({
    Duration timeout = const Duration(seconds: 15),
  }) async {
    await $(keys.home.noNextRideCard).waitUntilExists(timeout: timeout);
  }

  bool showsNextRide(String rideSlug) =>
      isShown(keys.home.nextRideCard(rideSlug));

  /// Whether [text] appears on the next-ride card of [rideSlug].
  bool nextRideShows(String rideSlug, String text) =>
      shows(keys.home.nextRideCard(rideSlug), text);

  bool get nextRideOffersLeave => isShown(keys.home.nextRideLeaveButton);

  /// Taps « Se désinscrire » on the next-ride card.
  Future<void> leaveNextRide() async {
    await (await scrolledTo(keys.home.nextRideLeaveButton)).tap();
  }

  // ── À venir ─────────────────────────────────────────────────────────────

  /// Waits until the carousel holds the card of [slug] — built, not necessarily on screen: the
  /// carousel is a lazy horizontal list, the first cards are the ones it builds.
  Future<void> waitUntilUpcomingHas(
    String slug, {
    Duration timeout = const Duration(seconds: 20),
  }) async {
    await $(keys.home.upcomingCard(slug)).waitUntilExists(timeout: timeout);
  }

  /// Scrolls the carousel back to its first card. The home keeps the carousel where it was left,
  /// and the lazy list only builds the cards near what is on screen.
  Future<void> rewindUpcoming() async {
    await _showCarousel();
    $.tester.state<ScrollableState>(_carousel).position.jumpTo(0);
    await $.pump(const Duration(milliseconds: 300));
  }

  bool upcomingHas(String slug) => isShown(keys.home.upcomingCard(slug));

  /// Whether the cards of [slugs] are all in the carousel, in this order from left to right.
  ///
  /// The carousel is a lazy list: only the first two cards are built on this screen width. So it
  /// is scrolled to each card in turn, and the cards compared by their offset in the whole row
  /// (on-screen offset plus how far the row is scrolled). It is scrolled back to its start
  /// afterwards.
  Future<bool> upcomingInOrder(List<String> slugs) async {
    await _showCarousel();
    final carousel = _carousel;
    final position = $.tester.state<ScrollableState>(carousel).position;
    try {
      double? previous;
      for (final slug in slugs) {
        final card = $(keys.home.upcomingCard(slug));
        try {
          await card.scrollTo(view: carousel);
        } on WaitUntilVisibleTimeoutException {
          return false;
        }
        final left = $.tester.getTopLeft(card.first).dx + position.pixels;
        if (previous != null && left <= previous) return false;
        previous = left;
      }
      return true;
    } finally {
      await rewindUpcoming();
    }
  }

  /// Taps « Rejoindre » on the carousel card of [slug]: it opens the ride, which registers.
  Future<void> joinFromUpcoming(String slug) async {
    await (await _inCarousel(slug, keys.home.upcomingJoinButton(slug))).tap();
  }

  bool upcomingOffersJoin(String slug) =>
      isShown(keys.home.upcomingJoinButton(slug));

  /// Taps « Choisir un groupe » on the carousel card of [slug]: it opens the ride.
  Future<void> chooseGroupFromUpcoming(String slug) async {
    await (await _inCarousel(
      slug,
      keys.home.upcomingChooseGroupButton(slug),
    )).tap();
  }

  bool upcomingOffersChooseGroup(String slug) =>
      isShown(keys.home.upcomingChooseGroupButton(slug));

  /// Waits until the carousel card of [slug] carries the « Inscrit » badge — the row is refetched
  /// after a registration, so the badge comes with that refetch.
  Future<void> waitUntilUpcomingShowsRegistered(
    String slug, {
    Duration timeout = const Duration(seconds: 15),
  }) async {
    await $(
      keys.home.upcomingRegisteredBadge(slug),
    ).waitUntilExists(timeout: timeout);
  }

  bool upcomingShowsRegistered(String slug) =>
      isShown(keys.home.upcomingRegisteredBadge(slug));

  /// The widget of [key], on the carousel card of [slug], scrolled into view in the carousel —
  /// a card past the first only peeks past the edge of the screen.
  Future<PatrolFinder> _inCarousel(String slug, Key key) async {
    await _showCarousel();
    return $(key).scrollTo(view: _carousel);
  }

  /// Scrolls the home so the whole carousel is on screen: « Ma prochaine sortie », once there is
  /// one, pushes it down, and a drag must start from a point of the carousel that can be touched.
  Future<void> _showCarousel() async {
    await $.tester.ensureVisible(find.byType(UpcomingCarousel));
    await $.pump(const Duration(milliseconds: 300));
  }

  /// The carousel's horizontal list. Found from the carousel itself rather than from a card, so
  /// it still resolves once the card it started from has scrolled out of the lazy list.
  Finder get _carousel => find
      .descendant(
        of: find.byType(UpcomingCarousel),
        matching: find.byWidgetPredicate(
          (Widget widget) =>
              widget is Scrollable &&
              axisDirectionToAxis(widget.axisDirection) == Axis.horizontal,
        ),
      )
      .first;
}
