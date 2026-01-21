import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
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
                  'Bonjour, ${authState.user?.displayName.split(' ').first ?? 'Cycliste'}!',
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
                child: Padding(
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
                                  'Connexion simplifiée',
                                  style: theme.textTheme.titleSmall?.copyWith(
                                    color: theme.colorScheme.onPrimaryContainer,
                                  ),
                                ),
                                Text(
                                  'Activez Passkey pour vous connecter avec votre empreinte',
                                  style: theme.textTheme.bodySmall?.copyWith(
                                    color: theme.colorScheme.onPrimaryContainer,
                                  ),
                                ),
                              ],
                            ),
                          ),
                          TextButton(
                            onPressed: () => _registerPasskey(context, ref),
                            child: const Text('Activer'),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),

            // Upcoming events section
            SliverToBoxAdapter(
              child: Padding(
                padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      'Prochaines sorties',
                      style: theme.textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    TextButton(
                      onPressed: () => context.go(Paths.calendar()),
                      child: const Text('Voir tout'),
                    ),
                  ],
                ),
              ),
            ),

            upcomingEvents.when(
              data: (events) {
                if (events.isEmpty) {
                  return SliverToBoxAdapter(
                    child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      child: Card(
                        child: Padding(
                          padding: const EdgeInsets.all(24),
                          child: Column(
                            children: [
                              Icon(
                                Icons.event_available,
                                size: 48,
                                color: theme.colorScheme.outline,
                              ),
                              const SizedBox(height: 8),
                              const Text('Aucune sortie prévue cette semaine'),
                            ],
                          ),
                        ),
                      ),
                    ),
                  );
                }

                return SliverList(
                  delegate: SliverChildBuilderDelegate(
                    (context, index) {
                      if (index >= events.length || index >= 3) return null;
                      final event = events[index];
                      return Padding(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 4,
                        ),
                        child: _EventCard(event: event),
                      );
                    },
                    childCount: events.length > 3 ? 3 : events.length,
                  ),
                );
              },
              loading: () => const SliverToBoxAdapter(
                child: Center(
                  child: Padding(
                    padding: EdgeInsets.all(24),
                    child: CircularProgressIndicator(),
                  ),
                ),
              ),
              error: (_, __) => SliverToBoxAdapter(
                child: Padding(
                  padding: const EdgeInsets.all(16),
                  child: Card(
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Row(
                        children: [
                          const Icon(Icons.error_outline, color: Colors.orange),
                          const SizedBox(width: 12),
                          const Expanded(child: Text('Impossible de charger les événements')),
                          TextButton(
                            onPressed: () => ref.invalidate(homeUpcomingEventsProvider),
                            child: const Text('Réessayer'),
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
              child: Padding(
                padding: const EdgeInsets.fromLTRB(16, 24, 16, 8),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      'Mes équipes',
                      style: theme.textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    TextButton(
                      onPressed: () => context.go(Paths.teams()),
                      child: const Text('Voir tout'),
                    ),
                  ],
                ),
              ),
            ),

            teams.when(
              data: (teamList) {
                if (teamList.isEmpty) {
                  return SliverToBoxAdapter(
                    child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      child: Card(
                        child: Padding(
                          padding: const EdgeInsets.all(24),
                          child: Column(
                            children: [
                              Icon(
                                Icons.group_add,
                                size: 48,
                                color: theme.colorScheme.outline,
                              ),
                              const SizedBox(height: 8),
                              const Text('Rejoignez une équipe pour commencer'),
                              const SizedBox(height: 16),
                              FilledButton.icon(
                                onPressed: () => context.go(Paths.teams()),
                                icon: const Icon(Icons.search),
                                label: const Text('Découvrir'),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ),
                  );
                }

                return SliverToBoxAdapter(
                  child: SizedBox(
                    height: 100,
                    child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      padding: const EdgeInsets.symmetric(horizontal: 16),
                      itemCount: teamList.length > 5 ? 5 : teamList.length,
                      itemBuilder: (context, index) {
                        final team = teamList[index];
                        return _TeamChip(team: team);
                      },
                    ),
                  ),
                );
              },
              loading: () => const SliverToBoxAdapter(
                child: Center(
                  child: Padding(
                    padding: EdgeInsets.all(24),
                    child: CircularProgressIndicator(),
                  ),
                ),
              ),
              error: (_, __) => const SliverToBoxAdapter(
                child: Padding(
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

  Future<void> _registerPasskey(BuildContext context, WidgetRef ref) async {
    try {
      final passkeyService = ref.read(passkeyServiceProvider);
      await passkeyService.register(deviceName: 'Mobile');
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Passkey ajouté avec succès!')),
        );
        ref.invalidate(authProvider);
      }
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Erreur: $e')),
        );
      }
    }
  }
}

class _EventCard extends StatelessWidget {
  final CalendarEventDto event;

  const _EventCard({required this.event});

  DateTime get _startDate => DateTime.parse(event.start);

  @override
  Widget build(BuildContext context) {
    final isRide = event.type == 'RIDE';
    final theme = Theme.of(context);
    final startDate = _startDate;

    return Card(
      child: InkWell(
        onTap: () {
          if (isRide) {
            context.push(Paths.ride(event.teamSlug, event.entitySlug));
          }
        },
        borderRadius: BorderRadius.circular(12),
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
                      _getDayName(startDate.weekday),
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
                      '${event.teamName} • ${_formatTime(startDate)}',
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
      ),
    );
  }

  String _getDayName(int weekday) {
    const days = ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'];
    return days[weekday - 1];
  }

  String _formatTime(DateTime date) {
    return '${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
  }
}

class _TeamChip extends StatelessWidget {
  final TeamDetailDto team;

  const _TeamChip({required this.team});

  String? get _logoUrl => team.about.assets.logo?.url;

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Card(
      margin: const EdgeInsets.only(right: 12),
      child: InkWell(
        onTap: () => context.push(Paths.team(team.slug)),
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              CircleAvatar(
                radius: 20,
                backgroundImage:
                    _logoUrl != null ? NetworkImage(_logoUrl!) : null,
                child: _logoUrl == null
                    ? Text(
                        team.name.substring(0, 1).toUpperCase(),
                        style: const TextStyle(fontSize: 16),
                      )
                    : null,
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
