import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../api/generated/export.dart';
import '../../../api/tribly_api_client.dart';

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
}
