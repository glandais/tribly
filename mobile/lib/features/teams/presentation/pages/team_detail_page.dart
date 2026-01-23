import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/utils/safe_string.dart';
import '../../../rides/data/ride_repository.dart';
import '../../../routes/data/route_repository.dart';
import '../../data/team_repository.dart';

final teamDetailProvider =
    FutureProvider.family<TeamDetailDto, String>((ref, slug) async {
  final repository = ref.watch(teamRepositoryProvider);
  return repository.getTeam(slug);
});

final teamRidesProvider =
    FutureProvider.family<List<RideDto>, String>((ref, teamSlug) async {
  final repository = ref.watch(rideRepositoryProvider);
  return repository.getTeamRides(teamSlug, upcoming: true);
});

final teamRoutesProvider =
    FutureProvider.family<List<RouteDto>, String>((ref, teamSlug) async {
  final repository = ref.watch(routeRepositoryProvider);
  return repository.getTeamRoutes(teamSlug);
});

class TeamDetailPage extends ConsumerWidget {
  final String teamSlug;

  const TeamDetailPage({super.key, required this.teamSlug});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final teamAsync = ref.watch(teamDetailProvider(teamSlug));

    return teamAsync.when(
      data: (team) => _TeamDetailContent(team: team),
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
              Text('Erreur: $error'),
              const SizedBox(height: 16),
              FilledButton(
                onPressed: () => ref.invalidate(teamDetailProvider(teamSlug)),
                child: const Text('Réessayer'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _TeamDetailContent extends ConsumerWidget {
  final TeamDetailDto team;

  const _TeamDetailContent({required this.team});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final ridesAsync = ref.watch(teamRidesProvider(team.slug));
    final routesAsync = ref.watch(teamRoutesProvider(team.slug));

    return Scaffold(
      body: RefreshIndicator(
        onRefresh: () async {
          ref.invalidate(teamDetailProvider(team.slug));
          ref.invalidate(teamRidesProvider(team.slug));
          ref.invalidate(teamRoutesProvider(team.slug));
        },
        child: CustomScrollView(
          slivers: [
            // App bar with team info
            SliverAppBar(
              expandedHeight: 200,
              pinned: true,
              flexibleSpace: FlexibleSpaceBar(
                title: Text(team.name),
                background: Container(
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
                  child: Center(
                    child: team.about.assets.logo != null
                        ? CircleAvatar(
                            radius: 40,
                            backgroundImage: NetworkImage(team.about.assets.logo!.url),
                            onBackgroundImageError: (exception, stackTrace) {},
                          )
                        : CircleAvatar(
                            radius: 40,
                            child: Text(
                              team.name.safeFirstUpper(),
                              style: const TextStyle(fontSize: 32),
                            ),
                          ),
                  ),
                ),
              ),
            ),

            // Team stats
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.all(16),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    _StatItem(
                      icon: Icons.people,
                      value: '${team.memberCount}',
                      label: 'Membres',
                    ),
                    ridesAsync.when(
                      data: (rides) => _StatItem(
                        icon: Icons.directions_bike,
                        value: '${rides.length}',
                        label: 'Sorties',
                      ),
                      loading: () => const _StatItem(
                        icon: Icons.directions_bike,
                        value: '-',
                        label: 'Sorties',
                      ),
                      error: (_, __) => const _StatItem(
                        icon: Icons.directions_bike,
                        value: '?',
                        label: 'Sorties',
                      ),
                    ),
                    routesAsync.when(
                      data: (routes) => _StatItem(
                        icon: Icons.route,
                        value: '${routes.length}',
                        label: 'Itinéraires',
                      ),
                      loading: () => const _StatItem(
                        icon: Icons.route,
                        value: '-',
                        label: 'Itinéraires',
                      ),
                      error: (_, __) => const _StatItem(
                        icon: Icons.route,
                        value: '?',
                        label: 'Itinéraires',
                      ),
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
                            'À propos',
                            style: Theme.of(context).textTheme.titleMedium,
                          ),
                          const SizedBox(height: 8),
                          Text(team.about.markdown),
                        ],
                      ),
                    ),
                  ),
                ),
              ),

            // Upcoming rides section
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.all(16),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      'Prochaines sorties',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    TextButton(
                      onPressed: () {
                        // TODO: Navigate to all rides
                      },
                      child: const Text('Voir tout'),
                    ),
                  ],
                ),
              ),
            ),

            ridesAsync.when(
              data: (rides) {
                if (rides.isEmpty) {
                  return SliverToBoxAdapter(
                    child: ContentWidthConstraint(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      child: const Card(
                        child: Padding(
                          padding: EdgeInsets.all(24),
                          child: Center(
                            child: Text('Aucune sortie à venir'),
                          ),
                        ),
                      ),
                    ),
                  );
                }
                return SliverList(
                  delegate: SliverChildBuilderDelegate(
                    (context, index) {
                      if (index >= rides.length || index >= 3) {
                        return null;
                      }
                      return ContentWidthConstraint(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 4,
                        ),
                        child: _RideCard(ride: rides[index]),
                      );
                    },
                    childCount: rides.length > 3 ? 3 : rides.length,
                  ),
                );
              },
              loading: () => const SliverToBoxAdapter(
                child: Center(child: CircularProgressIndicator()),
              ),
              error: (_, __) => const SliverToBoxAdapter(
                child: ContentWidthConstraint(
                  padding: EdgeInsets.all(16),
                  child: Text('Erreur de chargement'),
                ),
              ),
            ),

            // Routes section
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.all(16),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      'Itinéraires',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    TextButton(
                      onPressed: () => context.push(Paths.routes(team.slug)),
                      child: const Text('Voir tout'),
                    ),
                  ],
                ),
              ),
            ),

            routesAsync.when(
              data: (routes) {
                if (routes.isEmpty) {
                  return SliverToBoxAdapter(
                    child: ContentWidthConstraint(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      child: const Card(
                        child: Padding(
                          padding: EdgeInsets.all(24),
                          child: Center(
                            child: Text('Aucun itinéraire'),
                          ),
                        ),
                      ),
                    ),
                  );
                }
                return SliverToBoxAdapter(
                  child: _AdaptiveRouteCards(routes: routes),
                );
              },
              loading: () => const SliverToBoxAdapter(
                child: Center(child: CircularProgressIndicator()),
              ),
              error: (_, __) => const SliverToBoxAdapter(
                child: ContentWidthConstraint(
                  padding: EdgeInsets.all(16),
                  child: Text('Erreur de chargement'),
                ),
              ),
            ),

            const SliverPadding(padding: EdgeInsets.only(bottom: 32)),
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

