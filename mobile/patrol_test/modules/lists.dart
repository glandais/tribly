import 'package:flutter/widgets.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';

import 'module.dart';

/// The paged lists — a feed (home or team), a team's ads and members, a comment thread — their
/// search, their type chips and their filtered empty state.
final class Lists extends Module {
  Lists(super.$);

  // ── Infinite scroll ─────────────────────────────────────────────────────

  /// Scrolls the screen's vertical list down until the widget of [key] is built — past the end
  /// of the first page, the next one loading on the way — then brings it on screen.
  ///
  /// Patrol's `scrollTo` gives up after 15 steps of 64 px, a fraction of a page of 20 cards.
  Future<void> scrollDownTo(
    Key key, {
    Duration timeout = const Duration(seconds: 45),
  }) async {
    final deadline = DateTime.now().add(timeout);
    while (!$(key).exists) {
      if (DateTime.now().isAfter(deadline)) {
        throw TestFailure('$key not reached after $timeout of scrolling');
      }
      final lists = _verticalScrollable.evaluate();
      if (lists.isNotEmpty) {
        await $.tester.drag(
          _verticalScrollable.first,
          const Offset(0, -500),
          warnIfMissed: false,
        );
      }
      await $.pump(const Duration(milliseconds: 400));
    }
    await $.tester.ensureVisible(find.byKey(key));
    await $.pump(const Duration(milliseconds: 300));
  }

  // ── Feeds ───────────────────────────────────────────────────────────────

  Future<void> scrollFeedTo(String slug) => scrollDownTo(keys.feed.card(slug));

  Future<void> waitUntilFeedHas(String slug) async {
    await $(
      keys.feed.card(slug),
    ).waitUntilExists(timeout: const Duration(seconds: 20));
  }

  Future<void> waitUntilFeedLacks(String slug) =>
      waitUntilGone(keys.feed.card(slug));

  bool feedHas(String slug) => isShown(keys.feed.card(slug));

  /// Taps a type chip of the feed; `null` is « Tout ».
  Future<void> selectFeedType(PublicationType? type) async {
    await _chipInView(keys.feed.typeChip(type));
    await $(keys.feed.typeChip(type)).tap();
  }

  Future<void> searchFeed(String text) async {
    await $(keys.feed.searchField).enterText(text);
  }

  Future<void> waitUntilFeedFilteredEmpty() async {
    await $(
      keys.feed.filteredEmptyState,
    ).waitUntilExists(timeout: const Duration(seconds: 15));
  }

  // ── Ads ─────────────────────────────────────────────────────────────────

  Future<void> scrollAdsTo(String adSlug) =>
      scrollDownTo(keys.adsList.card(adSlug));

  Future<void> waitUntilAdsHave(String adSlug) async {
    await $(
      keys.adsList.card(adSlug),
    ).waitUntilExists(timeout: const Duration(seconds: 20));
  }

  Future<void> waitUntilAdsLack(String adSlug) =>
      waitUntilGone(keys.adsList.card(adSlug));

  bool adsHave(String adSlug) => isShown(keys.adsList.card(adSlug));

  /// Taps an ad type chip (`AdType.json`); `null` is « Tous ».
  Future<void> selectAdType(String? adType) async {
    await _chipInView(keys.adsList.typeChip(adType));
    await $(keys.adsList.typeChip(adType)).tap();
  }

  Future<void> searchAds(String text) async {
    await $(keys.adsList.searchField).enterText(text);
  }

  Future<void> waitUntilAdsFilteredEmpty() async {
    await $(
      keys.adsList.filteredEmptyState,
    ).waitUntilExists(timeout: const Duration(seconds: 15));
  }

  // ── Members ─────────────────────────────────────────────────────────────

  Future<void> scrollMembersTo(String userId) =>
      scrollDownTo(keys.team.memberRow(userId));

  Future<void> waitUntilMembersHave(String userId) async {
    await $(
      keys.team.memberRow(userId),
    ).waitUntilExists(timeout: const Duration(seconds: 20));
  }

  bool membersHave(String userId) => isShown(keys.team.memberRow(userId));

  Future<void> waitUntilMembersLack(String userId) =>
      waitUntilGone(keys.team.memberRow(userId));

  Future<void> searchMembers(String text) async {
    await $(keys.team.membersSearchField).enterText(text);
  }

  // ── Comments ────────────────────────────────────────────────────────────

  /// Waits until the thread holds the comment of [commentId] — built, not necessarily on screen.
  Future<void> waitUntilCommentIsLoaded(String commentId) async {
    await $(
      keys.comments.comment(commentId),
    ).waitUntilExists(timeout: const Duration(seconds: 20));
  }

  Future<void> scrollCommentsTo(String commentId) =>
      scrollDownTo(keys.comments.comment(commentId));

  /// Scrolls a chip row sideways until the chip of [key] is on screen: the last chips of a row
  /// start past its right edge.
  Future<void> _chipInView(Key key) async {
    await $(key).waitUntilExists();
    await $.tester.ensureVisible(find.byKey(key));
    await $.pump(const Duration(milliseconds: 300));
  }

  static final Finder _verticalScrollable = find.byWidgetPredicate(
    (Widget widget) =>
        widget is Scrollable &&
        axisDirectionToAxis(widget.axisDirection) == Axis.vertical,
  );
}
