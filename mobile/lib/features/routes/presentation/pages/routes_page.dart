import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../data/route_repository.dart';

final teamRoutesPageProvider =
    FutureProvider.family<List<RouteDto>, String>((ref, teamSlug) async {
  final repository = ref.watch(routeRepositoryProvider);
  return repository.getTeamRoutes(teamSlug);
});

class RoutesPage extends ConsumerWidget {
  final String teamSlug;

  const RoutesPage({super.key, required this.teamSlug});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final routesAsync = ref.watch(teamRoutesPageProvider(teamSlug));

    return Scaffold(
      appBar: AppBar(
        title: const Text('Itinéraires'),
      ),
      body: routesAsync.when(
        data: (routes) {
          if (routes.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.route,
                    size: 64,
                    color: Theme.of(context).colorScheme.outline,
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'Aucun itinéraire',
                    style: Theme.of(context).textTheme.titleLarge,
                  ),
                ],
              ),
            );
          }

          return RefreshIndicator(
            onRefresh: () => ref.refresh(teamRoutesPageProvider(teamSlug).future),
            child: ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: routes.length,
              itemBuilder: (context, index) {
                return _RouteListItem(route: routes[index]);
              },
            ),
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (error, stack) => Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.error_outline, size: 48, color: Colors.red),
              const SizedBox(height: 16),
              Text('Erreur: $error'),
              const SizedBox(height: 16),
              FilledButton(
                onPressed: () =>
                    ref.invalidate(teamRoutesPageProvider(teamSlug)),
                child: const Text('Réessayer'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _RouteListItem extends StatelessWidget {
  final RouteDto route;

  const _RouteListItem({required this.route});

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: () => context.push(Paths.route(route.team.slug, route.slug)),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Thumbnail
            Container(
              height: 120,
              width: double.infinity,
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.primaryContainer,
                image: route.media.assets.thumbnail?.url != null
                    ? DecorationImage(
                        image: NetworkImage(route.media.assets.thumbnail!.url),
                        fit: BoxFit.cover,
                      )
                    : null,
              ),
              child: route.media.assets.thumbnail?.url == null
                  ? Center(
                      child: Icon(
                        Icons.route,
                        size: 48,
                        color: Theme.of(context).colorScheme.onPrimaryContainer,
                      ),
                    )
                  : null,
            ),
            Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    route.name,
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      _StatChip(
                        icon: Icons.straighten,
                        value: '${(route.distance / 1000).toStringAsFixed(1)} km',
                      ),
                      const SizedBox(width: 12),
                      _StatChip(
                        icon: Icons.trending_up,
                        value: '${route.elevationGain.toInt()} m',
                      ),
                      const SizedBox(width: 12),
                      _StatChip(
                        icon: Icons.trending_down,
                        value: '${route.elevationLoss.toInt()} m',
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

  const _StatChip({
    required this.icon,
    required this.value,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(
          icon,
          size: 16,
          color: Theme.of(context).colorScheme.outline,
        ),
        const SizedBox(width: 4),
        Text(
          value,
          style: Theme.of(context).textTheme.bodySmall,
        ),
      ],
    );
  }
}