class _RideCard extends StatelessWidget {
  final RideDto ride;

  const _RideCard({required this.ride});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: InkWell(
        onTap: () => context.push(Paths.ride(ride.team.slug, ride.slug)),
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Row(
            children: [
              // Thumbnail or icon
              Container(
                width: 60,
                height: 60,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(8),
                  color: Theme.of(context).colorScheme.primaryContainer,
                  image: ride.routeThumbnailUrl != null
                      ? DecorationImage(
                          image: NetworkImage(ride.routeThumbnailUrl!),
                          fit: BoxFit.cover,
                        )
                      : null,
                ),
                child: ride.routeThumbnailUrl == null
                    ? Icon(
                        Icons.directions_bike,
                        color: Theme.of(context).colorScheme.onPrimaryContainer,
                      )
                    : null,
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      ride.name,
                      style: Theme.of(context).textTheme.titleSmall?.copyWith(
                            fontWeight: FontWeight.bold,
                          ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: 4),
                    Row(
                      children: [
                        Icon(
                          Icons.calendar_today,
                          size: 14,
                          color: Theme.of(context).colorScheme.outline,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          _formatDate(DateTime.parse(ride.dateTime)),
                          style: Theme.of(context).textTheme.bodySmall,
                        ),
                      ],
                    ),
                    const SizedBox(height: 2),
                    Row(
                      children: [
                        Icon(
                          Icons.people,
                          size: 14,
                          color: Theme.of(context).colorScheme.outline,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          '${ride.participantCount} participants',
                          style: Theme.of(context).textTheme.bodySmall,
                        ),
                      ],
                    ),
                  ],
                ),
              ),
              const Icon(Icons.chevron_right),
            ],
          ),
        ),
      ),
    );
  }

  String _formatDate(DateTime date) {
    final now = DateTime.now();
    final diff = date.difference(now);
    if (diff.inDays == 0) {
      return "Aujourd'hui ${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}";
    } else if (diff.inDays == 1) {
      return "Demain ${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}";
    }
    return "${date.day}/${date.month} ${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}";
  }
}

class _RouteCard extends StatelessWidget {
  final RouteDto route;

  const _RouteCard({required this.route});

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(right: 12),
      child: InkWell(
        onTap: () => context.push(Paths.route(route.team.slug, route.slug)),
        borderRadius: BorderRadius.circular(12),
        child: SizedBox(
          width: 200,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Thumbnail
              Container(
                height: 80,
                decoration: BoxDecoration(
                  borderRadius:
                      const BorderRadius.vertical(top: Radius.circular(12)),
                  color: Theme.of(context).colorScheme.primaryContainer,
                  image: route.media.assets.thumbnail != null
                      ? DecorationImage(
                          image: NetworkImage(route.media.assets.thumbnail!.url),
                          fit: BoxFit.cover,
                        )
                      : null,
                ),
                child: route.media.assets.thumbnail == null
                    ? Center(
                        child: Icon(
                          Icons.route,
                          size: 32,
                          color:
                              Theme.of(context).colorScheme.onPrimaryContainer,
                        ),
                      )
                    : null,
              ),
              Padding(
                padding: const EdgeInsets.all(12),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      route.name,
                      style: Theme.of(context).textTheme.titleSmall,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '${(route.distance / 1000).toStringAsFixed(1)} km • ${route.elevationGain.toInt()} m D+',
                      style: Theme.of(context).textTheme.bodySmall,
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

/// Adaptive route cards that use horizontal scroll on compact and grid on larger screens.
class _AdaptiveRouteCards extends StatelessWidget {
  final List<RouteDto> routes;

  const _AdaptiveRouteCards({required this.routes});

  @override
  Widget build(BuildContext context) {
    final width = MediaQuery.sizeOf(context).width;
    final sizeClass = Breakpoints.getWindowSizeClass(width);
    final displayRoutes = routes.length > 5 ? routes.sublist(0, 5) : routes;

    // On compact screens, use horizontal scroll
    if (sizeClass == WindowSizeClass.compact) {
      return SizedBox(
        height: 160,
        child: ListView.builder(
          scrollDirection: Axis.horizontal,
          padding: const EdgeInsets.symmetric(horizontal: 16),
          itemCount: displayRoutes.length,
          itemBuilder: (context, index) => _RouteCard(route: displayRoutes[index]),
        ),
      );
    }

    // On larger screens, use wrap/grid layout
    final columns = Breakpoints.gridColumns(sizeClass);
    return ContentWidthConstraint(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: GridView.builder(
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: columns,
          crossAxisSpacing: 12,
          mainAxisSpacing: 12,
          childAspectRatio: 1.3,
        ),
        itemCount: displayRoutes.length,
        itemBuilder: (context, index) => _RouteCard(route: displayRoutes[index]),
      ),
    );
  }
}
