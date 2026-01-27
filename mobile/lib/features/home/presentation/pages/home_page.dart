import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/widgets/authenticated_image.dart';
import '../../../../core/widgets/widgets.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../core/utils/safe_string.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../auth/services/passkey_service.dart';
import '../../../calendar/data/calendar_repository.dart';
import '../../../teams/data/team_repository.dart';

/// Provider for upcoming events on home page
final homeUpcomingEventsProvider =
    FutureProvider<List<CalendarEventDto>>((ref) async {
  final repository = ref.watch(calendarRepositoryProvider);
  final now = DateTime.now();
  final end = now.add(const Duration(days: 7));
  return repository.getMyCalendarEvents(start: now, end: end);
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
    final upcomingEvents = ref.watch(homeUpcomingEventsProvider);
    final teams = ref.watch(homeTeamsProvider);

    final userName = authState.user?.displayName.split(' ').firstOrNull ??
        'home.defaultGreeting'.tr();

    return Scaffold(
      body: RefreshIndicator(
        onRefresh: () async {
          ref.invalidate(homeUpcomingEventsProvider);
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

            upcomingEvents.when(
              data: (events) {
                if (events.isEmpty) {
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

                final displayCount = events.length > 3 ? 3 : events.length;
                return SliverList(
                  delegate: SliverChildBuilderDelegate(
                    (context, index) {
                      if (index >= displayCount) return null;
                      final event = events[index];
                      return ContentWidthConstraint(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 4,
                        ),
                        child: StaggeredListItem(
                          index: index,
                          child: _EventCard(key: ValueKey(event.entitySlug), event: event),
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
              error: (_, __) => SliverToBoxAdapter(
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
                            onPressed: () => ref.invalidate(homeUpcomingEventsProvider),
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
              error: (_, __) => SliverToBoxAdapter(
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
          SnackBar(content: Text('auth.errors.generic'.tr(namedArgs: {'error': e.toString()}))),
        );
      }
    }
  }
}

class _EventCard extends StatelessWidget {
  final CalendarEventDto event;

  const _EventCard({super.key, required this.event});

  DateTime get _startDate => DateTime.parse(event.start);

  @override
  Widget build(BuildContext context) {
    final isRide = event.type == 'RIDE';
    final theme = Theme.of(context);
    final startDate = _startDate;

    return AnimatedCard(
      onTap: isRide
          ? () => context.push(Paths.ride(event.teamSlug, event.entitySlug))
          : null,
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Row(
          children: [
            // Date badge
            Container(
              width: 48,
              height: 48,
              decoration: BoxDecoration(
                color: theme.colorScheme.primaryContainer,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    AppFormatters.dayAbbrev(startDate.weekday),
                    style: TextStyle(
                      fontSize: 10,
                      color: theme.colorScheme.onPrimaryContainer,
                    ),
                  ),
                  Text(
                    '${startDate.day}',
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: theme.colorScheme.onPrimaryContainer,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(width: 12),
            // Event info
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    event.title,
                    style: theme.textTheme.titleSmall?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 2),
                  Text(
                    '${event.teamName} • ${AppFormatters.formatTime(startDate)}',
                    style: theme.textTheme.bodySmall,
                  ),
                ],
              ),
            ),
            Icon(
              isRide ? Icons.directions_bike : Icons.event,
              color: theme.colorScheme.primary,
            ),
          ],
        ),
      ),
    );
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
