import 'package:flutter/foundation.dart';

/// One page of results, as returned by a paginated endpoint.
@immutable
class PageResult<T> {
  /// Items of this page only.
  final List<T> items;

  /// Total number of items matching the query, across all pages.
  final int total;

  const PageResult({required this.items, required this.total});
}

const Object _unset = Object();

/// State of a list that loads itself page by page while the user scrolls.
///
/// The list has exactly four states, and they are mutually exclusive on
/// purpose — see [PagedListFooter] for how they are rendered:
///
/// - initial load: [isLoadingInitial] (skeletons replace the whole list)
/// - next page: [isLoadingNext] (spinner in the footer, list stays put)
/// - end of list: [hasMore] is false
/// - error: [initialError] takes over the list, [nextError] stays in the
///   footer so already-loaded items never disappear.
@immutable
class PagedListState<T> {
  final List<T> items;

  /// Total matching items reported by the server, null until the first page
  /// lands.
  final int? total;

  /// How many pages have been loaded so far — also the index of the next one.
  final int loadedPages;

  final bool hasMore;
  final bool isLoadingInitial;
  final bool isLoadingNext;
  final bool isRefreshing;

  /// Failure of the first page: the list has nothing to show.
  final Object? initialError;

  /// Failure of a subsequent page: shown in the footer only.
  final Object? nextError;

  const PagedListState({
    this.items = const [],
    this.total,
    this.loadedPages = 0,
    this.hasMore = true,
    this.isLoadingInitial = true,
    this.isLoadingNext = false,
    this.isRefreshing = false,
    this.initialError,
    this.nextError,
  });

  /// True once the first page landed and came back empty.
  bool get isEmpty =>
      items.isEmpty && !isLoadingInitial && initialError == null;

  /// True while the very first page is in flight and nothing is on screen yet.
  bool get showsSkeletons => isLoadingInitial && items.isEmpty;

  PagedListState<T> copyWith({
    List<T>? items,
    Object? total = _unset,
    int? loadedPages,
    bool? hasMore,
    bool? isLoadingInitial,
    bool? isLoadingNext,
    bool? isRefreshing,
    Object? initialError = _unset,
    Object? nextError = _unset,
  }) {
    return PagedListState<T>(
      items: items ?? this.items,
      total: identical(total, _unset) ? this.total : total as int?,
      loadedPages: loadedPages ?? this.loadedPages,
      hasMore: hasMore ?? this.hasMore,
      isLoadingInitial: isLoadingInitial ?? this.isLoadingInitial,
      isLoadingNext: isLoadingNext ?? this.isLoadingNext,
      isRefreshing: isRefreshing ?? this.isRefreshing,
      initialError: identical(initialError, _unset)
          ? this.initialError
          : initialError,
      nextError: identical(nextError, _unset) ? this.nextError : nextError,
    );
  }
}
