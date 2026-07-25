import 'dart:async';

import 'package:flutter_riverpod/legacy.dart';

import 'paged_list_state.dart';

/// Default number of items requested per page.
const int kDefaultPageSize = 20;

/// How many items before the end of the list trigger the next page.
///
/// The design asks for the next page to start loading while about three cards
/// are still below the fold, not at the very last pixel.
const int kPrefetchDistance = 3;

/// Base class for a list that loads itself page by page.
///
/// Subclasses only implement [fetchPage]. Everything else — a single request
/// in flight at a time, discarding responses that belong to an abandoned
/// query, keeping loaded items on screen when a page fails — is handled here.
///
/// The set of results is identified by the provider family key (the filters),
/// so changing a filter builds a *new* notifier rather than merging into this
/// one. That is what keeps a filter change from mixing two result sets.
abstract class PagedListNotifier<T> extends StateNotifier<PagedListState<T>> {
  PagedListNotifier({this.pageSize = kDefaultPageSize})
    : super(PagedListState<T>()) {
    unawaited(loadFirstPage());
  }

  final int pageSize;

  bool _requestInFlight = false;

  /// Bumped whenever the list is reset, so a response from before the reset
  /// can be recognised as stale and dropped.
  int _generation = 0;

  /// Fetch [page] (0-indexed) with this notifier's filters applied.
  Future<PageResult<T>> fetchPage(int page);

  /// Stable identity of an item, used to drop duplicates when appending.
  ///
  /// The API paginates by offset, so a route created (or re-sorted) between
  /// two requests can show up on two consecutive pages. Returning a key here
  /// keeps such an item from being rendered twice. Return null to skip the
  /// check.
  Object? itemKey(T item) => null;

  /// Load page 0, replacing whatever is in the list.
  ///
  /// When [refreshing] is true the current items stay on screen while the
  /// request is in flight — the [RefreshIndicator] is the progress feedback.
  Future<void> loadFirstPage({bool refreshing = false}) async {
    final generation = ++_generation;
    _requestInFlight = true;

    state = state.copyWith(
      isLoadingInitial: !refreshing,
      isRefreshing: refreshing,
      initialError: null,
      nextError: null,
    );

    try {
      final result = await fetchPage(0);
      if (!mounted || generation != _generation) return;
      state = PagedListState<T>(
        items: result.items,
        total: result.total,
        loadedPages: 1,
        hasMore: _hasMoreAfter(result.items.length, result),
        isLoadingInitial: false,
      );
    } catch (error) {
      if (!mounted || generation != _generation) return;
      // A failed refresh must not wipe a list the user is already reading:
      // it degrades to a footer error instead.
      final keepsItems = refreshing && state.items.isNotEmpty;
      state = state.copyWith(
        isLoadingInitial: false,
        isRefreshing: false,
        initialError: keepsItems ? null : error,
        nextError: keepsItems ? error : null,
      );
    } finally {
      if (generation == _generation) _requestInFlight = false;
    }
  }

  /// Load the page after the last one loaded.
  ///
  /// No-op while another request is in flight, once the end is reached, or
  /// while a footer error is showing — in that last case the user has to tap
  /// retry, otherwise scrolling would hammer a failing endpoint.
  Future<void> loadNextPage() async {
    final current = state;
    if (_requestInFlight ||
        !current.hasMore ||
        current.isLoadingInitial ||
        current.nextError != null) {
      return;
    }

    final generation = _generation;
    _requestInFlight = true;
    state = current.copyWith(isLoadingNext: true, nextError: null);

    try {
      final result = await fetchPage(current.loadedPages);
      if (!mounted || generation != _generation) return;
      final merged = _append(state.items, result.items);
      state = state.copyWith(
        items: merged,
        total: result.total,
        loadedPages: state.loadedPages + 1,
        hasMore: _hasMoreAfter(merged.length, result),
        isLoadingNext: false,
      );
    } catch (error) {
      if (!mounted || generation != _generation) return;
      state = state.copyWith(isLoadingNext: false, nextError: error);
    } finally {
      if (generation == _generation) _requestInFlight = false;
    }
  }

  /// Retry the page that just failed, from the footer.
  Future<void> retryNextPage() async {
    if (state.nextError == null) return;
    state = state.copyWith(nextError: null);
    await loadNextPage();
  }

  /// Pull-to-refresh: drop everything and reload from page 0.
  Future<void> refresh() => loadFirstPage(refreshing: true);

  /// Called from the item builder to start the next page early.
  ///
  /// The load is deferred to a microtask because the builder runs during
  /// `build`, where mutating provider state is not allowed.
  void onItemBuilt(int index) {
    final current = state;
    if (index < current.items.length - kPrefetchDistance) return;
    if (!current.hasMore ||
        current.isLoadingNext ||
        current.nextError != null) {
      return;
    }
    scheduleMicrotask(loadNextPage);
  }

  bool _hasMoreAfter(int loadedCount, PageResult<T> result) {
    // An empty page means the end, whatever the reported total says.
    if (result.items.isEmpty) return false;
    return loadedCount < result.total;
  }

  List<T> _append(List<T> existing, List<T> incoming) {
    final seen = <Object>{};
    for (final item in existing) {
      final key = itemKey(item);
      if (key != null) seen.add(key);
    }
    if (seen.isEmpty) return [...existing, ...incoming];

    return [
      ...existing,
      ...incoming.where((item) {
        final key = itemKey(item);
        return key == null || seen.add(key);
      }),
    ];
  }
}
