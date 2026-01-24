import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../../api/generated/export.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../core/utils/safe_string.dart';
import '../../data/route_repository.dart';

final routeDetailProvider = FutureProvider.family<RouteDetailDto,
    ({String teamSlug, String routeSlug})>((ref, params) async {
  final repository = ref.watch(routeRepositoryProvider);
  return repository.getRoute(params.teamSlug, params.routeSlug);
});

class RouteDetailPage extends ConsumerWidget {
  final String teamSlug;
  final String routeSlug;

  const RouteDetailPage({
    super.key,
    required this.teamSlug,
    required this.routeSlug,
  });

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final params = (teamSlug: teamSlug, routeSlug: routeSlug);
    final routeAsync = ref.watch(routeDetailProvider(params));

    return routeAsync.when(
      data: (route) => _RouteDetailContent(route: route),
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
              const Icon(Icons.error_outline, size: 48, color: Colors.red),
              const SizedBox(height: 16),
              Text('common.errorPrefix'.tr(args: [error.toString()])),
              const SizedBox(height: 16),
              FilledButton(
                onPressed: () => ref.invalidate(routeDetailProvider(params)),
                child: Text('common.retry'.tr()),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _RouteDetailContent extends StatelessWidget {
  final RouteDetailDto route;

  const _RouteDetailContent({required this.route});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: CustomScrollView(
        slivers: [
          SliverAppBar(
            expandedHeight: 200,
            pinned: true,
            flexibleSpace: FlexibleSpaceBar(
              title: Text(
                route.name,
                style: const TextStyle(fontSize: 16),
              ),
              // Hero animation for route thumbnail
              background: Hero(
                tag: 'route-thumbnail-${route.slug}',
                child: route.media.assets.thumbnail != null
                    ? Image.network(
                        route.media.assets.thumbnail!.url,
                        fit: BoxFit.cover,
                        errorBuilder: (context, error, stackTrace) => const SizedBox.shrink(),
                      )
                    : Container(
                        decoration: BoxDecoration(
                          gradient: LinearGradient(
                            begin: Alignment.topCenter,
                            end: Alignment.bottomCenter,
                            colors: [
                              Theme.of(context).colorScheme.primary,
                              Theme.of(context).colorScheme.primaryContainer,
                            ],
                          ),
                        ),
                        child: const Center(
                          child: Icon(
                            Icons.route,
                            size: 64,
                            color: Colors.white54,
                          ),
                        ),
                      ),
              ),
            ),
          ),

          // Stats
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.all(16),
              child: Card(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceAround,
                    children: [
                      _StatItem(
                        icon: Icons.straighten,
                        value:
                            '${(route.distance / 1000).toStringAsFixed(1)} km',
                        label: 'routes.distance'.tr(),
                      ),
                      _StatItem(
                        icon: Icons.trending_up,
                        value: '${route.elevationGain.toInt()} m',
                        label: 'routes.elevation'.tr(),
                      ),
                      _StatItem(
                        icon: Icons.trending_down,
                        value: '${route.elevationLoss.toInt()} m',
                        label: 'routes.elevationDown'.tr(),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),

          // Surface type
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              child: Card(
                child: ListTile(
                  leading: Icon(
                    AppFormatters.surfaceIcon(route.surfaceType),
                    color: Theme.of(context).colorScheme.primary,
                  ),
                  title: Text('routes.surface'.tr()),
                  subtitle: Text(AppFormatters.surfaceName(route.surfaceType)),
                ),
              ),
            ),
          ),

          // Description
          if (route.media.markdown.isNotEmpty)
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.all(16),
                child: Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'routes.description'.tr(),
                          style: Theme.of(context).textTheme.titleMedium,
                        ),
                        const SizedBox(height: 8),
                        Text(route.media.markdown),
                      ],
                    ),
                  ),
                ),
              ),
            ),

          // Created by
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.all(16),
              child: Row(
                children: [
                  CircleAvatar(
                    radius: 16,
                    backgroundImage: route.createdBy.avatarUrl != null
                        ? NetworkImage(route.createdBy.avatarUrl!)
                        : null,
                    child: route.createdBy.avatarUrl == null
                        ? Text(
                            route.createdBy.displayName.safeFirstUpper(),
                            style: const TextStyle(fontSize: 12),
                          )
                        : null,
                  ),
                  const SizedBox(width: 8),
                  Text(
                    'routes.createdBy'.tr(args: [route.createdBy.displayName]),
                    style: Theme.of(context).textTheme.bodySmall,
                  ),
                ],
              ),
            ),
          ),

          // Map placeholder
          SliverToBoxAdapter(
            child: ContentWidthConstraint(
              padding: const EdgeInsets.all(16),
              child: Card(
                child: Container(
                  height: 200,
                  decoration: BoxDecoration(
                    color: Theme.of(context).colorScheme.surfaceContainerHighest,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(
                          Icons.map,
                          size: 48,
                          color: Theme.of(context).colorScheme.outline,
                        ),
                        const SizedBox(height: 8),
                        Text(
                          'routes.mapPlaceholder'.tr(),
                          style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                                color: Theme.of(context).colorScheme.outline,
                              ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
          ),

          const SliverPadding(padding: EdgeInsets.only(bottom: 100)),
        ],
      ),
      bottomNavigationBar: SafeArea(
        child: ContentWidthConstraint(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              Expanded(
                child: OutlinedButton.icon(
                  onPressed: () {
                    // TODO: Share route
                  },
                  icon: const Icon(Icons.share),
                  label: Text('routes.share'.tr()),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: FilledButton.icon(
                  onPressed: () {
                    // TODO: Export GPX
                  },
                  icon: const Icon(Icons.download),
                  label: Text('routes.exportGpx'.tr()),
                ),
              ),
            ],
          ),
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
          style: Theme.of(context).textTheme.titleMedium?.copyWith(
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
