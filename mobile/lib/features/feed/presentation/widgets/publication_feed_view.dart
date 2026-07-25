import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/pagination/pagination.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../teams/presentation/widgets/publication_card.dart';
import '../../providers/publication_feed_provider.dart';

/// A publication feed: type chips, infinite scroll, pull-to-refresh and the
/// four list states.
///
/// Shared by the home feed and a team's feed so both behave identically —
/// only the slivers above the chips differ.
class PublicationFeedView extends ConsumerStatefulWidget {
  /// Team to show the feed of, or null for the cross-team home feed.
  final String? teamSlug;

  /// Slivers rendered above the filter chips (app bar, prompts…).
  final List<Widget> leadingSlivers;

  /// Message shown when the feed has nothing at all.
  final String emptyMessage;

  const PublicationFeedView({
    super.key,
    required this.teamSlug,
    required this.emptyMessage,
    this.leadingSlivers = const [],
  });

  @override
  ConsumerState<PublicationFeedView> createState() =>
      _PublicationFeedViewState();
}

class _PublicationFeedViewState extends ConsumerState<PublicationFeedView> {
  /// Scrolls back to the top through the route's primary controller.
  ///
  /// The feed deliberately declares no `controller:` of its own: that is what
  /// keeps it the primary scrollable of its route, which is what makes an iOS
  /// status-bar tap scroll it to the top.
  void _scrollToTop() {
    final controller = PrimaryScrollController.maybeOf(context);
    if (controller != null && controller.hasClients) controller.jumpTo(0);
  }

  @override
  Widget build(BuildContext context) {
    // Switching type is a new result set: back to the top of the list.
    ref.listen(publicationFeedTypeProvider(widget.teamSlug), (previous, next) {
      if (previous != next) _scrollToTop();
    });

    final type = ref.watch(publicationFeedTypeProvider(widget.teamSlug));
    final key = (teamSlug: widget.teamSlug, type: type);
    final state = ref.watch(publicationFeedProvider(key));
    final notifier = ref.read(publicationFeedProvider(key).notifier);

    return RefreshIndicator(
      onRefresh: notifier.refresh,
      child: CustomScrollView(
        // Explicitly the route's primary scrollable, with no controller of its
        // own: an iOS status-bar tap scrolls it back to the top, and pull-to-
        // refresh still works when the list is too short to scroll.
        primary: true,
        slivers: [
          ...widget.leadingSlivers,
          SliverToBoxAdapter(
            child: _PublicationTypeChips(
              selected: type,
              onSelected: (value) =>
                  ref
                          .read(
                            publicationFeedTypeProvider(
                              widget.teamSlug,
                            ).notifier,
                          )
                          .state =
                      value,
            ),
          ),
          ..._buildContentSlivers(context, state, notifier),
          const SliverPadding(padding: EdgeInsets.only(bottom: 32)),
        ],
      ),
    );
  }

  List<Widget> _buildContentSlivers(
    BuildContext context,
    PagedListState<PublicationDto> state,
    PublicationFeedNotifier notifier,
  ) {
    final theme = Theme.of(context);

    if (state.showsSkeletons) {
      return [
        SliverPadding(
          padding: const EdgeInsets.all(16),
          sliver: SliverToBoxAdapter(
            child: ContentWidthConstraint(
              child: const ShimmerCardList(itemCount: 5),
            ),
          ),
        ),
      ];
    }

    if (state.initialError != null) {
      return [
        SliverToBoxAdapter(
          child: ContentWidthConstraint(
            padding: const EdgeInsets.all(16),
            child: Card(
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Row(
                  children: [
                    Icon(Icons.error_outline, color: theme.colorScheme.error),
                    const SizedBox(width: 12),
                    Expanded(child: Text(getErrorMessage(state.initialError!))),
                    TextButton(
                      onPressed: notifier.loadFirstPage,
                      child: Text('common.retry'.tr()),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ];
    }

    if (state.isEmpty) {
      return [
        SliverFillRemaining(
          hasScrollBody: false,
          child: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                AnimatedEmptyState(
                  child: Icon(
                    Icons.dynamic_feed,
                    size: 64,
                    color: theme.colorScheme.outline,
                  ),
                ),
                const SizedBox(height: 16),
                Text(widget.emptyMessage, style: theme.textTheme.titleMedium),
              ],
            ),
          ),
        ),
      ];
    }

    return [
      SliverPadding(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 0),
        sliver: SliverList.separated(
          itemCount: state.items.length,
          separatorBuilder: (context, index) => const SizedBox(height: 8),
          itemBuilder: (context, index) {
            notifier.onItemBuilt(index);
            return ContentWidthConstraint(
              child: PublicationCard(publication: state.items[index]),
            );
          },
        ),
      ),
      SliverPadding(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        sliver: SliverToBoxAdapter(
          child: ContentWidthConstraint(
            child: PagedListFooter(
              state: state,
              onRetry: notifier.retryNextPage,
              skeleton: const ShimmerCard(),
            ),
          ),
        ),
      ),
    ];
  }
}

class _PublicationTypeChips extends StatelessWidget {
  final PublicationType? selected;
  final ValueChanged<PublicationType?> onSelected;

  const _PublicationTypeChips({
    required this.selected,
    required this.onSelected,
  });

  @override
  Widget build(BuildContext context) {
    return ContentWidthConstraint(
      padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
      child: SingleChildScrollView(
        scrollDirection: Axis.horizontal,
        child: Row(
          children: [
            _chip(label: 'home.feed.all'.tr(), value: null),
            const SizedBox(width: 8),
            _chip(
              label: 'rides.title'.tr(),
              icon: Icons.directions_bike,
              value: PublicationType.ride,
            ),
            const SizedBox(width: 8),
            _chip(
              label: 'posts.title'.tr(),
              icon: Icons.article,
              value: PublicationType.post,
            ),
            const SizedBox(width: 8),
            _chip(
              label: 'trips.title'.tr(),
              icon: Icons.hiking,
              value: PublicationType.trip,
            ),
          ],
        ),
      ),
    );
  }

  Widget _chip({
    required String label,
    required PublicationType? value,
    IconData? icon,
  }) {
    return FilterChip(
      label: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          if (icon != null) ...[Icon(icon, size: 16), const SizedBox(width: 4)],
          Text(label),
        ],
      ),
      selected: selected == value,
      onSelected: (_) => onSelected(value),
    );
  }
}
