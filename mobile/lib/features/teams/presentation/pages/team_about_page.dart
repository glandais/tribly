import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/widgets/widgets.dart';
import '../widgets/team_sliver_app_bar.dart';

/// Provider for a single team page's full content
final _teamPageProvider =
    FutureProvider.family<TeamPageDto, ({String teamSlug, String pageSlug})>(
  (ref, params) async {
    final client = ref.watch(teamPagesClientProvider);
    return client.getPage(teamSlug: params.teamSlug, pageSlug: params.pageSlug);
  },
);

class TeamAboutPage extends ConsumerWidget {
  final String teamSlug;
  final TeamDetailDto team;

  const TeamAboutPage({
    super.key,
    required this.teamSlug,
    required this.team,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final visiblePages = team.pages
            ?.where((p) => !p.deleted)
            .toList()
          ?..sort((a, b) => a.order.compareTo(b.order));

    return CustomScrollView(
      slivers: [
        TeamSliverAppBar(team: team),

        // Stats row
        SliverToBoxAdapter(
          child: ContentWidthConstraint(
            padding: const EdgeInsets.all(16),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _StatItem(
                  icon: Icons.people,
                  value: '${team.memberCount}',
                  label: 'teams.membersLabel'.tr(),
                ),
                _StatItem(
                  icon: Icons.calendar_today,
                  value: team.createdAt.substring(0, 4),
                  label: 'teams.aboutPage.createdYear'.tr(),
                ),
              ],
            ),
          ),
        ),

        // About section
        if (team.about.markdown.isNotEmpty)
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'teams.about'.tr(),
                        style: Theme.of(context).textTheme.titleMedium,
                      ),
                      const SizedBox(height: 8),
                      MarkdownContent(
                        data: team.about.markdown,
                        images: team.about.assets.images,
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),

        // Team pages — each displayed inline with its content
        if (visiblePages != null && visiblePages.isNotEmpty)
          ...visiblePages.map((pageSummary) => SliverToBoxAdapter(
                child: ContentWidthConstraint(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  child: _TeamPageCard(
                    teamSlug: teamSlug,
                    pageSummary: pageSummary,
                  ),
                ),
              )),

        const SliverPadding(padding: EdgeInsets.only(bottom: 32)),
      ],
    );
  }
}

/// Displays a team page's full content inline.
class _TeamPageCard extends ConsumerWidget {
  final String teamSlug;
  final TeamPageSummaryDto pageSummary;

  const _TeamPageCard({
    required this.teamSlug,
    required this.pageSummary,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final params = (teamSlug: teamSlug, pageSlug: pageSummary.slug);
    final pageAsync = ref.watch(_teamPageProvider(params));

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              pageSummary.title,
              style: Theme.of(context).textTheme.titleMedium,
            ),
            const SizedBox(height: 8),
            pageAsync.when(
              data: (page) => MarkdownContent(
                data: page.media.markdown,
                images: page.media.assets.images,
              ),
              loading: () => const Padding(
                padding: EdgeInsets.symmetric(vertical: 16),
                child: Center(child: CircularProgressIndicator()),
              ),
              error: (error, _) => Text(
                'common.loadError'.tr(),
                style: TextStyle(
                    color: Theme.of(context).colorScheme.error),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _StatItem extends StatelessWidget {
  final IconData icon;
  final String value;
  final String label;

  const _StatItem({
    required this.icon,
    required this.value,
    required this.label,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Icon(icon, color: Theme.of(context).colorScheme.primary),
        const SizedBox(height: 4),
        Text(
          value,
          style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.bold,
              ),
        ),
        Text(
          label,
          style: Theme.of(context).textTheme.bodySmall,
        ),
      ],
    );
  }
}
