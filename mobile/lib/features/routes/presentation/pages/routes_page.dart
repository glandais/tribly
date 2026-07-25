import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../config/paths.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/pagination/pagination.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/widgets/authenticated_image.dart';
import '../../../../core/widgets/widgets.dart';
import '../../domain/route_filters.dart';
import '../../providers/route_list_provider.dart';
import '../widgets/route_filter_chips_bar.dart';
import '../widgets/route_filter_labels.dart';
import '../widgets/route_filter_sheet.dart';
import '../widgets/route_search_bar.dart';

class RoutesPage extends ConsumerStatefulWidget {
  /// Team to list routes for, or null for every team the user can see.
  final String? teamSlug;
  final bool embedded;

  const RoutesPage({super.key, required this.teamSlug, this.embedded = false});

  @override
  ConsumerState<RoutesPage> createState() => _RoutesPageState();
}

class _RoutesPageState extends ConsumerState<RoutesPage> {
  /// Scrolls back to the top through the route's primary controller.
  ///
  /// The list deliberately declares no `controller:` of its own: that is what
  /// keeps it the primary scrollable of its route, which is what makes an iOS
  /// status-bar tap scroll it to the top.
  void _scrollToTop() {
    final controller = PrimaryScrollController.maybeOf(context);
    if (controller != null && controller.hasClients) controller.jumpTo(0);
  }

  void _setFilters(RouteFilters next) {
    ref.read(routeFiltersProvider(widget.teamSlug).notifier).state = next;
  }

  @override
  Widget build(BuildContext context) {
    // Any filter change is a new result set, so the list restarts at the top
    // instead of leaving the user mid-scroll in results they never saw.
    ref.listen(routeFiltersProvider(widget.teamSlug), (previous, next) {
      if (previous != next) _scrollToTop();
    });

    final filters = ref.watch(routeFiltersProvider(widget.teamSlug));
    final key = (teamSlug: widget.teamSlug, filters: filters);
    final state = ref.watch(routeListProvider(key));
    final notifier = ref.read(routeListProvider(key).notifier);

    final body = RefreshIndicator(
      onRefresh: notifier.refresh,
      child: CustomScrollView(
        // Explicitly the route's primary scrollable, with no controller of its
        // own: an iOS status-bar tap scrolls it back to the top, and pull-to-
        // refresh still works when the list is too short to scroll.
        primary: true,
        slivers: [
          PinnedHeaderSliver(
            child: _FilterHeader(
              filters: filters,
              onChanged: _setFilters,
              onOpenFilters: _openFilterSheet,
            ),
          ),
          ..._buildContentSlivers(context, state, notifier, filters),
          const SliverPadding(padding: EdgeInsets.only(bottom: 32)),
        ],
      ),
    );

    if (widget.embedded) return body;

    return Scaffold(
      appBar: AppBar(title: Text('routes.title'.tr())),
      body: body,
    );
  }

  List<Widget> _buildContentSlivers(
    BuildContext context,
    PagedListState<RouteDto> state,
    RouteListNotifier notifier,
    RouteFilters filters,
  ) {
    const padding = EdgeInsets.fromLTRB(16, 8, 16, 0);

    if (state.showsSkeletons) {
      return [
        SliverPadding(
          padding: padding,
          sliver: const ResponsiveSliverGrid(
            itemCount: 4,
            childAspectRatio: _routeCardAspectRatio,
            itemBuilder: _shimmerBuilder,
          ),
        ),
      ];
    }

    if (state.initialError != null) {
      return [
        SliverFillRemaining(
          hasScrollBody: false,
          child: _ListErrorState(
            error: state.initialError!,
            onRetry: notifier.loadFirstPage,
          ),
        ),
      ];
    }

    if (state.isEmpty) {
      return [
        SliverToBoxAdapter(
          child: RoutesEmptyState(
            teamSlug: widget.teamSlug,
            filters: filters,
            onChanged: _setFilters,
          ),
        ),
      ];
    }

    return [
      SliverToBoxAdapter(
        child: Padding(
          padding: const EdgeInsets.fromLTRB(16, 8, 16, 4),
          child: Text(
            'routes.list.count'.plural(state.total ?? state.items.length),
            style: Theme.of(context).textTheme.labelMedium?.copyWith(
              color: Theme.of(context).colorScheme.outline,
            ),
          ),
        ),
      ),
      SliverPadding(
        padding: padding,
        sliver: ResponsiveSliverGrid(
          itemCount: state.items.length,
          childAspectRatio: _routeCardAspectRatio,
          itemBuilder: (context, index) {
            notifier.onItemBuilt(index);
            return _RouteGridItem(route: state.items[index]);
          },
        ),
      ),
      SliverPadding(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        sliver: SliverToBoxAdapter(
          child: PagedListFooter(
            state: state,
            onRetry: notifier.retryNextPage,
            skeleton: const SizedBox(
              height: 190,
              child: ShimmerRouteGridItem(),
            ),
          ),
        ),
      ),
    ];
  }

