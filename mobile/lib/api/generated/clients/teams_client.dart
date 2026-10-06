// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/min_role.dart';
import '../models/slug_change_request.dart';
import '../models/sort_direction.dart';
import '../models/team_dashboard_dto.dart';
import '../models/team_detail_dto.dart';
import '../models/team_list_response.dart';
import '../models/team_request.dart';
import '../models/team_sort_by.dart';

part 'teams_client.g.dart';

@RestApi()
abstract class TeamsClient {
  factory TeamsClient(Dio dio, {String? baseUrl}) = _TeamsClient;

  /// List public teams.
  ///
  /// Get a paginated list of public teams with optional search.
  ///
  /// [joinable] - Keep only teams that accept a join request from any domain user (true), or only those that do not (false). Omitted keeps both. A filter on top of the visibility rules, never instead of them.
  ///
  /// [minRole] - Minimum role in team.
  ///
  /// [page] - Page number (0-indexed).
  ///
  /// [search] - Search query to filter teams by name.
  ///
  /// [size] - Page size.
  ///
  /// [sortBy] - Sort column (default: name ascending). MEMBER_COUNT orders by the memberCount the rows carry. The team id always ends the key, so the order is total.
  ///
  /// [sortDir] - Sort direction when sortBy is set (default: DESC).
  @GET('/api/teams')
  Future<TeamListResponse> listTeams({
    @Query('page') int? page = 0,
    @Query('size') int? size = 20,
    @Query('joinable') bool? joinable,
    @Query('minRole') MinRole? minRole,
    @Query('search') String? search,
    @Query('sortBy') TeamSortBy? sortBy,
    @Query('sortDir') SortDirection? sortDir,
  });

  /// Create team.
  ///
  /// Create a new team. The current user will be set as the team owner.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/teams')
  Future<TeamDetailDto> createTeam({
    @Body() required TeamRequest body,
  });

  /// Update team.
  ///
  /// Update team information. Requires ADMIN role.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PUT('/api/teams/{teamSlug}')
  Future<TeamDetailDto> updateTeam({
    @Path('teamSlug') required String teamSlug,
    @Body() required TeamRequest body,
  });

  /// Get team by slug.
  ///
  /// Get detailed team information by URL slug.
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/teams/{teamSlug}')
  Future<TeamDetailDto> getTeam({
    @Path('teamSlug') required String teamSlug,
  });

  /// Delete team.
  ///
  /// Soft delete a team. Requires OWNER role.
  ///
  /// [teamSlug] - Team URL slug.
  @DELETE('/api/teams/{teamSlug}')
  Future<void> deleteTeam({
    @Path('teamSlug') required String teamSlug,
  });

  /// Get the team dashboard.
  ///
  /// Everything a member's « Tableau de bord » shows, in one call, graded by the caller's role: the member sections for everyone, the organizer block for organizers and administrators, the admin block for administrators. Each section is a short page of the matching list, and is null when the team has disabled its module. The teams switcher, the unread notification count and the calendar token are not part of it.
  ///
  /// [teamSlug] - Team URL slug.
  @GET('/api/teams/{teamSlug}/dashboard')
  Future<TeamDashboardDto> getTeamDashboard({
    @Path('teamSlug') required String teamSlug,
  });

  /// Change team slug.
  ///
  /// Change team URL slug. Requires ADMIN role.
  ///
  /// [teamSlug] - Current team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PATCH('/api/teams/{teamSlug}/slug')
  Future<TeamDetailDto> changeTeamSlug({
    @Path('teamSlug') required String teamSlug,
    @Body() required SlugChangeRequest body,
  });
}
