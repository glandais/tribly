import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../api/pedalons_api_client.dart';
import '../../../../config/paths.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/utils/api_error_handler.dart';
import '../../../../core/widgets/authenticated_image.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../core/utils/safe_string.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../auth/services/passkey_service.dart';
import '../../../teams/data/team_repository.dart';

/// Provider for upcoming rides on home page (via publications endpoint)
final homeUpcomingRidesProvider =
    FutureProvider<List<RideDto>>((ref) async {
  final client = ref.watch(publicationsClientProvider);
  final response = await client.listAllPublications(
    type: PublicationType.ride,
    from: DateTime.now().toUtc().toIso8601String(),
    size: 5,
  );
  return response.publications
      .whereType<PublicationDtoRide>()
      .map((p) => RideDto(
            type: 'RIDE',
            team: p.team,
            id: p.id,
            slug: p.slug,
            name: p.name,
            media: p.media,
            dateTime: p.dateTime,
            status: p.status,
            visibility: p.visibility,
            participantCount: p.participantCount,
            groupCount: p.groupCount,
            groups: p.groups,
            topParticipants: p.topParticipants,
            publishAt: p.publishAt,
            createdAt: p.createdAt,
            routeSlug: p.routeSlug,
            startPlace: p.startPlace,
            endPlace: p.endPlace,
            thumbnailLightUrl: p.thumbnailLightUrl,
            thumbnailDarkUrl: p.thumbnailDarkUrl,
            deleted: p.deleted,
          ))
      .toList();
});

/// Provider for user's teams on home page
final homeTeamsProvider = FutureProvider<List<TeamDetailDto>>((ref) async {
  final repository = ref.watch(teamRepositoryProvider);
  return repository.getMyTeams();
});