  Future<void> _openFilterSheet() async {
    final current = ref.read(routeFiltersProvider(widget.teamSlug));
    final result = await showRouteFilterSheet(
      context,
      teamSlug: widget.teamSlug,
      filters: current,
    );
    if (result != null) _setFilters(result);
  }
}

/// [ShimmerRouteGridItem] lays its content out with [Expanded], so it needs a
/// bounded height. [ResponsiveSliverGrid] gives its children one in grid mode
/// but not in the single-column list mode it falls back to on a phone, hence
/// the [AspectRatio] — which collapses to the tight box when there is one.
Widget _shimmerBuilder(BuildContext context, int index) => const AspectRatio(
  aspectRatio: _routeCardAspectRatio,
  child: ShimmerRouteGridItem(),
);

const double _routeCardAspectRatio = 1.2;

/// Search bar and filter chips, pinned so the controls stay reachable while
/// the list scrolls under them.
///
/// Sized by its own content rather than a hardcoded extent: the search field
/// and the chips grow with the user's text scale, and a persistent header
/// delegate promising a fixed height would break as soon as they did.
class _FilterHeader extends StatelessWidget {
  final RouteFilters filters;
  final ValueChanged<RouteFilters> onChanged;
  final VoidCallback onOpenFilters;

  const _FilterHeader({
    required this.filters,
    required this.onChanged,
    required this.onOpenFilters,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    return Material(
      color: theme.colorScheme.surface,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 10),
            child: RouteSearchBar(
              search: filters.search,
              activeFilterCount: filters.activeCount,
              onSearchChanged: (value) =>
                  onChanged(filters.copyWith(search: value)),
              onOpenFilters: onOpenFilters,
            ),
          ),
          RouteFilterChipsBar(
            filters: filters,
            onChanged: onChanged,
            onOpenFilters: onOpenFilters,
          ),
          Divider(height: 1, color: theme.dividerColor),
        ],
      ),
    );
  }
}

/// The first page failed: there is nothing to show but the error.
class _ListErrorState extends StatelessWidget {
  final Object error;
  final VoidCallback onRetry;

  const _ListErrorState({required this.error, required this.onRetry});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.error_outline,
              size: 48,
              color: Theme.of(context).colorScheme.error,
            ),
            const SizedBox(height: 16),
            Text(getErrorMessage(error), textAlign: TextAlign.center),
            const SizedBox(height: 16),
            FilledButton(onPressed: onRetry, child: Text('common.retry'.tr())),
          ],
        ),
      ),
    );
  }
}

/// Dead end: the filters match nothing.
///
/// Rather than a bare "no results", this names the filter most likely to
/// blame, offers to drop it, and proves the offer is worth taking by
/// previewing what would come back without it.
class RoutesEmptyState extends ConsumerWidget {
  final String? teamSlug;
  final RouteFilters filters;
  final ValueChanged<RouteFilters> onChanged;

  const RoutesEmptyState({
    super.key,
    required this.teamSlug,
    required this.filters,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final culprit = filters.narrowestField;
    final search = filters.search?.trim();

    return Padding(
      padding: const EdgeInsets.fromLTRB(24, 40, 24, 16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          AnimatedEmptyState(
            child: Icon(
              Icons.search_off,
              size: 56,
              color: theme.colorScheme.outline,
            ),
          ),
          const SizedBox(height: 20),
          Text(
            'routes.list.empty.title'.tr(),
            textAlign: TextAlign.center,
            style: theme.textTheme.titleMedium,
          ),
          const SizedBox(height: 8),
          Text(
            search == null || search.isEmpty
                ? 'routes.list.empty.description'.tr()
                : 'routes.list.empty.descriptionSearch'.tr(
                    namedArgs: {'search': search},
                  ),
            textAlign: TextAlign.center,
            style: theme.textTheme.bodyMedium?.copyWith(
              color: theme.colorScheme.outline,
            ),
          ),
          const SizedBox(height: 24),
          if (culprit != null)
            FilledButton(
              onPressed: () => onChanged(filters.without(culprit)),
              style: FilledButton.styleFrom(
                minimumSize: const Size.fromHeight(48),
              ),
              child: Text(
                'routes.list.empty.removeFilter'.tr(
                  namedArgs: {'filter': RouteFilterLabels.field(culprit)},
                ),
              ),
            ),
          if (culprit != null) const SizedBox(height: 10),
          OutlinedButton(
            onPressed: () => onChanged(filters.cleared),
            style: OutlinedButton.styleFrom(
              minimumSize: const Size.fromHeight(48),
            ),
            child: Text('routes.list.empty.resetAll'.tr()),
          ),
          if (culprit != null)
            _WithoutFilterPreview(
              teamSlug: teamSlug,
              filters: filters,
              field: culprit,
            ),
        ],
      ),
    );
  }
}

