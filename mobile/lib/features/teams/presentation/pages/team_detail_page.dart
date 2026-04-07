import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../config/paths.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/widgets/authenticated_image.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../../core/utils/safe_string.dart';
import '../../../rides/data/ride_repository.dart';
import '../../../rides/presentation/widgets/ride_card.dart';
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
              Icon(Icons.error_outline, size: 48, color: Theme.of(context).colorScheme.error),
              const SizedBox(height: 16),
              Text(getErrorMessage(error)),
              const SizedBox(height: 16),
              FilledButton(
                onPressed: () => ref.invalidate(teamDetailProvider(teamSlug)),
                child: Text('common.retry'.tr()),
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
                    // Hero animation for team logo
                    child: Hero(
                      tag: 'team-logo-${team.slug}',
                      child: AuthenticatedCircleAvatar(
                        imageUrl: team.about.assets.logo?.url,
                        fallbackText: team.name.safeFirstUpper(),
                        radius: 40,
                        fontSize: 32,
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
                      label: 'teams.membersLabel'.tr(),
                    ),
                    ridesAsync.when(
                      data: (rides) => _StatItem(
                        icon: Icons.directions_bike,
                        value: '${rides.length}',
                        label: 'rides.title'.tr(),
                      ),
                      loading: () => _StatItem(
                        icon: Icons.directions_bike,
                        value: '-',
                        label: 'rides.title'.tr(),
                      ),
                      error: (_, _) => _StatItem(
                        icon: Icons.directions_bike,
                        value: '?',
                        label: 'rides.title'.tr(),
                      ),
                    ),
                    routesAsync.when(
                      data: (routes) => _StatItem(
                        icon: Icons.route,
                        value: '${routes.length}',
                        label: 'routes.title'.tr(),
                      ),
                      loading: () => _StatItem(
                        icon: Icons.route,
                        value: '-',
                        label: 'routes.title'.tr(),
                      ),
                      error: (_, _) => _StatItem(
                        icon: Icons.route,
                        value: '?',
                        label: 'routes.title'.tr(),
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

            // Upcoming rides section
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.all(16),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      'rides.upcoming'.tr(),
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    TextButton(
                      onPressed: () => context.push(Paths.teamCalendar(team.slug)),
                      child: Text('common.viewAll'.tr()),
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
                      child: Card(
                        child: Padding(
                          padding: const EdgeInsets.all(24),
                          child: Center(
                            child: Text('rides.noUpcoming'.tr()),
                          ),
                        ),
                      ),
                    ),
                  );
                }
                final displayCount = rides.length > 3 ? 3 : rides.length;
                return StaggeredSliverList(
                  itemCount: displayCount,
                  itemBuilder: (context, index) {
                    return ContentWidthConstraint(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 16,
                        vertical: 4,
                      ),
                      child: RideCard(ride: rides[index]),
                    );
                  },
                );
              },
              loading: () => SliverToBoxAdapter(
                child: ContentWidthConstraint(
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  child: const ShimmerCardList(itemCount: 2),
                ),
              ),
              error: (_, _) => SliverToBoxAdapter(
                child: ContentWidthConstraint(
                  padding: const EdgeInsets.all(16),
                  child: Text('common.loadError'.tr()),
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
                      'routes.title'.tr(),
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    TextButton(
                      onPressed: () => context.push(Paths.routes(team.slug)),
                      child: Text('common.viewAll'.tr()),
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
                      child: Card(
                        child: Padding(
                          padding: const EdgeInsets.all(24),
                          child: Center(
                            child: Text('routes.empty'.tr()),
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
              loading: () => SliverToBoxAdapter(
                child: SizedBox(
                  height: 160,
                  child: ListView.builder(
                    scrollDirection: Axis.horizontal,
                    padding: const EdgeInsets.symmetric(horizontal: 16),
                    itemCount: 3,
                    itemBuilder: (context, index) => const Padding(
                      padding: EdgeInsets.only(right: 12),
                      child: SizedBox(
                        width: 200,
                        child: ShimmerRouteGridItem(),
                      ),
                    ),
                  ),
                ),
              ),
              error: (_, _) => SliverToBoxAdapter(
                child: ContentWidthConstraint(
                  padding: const EdgeInsets.all(16),
                  child: Text('common.loadError'.tr()),
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

class _RouteCard extends ConsumerWidget {
  final RouteDto route;

  const _RouteCard({required this.route});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final token = ref.watch(accessTokenHolderProvider);

    final thumbnail = Theme.of(context).brightness == Brightness.dark
        ? route.media.assets.thumbnailDark
        : route.media.assets.thumbnailLight;

    return Padding(
      padding: const EdgeInsets.only(right: 12),
      child: AnimatedCard(
        onTap: () => context.push(Paths.route(route.team.slug, route.slug)),
        child: SizedBox(
          width: 200,
          child: ClipRRect(
            borderRadius: BorderRadius.circular(12),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // Thumbnail with Hero animation
                Hero(
                  tag: 'route-thumbnail-${route.slug}',
                  child: Container(
                    height: 80,
                    decoration: BoxDecoration(
                      color: Theme.of(context).colorScheme.primaryContainer,
                      image: thumbnail != null
                          ? DecorationImage(
                              image: AuthenticatedDecorationImage.fromUrl(
                                thumbnail.url,
                                token,
                              )!,
                              fit: BoxFit.cover,
                            )
                          : null,
                    ),
                    child: thumbnail == null
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

    // On compact screens, use horizontal scroll with staggered animation
    if (sizeClass == WindowSizeClass.compact) {
      return SizedBox(
        height: 160,
        child: ListView.builder(
          scrollDirection: Axis.horizontal,
          padding: const EdgeInsets.symmetric(horizontal: 16),
          itemCount: displayRoutes.length,
          itemBuilder: (context, index) => StaggeredListItem(
            index: index,
            child: _RouteCard(route: displayRoutes[index]),
          ),
        ),
      );
    }

    // On larger screens, use wrap/grid layout with staggered animation
    final columns = Breakpoints.gridColumns(sizeClass);
    return ContentWidthConstraint(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: StaggeredGridView(
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
