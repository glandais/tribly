import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_riverpod/legacy.dart';

import '../../../api/generated/export.dart';
import '../../../core/pagination/pagination.dart';
import '../data/route_repository.dart';
import '../domain/route_filters.dart';

/// Identifies one route result set: a scope (a team, or every visible team)
/// plus the filters applied to it.
typedef RouteListKey = ({String? teamSlug, RouteFilters filters});

/// Filters currently applied to a route list, keyed by scope.
///
/// Kept outside the paged notifier on purpose: changing it swaps the family
/// key below, which builds a fresh list rather than mutating the current one.
final routeFiltersProvider = StateProvider.autoDispose
    .family<RouteFilters, String?>((ref, teamSlug) => const RouteFilters());

/// The paginated route list for a scope + filters pair.
///
/// Auto-disposed, so abandoning a filter combination frees it, while the list
/// the user is actually reading stays alive as long as the page is mounted —
/// which is what preserves loaded pages and scroll position when they open a
/// route and come back.
final routeListProvider = StateNotifierProvider.autoDispose
    .family<RouteListNotifier, PagedListState<RouteDto>, RouteListKey>((
      ref,
      key,
    ) {
      return RouteListNotifier(ref.watch(routeRepositoryProvider), key);
    });

class RouteListNotifier extends PagedListNotifier<RouteDto> {
  final RouteRepository _repository;
  final RouteListKey _key;

  RouteListNotifier(this._repository, this._key);

  @override
  Future<PageResult<RouteDto>> fetchPage(int page) {
    return _repository.fetchRoutes(
      teamSlug: _key.teamSlug,
      filters: _key.filters,
      page: page,
      size: pageSize,
    );
  }

  @override
  Object? itemKey(RouteDto item) => item.id;
}

/// How many routes the given filters would return.
///
/// Debounced so dragging a slider in the filter sheet does not fire a request
/// per pixel; the auto-dispose family drops the pending call as soon as the
/// draft changes again.
final routeCountProvider = FutureProvider.autoDispose.family<int, RouteListKey>(
  (ref, key) async {
    await Future<void>.delayed(const Duration(milliseconds: 350));
    if (!ref.mounted) return 0;
    final repository = ref.watch(routeRepositoryProvider);
    return repository.countRoutes(teamSlug: key.teamSlug, filters: key.filters);
  },
);

/// A short preview of what the list would hold without the given filter.
///
/// Feeds the dead-end empty state: rather than claiming that dropping a filter
/// helps, it shows the routes that would actually come back.
final routePreviewWithoutFilterProvider = FutureProvider.autoDispose
    .family<List<RouteDto>, RouteListKey>((ref, key) async {
      final repository = ref.watch(routeRepositoryProvider);
      final page = await repository.fetchRoutes(
        teamSlug: key.teamSlug,
        filters: key.filters,
        size: 3,
      );
      return page.items;
    });