/// A few of the routes that dropping [field] would bring back.
class _WithoutFilterPreview extends ConsumerWidget {
  final String? teamSlug;
  final RouteFilters filters;
  final RouteFilterField field;

  const _WithoutFilterPreview({
    required this.teamSlug,
    required this.filters,
    required this.field,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);
    final preview = ref.watch(
      routePreviewWithoutFilterProvider((
        teamSlug: teamSlug,
        filters: filters.without(field),
      )),
    );
    final routes = preview.value;
    if (routes == null || routes.isEmpty) return const SizedBox.shrink();

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        const SizedBox(height: 28),
        Text(
          'routes.list.empty.withoutFilter'.tr(
            namedArgs: {'filter': RouteFilterLabels.field(field)},
          ),
          style: theme.textTheme.labelMedium?.copyWith(
            color: theme.colorScheme.outline,
          ),
        ),
        const SizedBox(height: 8),
        for (final route in routes)
          Padding(
            padding: const EdgeInsets.only(bottom: 8),
            child: _RoutePreviewTile(route: route),
          ),
      ],
    );
  }
}

class _RoutePreviewTile extends StatelessWidget {
  final RouteDto route;

  const _RoutePreviewTile({required this.route});

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: EdgeInsets.zero,
      child: ListTile(
        leading: const Icon(Icons.route),
        title: Text(
          route.name,
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
          style: Theme.of(
            context,
          ).textTheme.titleSmall?.copyWith(fontWeight: FontWeight.bold),
        ),
        subtitle: Text(
          '${RouteFilterLabels.preciseDistance(route.distance)} · '
          '${RouteFilterLabels.elevation(route.elevationGain)}',
        ),
        trailing: const Icon(Icons.chevron_right, size: 20),
        onTap: () => context.push(Paths.route(route.team.slug, route.slug)),
      ),
    );
  }
}

/// Route grid item - works both for list and grid views.
class _RouteGridItem extends ConsumerWidget {
  final RouteDto route;

  const _RouteGridItem({required this.route});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final token = ref.watch(accessTokenHolderProvider);

    final thumbnail = Theme.of(context).brightness == Brightness.dark
        ? route.media.assets.thumbnailDark
        : route.media.assets.thumbnailLight;

    return AnimatedCard(
      onTap: () => context.push(Paths.route(route.team.slug, route.slug)),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(12),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisSize: MainAxisSize.min,
          children: [
            // Thumbnail with Hero animation
            AspectRatio(
              aspectRatio: 16 / 9,
              child: Hero(
                tag: 'route-thumbnail-${route.slug}',
                child: Container(
                  width: double.infinity,
                  decoration: BoxDecoration(
                    color: Theme.of(context).colorScheme.primaryContainer,
                    image: thumbnail?.url != null
                        ? DecorationImage(
                            image: AuthenticatedDecorationImage.fromUrl(
                              thumbnail!.url,
                              token,
                            )!,
                            fit: BoxFit.cover,
                          )
                        : null,
                  ),
                  child: thumbnail?.url == null
                      ? Center(
                          child: Icon(
                            Icons.route,
                            size: 48,
                            color: Theme.of(
                              context,
                            ).colorScheme.onPrimaryContainer,
                          ),
                        )
                      : null,
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(12),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    route.name,
                    style: Theme.of(context).textTheme.titleSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 4),
                  Wrap(
                    spacing: 8,
                    children: [
                      _StatChip(
                        icon: Icons.straighten,
                        value: RouteFilterLabels.preciseDistance(
                          route.distance,
                        ),
                      ),
                      _StatChip(
                        icon: Icons.trending_up,
                        value: RouteFilterLabels.elevation(route.elevationGain),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _StatChip extends StatelessWidget {
  final IconData icon;
  final String value;

  const _StatChip({required this.icon, required this.value});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(icon, size: 16, color: Theme.of(context).colorScheme.outline),
        const SizedBox(width: 4),
        Text(value, style: Theme.of(context).textTheme.bodySmall),
      ],
    );
  }
}
