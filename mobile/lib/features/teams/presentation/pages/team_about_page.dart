import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/widgets/widgets.dart';
import '../widgets/team_sliver_app_bar.dart';

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

        // Custom pages
        if (team.pages != null && team.pages!.isNotEmpty)
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'teams.aboutPage.pages'.tr(),
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                  const SizedBox(height: 8),
                  ...team.pages!.map((page) => Card(
                        child: ListTile(
                          leading: const Icon(Icons.description),
                          title: Text(page.title),
                        ),
                      )),
                ],
              ),
            ),
          ),

        const SliverPadding(padding: EdgeInsets.only(bottom: 32)),
      ],
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
