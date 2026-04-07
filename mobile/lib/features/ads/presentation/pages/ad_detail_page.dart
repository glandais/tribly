import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../core/widgets/team_banner.dart';
import '../../../../core/widgets/widgets.dart';
import '../../data/ad_repository.dart';

final adDetailProvider =
    FutureProvider.family<AdDto, ({String teamSlug, String adSlug})>(
        (ref, params) async {
  final repository = ref.watch(adRepositoryProvider);
  return repository.getAd(params.teamSlug, params.adSlug);
});

class AdDetailPage extends ConsumerWidget {
  final String teamSlug;
  final String adSlug;

  const AdDetailPage({
    super.key,
    required this.teamSlug,
    required this.adSlug,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final params = (teamSlug: teamSlug, adSlug: adSlug);
    final adAsync = ref.watch(adDetailProvider(params));

    return adAsync.when(
      data: (ad) => _AdDetailContent(ad: ad),
      loading: () => Scaffold(
        appBar: AppBar(),
        body: const Center(child: CircularProgressIndicator()),
      ),
      error: (error, stack) => Scaffold(
        appBar: AppBar(),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(Icons.error_outline,
                  size: 48, color: Theme.of(context).colorScheme.error),
              const SizedBox(height: 16),
              Text(getErrorMessage(error)),
              const SizedBox(height: 16),
              FilledButton(
                onPressed: () => ref.invalidate(adDetailProvider(params)),
                child: Text('common.retry'.tr()),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _AdDetailContent extends StatelessWidget {
  final AdDto ad;

  const _AdDetailContent({required this.ad});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      body: CustomScrollView(
        slivers: [
          SliverAppBar(
            pinned: true,
            title: Text(ad.name),
          ),

          // Team
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.fromLTRB(16, 16, 16, 0),
              child: TeamBanner(team: ad.team),
            ),
          ),

          // Ad type badge and price
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  Chip(
                    label: Text('ads.adType.${ad.adType}'.tr()),
                    backgroundColor:
                        theme.colorScheme.primaryContainer,
                    labelStyle: TextStyle(
                        color: theme.colorScheme.onPrimaryContainer),
                  ),
                  const Spacer(),
                  if (ad.price != null)
                    Text(
                      _formatPrice(ad),
                      style: theme.textTheme.titleLarge?.copyWith(
                        fontWeight: FontWeight.bold,
                        color: theme.colorScheme.primary,
                      ),
                    )
                  else
                    Text(
                      'ads.detail.priceNegotiable'.tr(),
                      style: theme.textTheme.bodyLarge?.copyWith(
                        color: theme.colorScheme.outline,
                        fontStyle: FontStyle.italic,
                      ),
                    ),
                ],
              ),
            ),
          ),

          // Location
          if (ad.locationDescription != null)
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: Card(
                  child: ListTile(
                    leading: Icon(Icons.location_on,
                        color: theme.colorScheme.primary),
                    title: Text(ad.locationDescription!),
                  ),
                ),
              ),
            ),

          // Date
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  Icon(Icons.calendar_today,
                      size: 16, color: theme.colorScheme.outline),
                  const SizedBox(width: 8),
                  Text(
                    AppFormatters.formatFullDate(
                        DateTime.parse(ad.createdAt)),
                    style: theme.textTheme.bodyMedium?.copyWith(
                      color: theme.colorScheme.outline,
                    ),
                  ),
                ],
              ),
            ),
          ),

          // Content
          if (ad.media.markdown.isNotEmpty)
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                child: MarkdownContent(
                  data: ad.media.markdown,
                  images: ad.media.assets.images,
                ),
              ),
            ),

          const SliverPadding(padding: EdgeInsets.only(bottom: 32)),
        ],
      ),
    );
  }

  String _formatPrice(AdDto ad) {
    final price = '${ad.price!.toStringAsFixed(2)} €';
    if (ad.rentalPeriod != null) {
      final period = 'ads.rentalPeriod.${ad.rentalPeriod}'.tr();
      return '$price / $period';
    }
    return price;
  }
}
