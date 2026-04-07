import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../config/paths.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../teams/presentation/widgets/team_sliver_app_bar.dart';

final _teamAdsProvider =
    FutureProvider.family<List<AdDto>, String>((ref, teamSlug) async {
  final client = ref.watch(adsClientProvider);
  final response = await client.listAds(teamSlug: teamSlug);
  return response.ads;
});

class AdsPage extends ConsumerWidget {
  final String teamSlug;
  final TeamDetailDto team;

  const AdsPage({
    super.key,
    required this.teamSlug,
    required this.team,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final adsAsync = ref.watch(_teamAdsProvider(teamSlug));

    return RefreshIndicator(
      onRefresh: () => ref.refresh(_teamAdsProvider(teamSlug).future),
      child: CustomScrollView(
        slivers: [
          TeamSliverAppBar(team: team),
          adsAsync.when(
            data: (ads) {
              if (ads.isEmpty) {
                return SliverFillRemaining(
                  child: Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        AnimatedEmptyState(
                          child: Icon(
                            Icons.sell,
                            size: 64,
                            color: Theme.of(context).colorScheme.outline,
                          ),
                        ),
                        const SizedBox(height: 16),
                        Text(
                          'ads.empty'.tr(),
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
                  itemCount: ads.length,
                  separatorBuilder: (context, index) =>
                      const SizedBox(height: 8),
                  itemBuilder: (context, index) {
                    return ContentWidthConstraint(
                      child: _AdCard(ad: ads[index]),
                    );
                  },
                ),
              );
            },
            loading: () => SliverPadding(
              padding: const EdgeInsets.all(16),
              sliver: SliverToBoxAdapter(
                child: ContentWidthConstraint(
                  child: const ShimmerCardList(itemCount: 4),
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
                          ref.invalidate(_teamAdsProvider(teamSlug)),
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

class _AdCard extends StatelessWidget {
  final AdDto ad;

  const _AdCard({required this.ad});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return AnimatedCard(
      onTap: () => context.push(Paths.ad(ad.team.slug, ad.slug)),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Row(
          children: [
            Container(
              width: 60,
              height: 60,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(8),
                color: theme.colorScheme.secondaryContainer,
              ),
              child: Icon(
                _adTypeIcon(ad.adType),
                color: theme.colorScheme.onSecondaryContainer,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Icon(_adTypeIcon(ad.adType),
                          size: 14, color: theme.colorScheme.secondary),
                      const SizedBox(width: 4),
                      Text(
                        'ads.adType.${ad.adType}'.tr(),
                        style: theme.textTheme.labelSmall
                            ?.copyWith(color: theme.colorScheme.secondary),
                      ),
                    ],
                  ),
                  const SizedBox(height: 2),
                  Text(
                    ad.name,
                    style: theme.textTheme.titleSmall
                        ?.copyWith(fontWeight: FontWeight.bold),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  if (ad.price != null) ...[
                    const SizedBox(height: 4),
                    Text(
                      '${ad.price!.toStringAsFixed(0)} €',
                      style: theme.textTheme.bodySmall?.copyWith(
                        fontWeight: FontWeight.bold,
                        color: theme.colorScheme.primary,
                      ),
                    ),
                  ],
                ],
              ),
            ),
            const Icon(Icons.chevron_right),
          ],
        ),
      ),
    );
  }

  IconData _adTypeIcon(String adType) {
    return switch (adType) {
      'SALE' => Icons.sell,
      'RENTAL' => Icons.key,
      'WANTED' => Icons.search,
      _ => Icons.sell,
    };
  }
}