class HomePage extends ConsumerWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final authState = ref.watch(authProvider);
    final theme = Theme.of(context);
    final upcomingRides = ref.watch(homeUpcomingRidesProvider);
    final teams = ref.watch(homeTeamsProvider);

    final userName = authState.user?.displayName.split(' ').firstOrNull ??
        'home.defaultGreeting'.tr();

    return Scaffold(
      body: RefreshIndicator(
        onRefresh: () async {
          ref.invalidate(homeUpcomingRidesProvider);
          ref.invalidate(homeTeamsProvider);
        },
        child: CustomScrollView(
          slivers: [
            // App bar with greeting
            SliverAppBar(
              expandedHeight: 120,
              pinned: true,
              flexibleSpace: FlexibleSpaceBar(
                title: Text(
                  'home.greeting'.tr(namedArgs: {'name': userName}),
                  style: const TextStyle(fontSize: 18),
                ),
                background: Container(
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      begin: Alignment.topCenter,
                      end: Alignment.bottomCenter,
                      colors: [
                        theme.colorScheme.primary,
                        theme.colorScheme.primaryContainer,
                      ],
                    ),
                  ),
                ),
              ),
            ),

            // Passkey prompt if not configured
            if (!authState.hasPasskeys)
              SliverToBoxAdapter(
                child: ContentWidthConstraint(
                  padding: const EdgeInsets.all(16),
                  child: Card(
                    color: theme.colorScheme.primaryContainer,
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Row(
                        children: [
                          Icon(
                            Icons.fingerprint,
                            color: theme.colorScheme.onPrimaryContainer,
                          ),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  'auth.passkey.simplifiedLogin'.tr(),
                                  style: theme.textTheme.titleSmall?.copyWith(
                                    color: theme.colorScheme.onPrimaryContainer,
                                  ),
                                ),
                                Text(
                                  'auth.passkey.enablePrompt'.tr(),
                                  style: theme.textTheme.bodySmall?.copyWith(
                                    color: theme.colorScheme.onPrimaryContainer,
                                  ),
                                ),
                              ],
                            ),
                          ),
                          TextButton(
                            onPressed: () => _registerPasskey(context, ref),
                            child: Text('auth.passkey.enable'.tr()),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),

            // Upcoming events section
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      'home.upcomingRides'.tr(),
                      style: theme.textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    TextButton(
                      onPressed: () => context.go(Paths.calendar()),
                      child: Text('common.viewAll'.tr()),
                    ),
                  ],
                ),
              ),
            ),

            upcomingRides.when(
              data: (rides) {
                if (rides.isEmpty) {
                  return SliverToBoxAdapter(
                    child: ContentWidthConstraint(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      child: Card(
                        child: Padding(
                          padding: const EdgeInsets.all(24),
                          child: Column(
                            children: [
                              AnimatedEmptyState(
                                child: Icon(
                                  Icons.event_available,
                                  size: 48,
                                  color: theme.colorScheme.outline,
                                ),
                              ),
                              const SizedBox(height: 8),
                              Text('home.noUpcomingRides'.tr()),
                            ],
                          ),
                        ),
                      ),
                    ),
                  );
                }

                final displayCount = rides.length > 3 ? 3 : rides.length;
                return SliverList(
                  delegate: SliverChildBuilderDelegate(
                    (context, index) {
                      if (index >= displayCount) return null;
                      final ride = rides[index];
                      return ContentWidthConstraint(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 4,
                        ),
                        child: StaggeredListItem(
                          index: index,
                          child: _RideCard(key: ValueKey(ride.slug), ride: ride),
                        ),
                      );
                    },
                    childCount: displayCount,
                  ),
                );
              },
              loading: () => SliverToBoxAdapter(
                child: ContentWidthConstraint(
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  child: Column(
                    children: List.generate(
                      3,
                      (index) => const Padding(
                        padding: EdgeInsets.only(bottom: 8),
                        child: ShimmerEventCard(),
                      ),
                    ),
                  ),
                ),
              ),
              error: (_, _) => SliverToBoxAdapter(
                child: ContentWidthConstraint(
                  padding: const EdgeInsets.all(16),
                  child: Card(
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Row(
                        children: [
                          const Icon(Icons.error_outline, color: Colors.orange),
                          const SizedBox(width: 12),
                          Expanded(child: Text('home.unableToLoadEvents'.tr())),
                          TextButton(
                            onPressed: () => ref.invalidate(homeUpcomingRidesProvider),
                            child: Text('common.retry'.tr()),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ),

            // My teams section
            SliverToBoxAdapter(
              child: ContentWidthConstraint(
                padding: const EdgeInsets.fromLTRB(16, 24, 16, 8),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      'home.myTeams'.tr(),
                      style: theme.textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    TextButton(
                      onPressed: () => context.go(Paths.teams()),
                      child: Text('common.viewAll'.tr()),
                    ),
                  ],
                ),
              ),
            ),

            teams.when(
              data: (teamList) {
                if (teamList.isEmpty) {
                  return SliverToBoxAdapter(
                    child: ContentWidthConstraint(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      child: Card(
                        child: Padding(
                          padding: const EdgeInsets.all(24),
                          child: Column(
                            children: [
                              AnimatedEmptyState(
                                child: Icon(
                                  Icons.group_add,
                                  size: 48,
                                  color: theme.colorScheme.outline,
                                ),
                              ),
                              const SizedBox(height: 8),
                              Text('home.joinTeamPrompt'.tr()),
                              const SizedBox(height: 16),
                              FilledButton.icon(
                                onPressed: () => context.go(Paths.teams()),
                                icon: const Icon(Icons.search),
                                label: Text('common.discover'.tr()),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ),
                  );
                }

                return SliverToBoxAdapter(
                  child: _AdaptiveTeamChips(teams: teamList),
                );
              },
              loading: () => SliverToBoxAdapter(
                child: SizedBox(
                  height: 100,
                  child: ListView.builder(
                    scrollDirection: Axis.horizontal,
                    padding: const EdgeInsets.symmetric(horizontal: 16),
                    itemCount: 4,
                    itemBuilder: (context, index) => const ShimmerTeamChip(),
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

  Future<void> _registerPasskey(BuildContext context, WidgetRef ref) async {
    try {
      final passkeyService = ref.read(passkeyServiceProvider);
      await passkeyService.register(deviceName: 'Mobile');
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('auth.passkey.success'.tr())),
        );
        ref.invalidate(authProvider);
      }
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text(getErrorMessage(e))),
        );
      }
    }
  }
}

class _RideCard extends ConsumerWidget {
  final RideDto ride;

  const _RideCard({super.key, required this.ride});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final token = ref.watch(accessTokenHolderProvider);
    final theme = Theme.of(context);

    final thumbnailUrl = theme.brightness == Brightness.dark
        ? ride.thumbnailDarkUrl
        : ride.thumbnailLightUrl;

    return AnimatedCard(
      onTap: () => context.push(Paths.ride(ride.team.slug, ride.slug)),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Row(
          children: [
            // Thumbnail or icon with Hero animation
            Hero(
              tag: 'ride-thumbnail-${ride.slug}',
              child: Container(
              width: 60,
              height: 60,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(8),
                color: theme.colorScheme.primaryContainer,
                image: thumbnailUrl != null
                    ? DecorationImage(
                        image: AuthenticatedDecorationImage.fromUrl(
                          thumbnailUrl,
                          token,
                        )!,
                        fit: BoxFit.cover,
                      )
                    : null,
              ),
              child: thumbnailUrl == null
                  ? Icon(
                      Icons.directions_bike,
                      color: theme.colorScheme.onPrimaryContainer,
                    )
                  : null,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    ride.name,
                    style: theme.textTheme.titleSmall?.copyWith(
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
                        color: theme.colorScheme.outline,
                      ),
                      const SizedBox(width: 4),
                      Text(
                        _formatDate(DateTime.parse(ride.dateTime)),
                        style: theme.textTheme.bodySmall,
                      ),
                    ],
                  ),
                  const SizedBox(height: 2),
                  Row(
                    children: [
                      Icon(
                        Icons.people,
                        size: 14,
                        color: theme.colorScheme.outline,
                      ),
                      const SizedBox(width: 4),
                      Text(
                        '${ride.team.name} • ${'rides.participants'.tr(namedArgs: {'count': ride.participantCount.toString()})}',
                        style: theme.textTheme.bodySmall,
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
    );
  }

  String _formatDate(DateTime date) {
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final dateDay = DateTime(date.year, date.month, date.day);
    final dayDiff = dateDay.difference(today).inDays;
    final time = AppFormatters.formatTime(date);
    if (dayDiff == 0) {
      return "${AppFormatters.today} $time";
    } else if (dayDiff == 1) {
      return "${AppFormatters.tomorrow} $time";
    }
    return "${AppFormatters.formatFullDate(date)} $time";
  }
}

class _TeamChip extends ConsumerWidget {
  final TeamDetailDto team;

  const _TeamChip({super.key, required this.team});

  String? get _logoUrl => team.about.assets.logo?.url;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final theme = Theme.of(context);

    return Padding(
      padding: const EdgeInsets.only(right: 12),
      child: AnimatedCard(
        onTap: () => context.push(Paths.team(team.slug)),
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // Hero animation for team logo
              Hero(
                tag: 'team-logo-${team.slug}',
                child: AuthenticatedCircleAvatar(
                  imageUrl: _logoUrl,
                  fallbackText: team.name.safeFirstUpper(),
                  radius: 20,
                  fontSize: 16,
                ),
              ),
              const SizedBox(height: 8),
              SizedBox(
                width: 80,
                child: Text(
                  team.name,
                  style: theme.textTheme.bodySmall,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  textAlign: TextAlign.center,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

/// Adaptive team chips that wrap on larger screens instead of horizontal scroll.
class _AdaptiveTeamChips extends StatelessWidget {
  final List<TeamDetailDto> teams;

  const _AdaptiveTeamChips({required this.teams});

  @override
  Widget build(BuildContext context) {
    final width = MediaQuery.sizeOf(context).width;
    final sizeClass = Breakpoints.getWindowSizeClass(width);
    final displayTeams = teams.length > 5 ? teams.sublist(0, 5) : teams;

    // On compact screens, use horizontal scroll with staggered animation
    if (sizeClass == WindowSizeClass.compact) {
      return SizedBox(
        height: 100,
        child: ListView.builder(
          scrollDirection: Axis.horizontal,
          padding: const EdgeInsets.symmetric(horizontal: 16),
          itemCount: displayTeams.length,
          itemBuilder: (context, index) => StaggeredListItem(
            index: index,
            child: _TeamChip(key: ValueKey(displayTeams[index].slug), team: displayTeams[index]),
          ),
        ),
      );
    }

    // On larger screens, use wrap layout with staggered animation
    return ContentWidthConstraint(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: Wrap(
        spacing: 12,
        runSpacing: 12,
        children: displayTeams.asMap().entries.map((entry) => StaggeredListItem(
          index: entry.key,
          child: _TeamChip(key: ValueKey(entry.value.slug), team: entry.value),
        )).toList(),
      ),
    );
  }
}
