import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/widgets/widgets.dart';
import '../widgets/publication_card.dart';
import '../widgets/team_sliver_app_bar.dart';

final _teamPublicationsProvider = FutureProvider.family<
    PublicationListResponse, ({String teamSlug, int page})>((ref, params) async {
  final client = ref.watch(publicationsClientProvider);
  return client.listPublications(
    teamSlug: params.teamSlug,
    page: params.page,
    size: 20,
  );
});

class TeamFeedPage extends ConsumerStatefulWidget {
  final String teamSlug;
  final TeamDetailDto team;

  const TeamFeedPage({
    super.key,
    required this.teamSlug,
    required this.team,
  });

  @override
  ConsumerState<TeamFeedPage> createState() => _TeamFeedPageState();
}

class _TeamFeedPageState extends ConsumerState<TeamFeedPage> {
  @override
  Widget build(BuildContext context) {
    final params = (teamSlug: widget.teamSlug, page: 0);
    final publicationsAsync = ref.watch(_teamPublicationsProvider(params));

    return RefreshIndicator(
      onRefresh: () async {
        ref.invalidate(_teamPublicationsProvider(params));
      },
      child: CustomScrollView(
        slivers: [
          TeamSliverAppBar(team: widget.team),
          publicationsAsync.when(
            data: (response) {
              final publications = response.publications;
              if (publications.isEmpty) {
                return SliverFillRemaining(
                  child: Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        AnimatedEmptyState(
                          child: Icon(
                            Icons.dynamic_feed,
                            size: 64,
                            color: Theme.of(context).colorScheme.outline,
                          ),
                        ),
                        const SizedBox(height: 16),
                        Text(
                          'teams.feed.empty'.tr(),
                          style: Theme.of(context).textTheme.titleMedium,
                        ),
                      ],
                    ),
                  ),
                );
              }

              return SliverPadding(
                padding: const EdgeInsets.all(16),
                sliver: SliverList.separated(
                  itemCount: publications.length,
                  separatorBuilder: (context, index) =>
                      const SizedBox(height: 8),
                  itemBuilder: (context, index) {
                    return ContentWidthConstraint(
                      child: PublicationCard(publication: publications[index]),
                    );
                  },
                ),
              );
            },
            loading: () => SliverPadding(
              padding: const EdgeInsets.all(16),
              sliver: SliverToBoxAdapter(
                child: ContentWidthConstraint(
                  child: const ShimmerCardList(itemCount: 5),
                ),
              ),
            ),
            error: (error, stack) => SliverFillRemaining(
              child: Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.error_outline,
                        size: 48,
                        color: Theme.of(context).colorScheme.error),
                    const SizedBox(height: 16),
                    Text(getErrorMessage(error)),
                    const SizedBox(height: 16),
                    FilledButton(
                      onPressed: () =>
                          ref.invalidate(_teamPublicationsProvider(params)),
                      child: Text('common.retry'.tr()),
                    ),
                  ],
                ),
              ),
            ),
          ),
          const SliverPadding(padding: EdgeInsets.only(bottom: 32)),
        ],
      ),
    );
  }
}
