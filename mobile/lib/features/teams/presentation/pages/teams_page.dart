import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../../api/generated/export.dart';
import '../../../../config/paths.dart';
import '../../../../core/adaptive/adaptive.dart';
import '../../../../core/utils/formatters.dart';
import '../../../../core/utils/safe_string.dart';
import '../../data/team_repository.dart';

final myTeamsProvider = FutureProvider<List<TeamDetailDto>>((ref) async {
  final repository = ref.watch(teamRepositoryProvider);
  return repository.getMyTeams();
});

class TeamsPage extends ConsumerWidget {
  const TeamsPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final teamsAsync = ref.watch(myTeamsProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Mes équipes'),
        actions: [
          IconButton(
            icon: const Icon(Icons.search),
            onPressed: () {
              // TODO: Navigate to discover teams
            },
          ),
        ],
      ),
      body: teamsAsync.when(
        data: (teams) {
          if (teams.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(
                    Icons.group_off,
                    size: 64,
                    color: Theme.of(context).colorScheme.outline,
                  ),
                  const SizedBox(height: 16),
                  Text(
                    'Aucune équipe',
                    style: Theme.of(context).textTheme.titleLarge,
                  ),
                  const SizedBox(height: 8),
                  const Text('Rejoignez une équipe pour commencer'),
                  const SizedBox(height: 24),
                  FilledButton.icon(
                    onPressed: () {
                      // TODO: Navigate to discover teams
                    },
                    icon: const Icon(Icons.search),
                    label: const Text('Découvrir des équipes'),
                  ),
                ],
              ),
            );
          }

          return RefreshIndicator(
            onRefresh: () => ref.refresh(myTeamsProvider.future),
            child: ResponsiveGrid(
              padding: const EdgeInsets.all(16),
              itemCount: teams.length,
              childAspectRatio: 2.5,
              itemBuilder: (context, index) {
                final team = teams[index];
                return _TeamCard(team: team);
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
                onPressed: () => ref.invalidate(myTeamsProvider),
                child: const Text('Réessayer'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _TeamCard extends StatelessWidget {
  final TeamDetailDto team;

  const _TeamCard({required this.team});

  String? get _logoUrl => team.about.assets.logo?.url;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: InkWell(
        onTap: () => context.push(Paths.team(team.slug)),
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              // Team logo/avatar
              CircleAvatar(
                radius: 28,
                backgroundImage:
                    _logoUrl != null ? NetworkImage(_logoUrl!) : null,
                child: _logoUrl == null
                    ? Text(
                        team.name.safeFirstUpper(),
                        style: const TextStyle(fontSize: 20),
                      )
                    : null,
              ),
              const SizedBox(width: 16),
              // Team info
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      team.name,
                      style: Theme.of(context).textTheme.titleMedium?.copyWith(
                            fontWeight: FontWeight.bold,
                          ),
                    ),
                    const SizedBox(height: 4),
                    Row(
                      children: [
                        Icon(
                          Icons.people,
                          size: 16,
                          color: Theme.of(context).colorScheme.outline,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          '${team.memberCount} membres',
                          style: Theme.of(context).textTheme.bodySmall,
                        ),
                        if (team.role != null) ...[
                          const SizedBox(width: 12),
                          Container(
                            padding: const EdgeInsets.symmetric(
                              horizontal: 8,
                              vertical: 2,
                            ),
                            decoration: BoxDecoration(
                              color: Theme.of(context)
                                  .colorScheme
                                  .primaryContainer,
                              borderRadius: BorderRadius.circular(12),
                            ),
                            child: Text(
                              AppFormatters.roleName(team.role!),
                              style: TextStyle(
                                fontSize: 10,
                                color: Theme.of(context)
                                    .colorScheme
                                    .onPrimaryContainer,
                              ),
                            ),
                          ),
                        ],
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

}
