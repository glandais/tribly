import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/pedalons_api_client.dart';
import '../../../core/pagination/pagination.dart';
import '../domain/member_filters.dart';
import '../domain/team_discovery_filters.dart';

final teamRepositoryProvider = Provider<TeamRepository>((ref) {
  return TeamRepository(
    ref.watch(teamsClientProvider),
    ref.watch(teamMembersClientProvider),
  );
});

class TeamRepository {
  final TeamsClient _teamsClient;
  final TeamMembersClient _teamMembersClient;

  TeamRepository(this._teamsClient, this._teamMembersClient);

  /// Get teams the current user is a member of
  Future<List<TeamDetailDto>> getMyTeams() async {
    final response = await _teamsClient.listTeams(minRole: MinRole.member);
    return response.teams;
  }

  /// Get public teams (for discovery)
  Future<List<TeamDetailDto>> getPublicTeams({
    int page = 0,
    int size = 20,
    String? search,
  }) async {
    final response = await _teamsClient.listTeams(
      page: page,
      size: size,
      search: search,
    );
    return response.teams;
  }

  /// Get team details
  Future<TeamDetailDto> getTeam(String slug) {
    return _teamsClient.getTeam(teamSlug: slug);
  }

  /// Join a team
  Future<MemberDto> joinTeam(String slug) {
    return _teamMembersClient.joinTeam(teamSlug: slug);
  }

  /// Leave a team
  Future<void> leaveTeam(String slug) {
    return _teamMembersClient.leaveTeam(teamSlug: slug);
  }

  /// Get team members
  Future<List<MemberDto>> getTeamMembers(String slug) async {
    final response = await _teamMembersClient.getMembers(teamSlug: slug);
    return response.members;
  }

  /// One page of team members, filtered by role and search.
  ///
  /// C'est ce que [getTeamMembers] ne faisait pas : sans `page` ni `size`, le
  /// mobile montrait la page du serveur — vingt membres sur mille neuf cent
  /// quatre-vingt-dix-neuf — **sans le dire**. `total` est la seule chose qui
  /// permette de l'annoncer, et il est déjà dans la réponse.
  Future<PageResult<MemberDto>> fetchMembers({
    required MemberFilters filters,
    int page = 0,
    int size = kDefaultPageSize,
  }) async {
    final String? search = filters.search?.trim();
    final MemberListResponse response = await _teamMembersClient.getMembers(
      teamSlug: filters.teamSlug,
      role: filters.role,
      search: search == null || search.isEmpty ? null : search,
      page: page,
      size: size,
    );
    return PageResult<MemberDto>(
      items: response.members,
      total: response.total,
    );
  }

  /// One page of the team directory.
  ///
  /// `GET /api/teams` n'a **aucun paramètre de tri** : l'ordre est celui du
  /// serveur, et l'écran de découverte n'annonce donc aucun tri (§5.3).
  Future<PageResult<TeamDetailDto>> fetchTeams({
    required TeamDiscoveryFilters filters,
    int page = 0,
    int size = kDefaultPageSize,
  }) async {
    final String? search = filters.search?.trim();
    final TeamListResponse response = await _teamsClient.listTeams(
      page: page,
      size: size,
      joinable: filters.scope == TeamDiscoveryScope.joinable ? true : null,
      minRole: filters.scope == TeamDiscoveryScope.mine ? MinRole.member : null,
      search: search == null || search.isEmpty ? null : search,
    );
    return PageResult<TeamDetailDto>(
      items: response.teams,
      total: response.total,
    );
  }
}
