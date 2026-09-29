import 'package:flutter_test/flutter_test.dart';
import 'package:pedalons/api/generated/export.dart';
import 'package:pedalons/features/teams/data/team_repository.dart';
import 'package:pedalons/features/teams/domain/team_discovery_filters.dart';

/// Retient les paramètres de tri que l'annuaire envoie.
class _RecordingTeamsClient implements TeamsClient {
  TeamSortBy? sortBy;
  SortDirection? sortDir;

  @override
  Future<TeamListResponse> listTeams({
    int? page = 0,
    int? size = 20,
    bool? joinable,
    MinRole? minRole,
    String? search,
    TeamSortBy? sortBy,
    SortDirection? sortDir,
  }) async {
    this.sortBy = sortBy;
    this.sortDir = sortDir;
    return const TeamListResponse(
      teams: <TeamDetailDto>[],
      total: 0,
      page: 0,
      size: 20,
    );
  }

  @override
  dynamic noSuchMethod(Invocation invocation) => super.noSuchMethod(invocation);
}

class _NoMembersClient implements TeamMembersClient {
  @override
  dynamic noSuchMethod(Invocation invocation) => super.noSuchMethod(invocation);
}

/// `docs/LEDGER_*.md API-13` : l'écran de découverte annonce « triées par
/// nombre de membres » ; c'est ce test qui garantit qu'il le demande.
void main() {
  test(
    'la découverte demande le tri par nombre de membres, décroissant',
    () async {
      final _RecordingTeamsClient client = _RecordingTeamsClient();
      await TeamRepository(
        client,
        _NoMembersClient(),
      ).fetchTeams(filters: const TeamDiscoveryFilters());

      expect(client.sortBy, kTeamDiscoverySort);
      expect(client.sortBy, TeamSortBy.memberCount);
      expect(client.sortDir, SortDirection.desc);
    },
  );
}
