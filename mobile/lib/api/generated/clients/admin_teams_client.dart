// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/admin_team_dto.dart';
import '../models/admin_team_list_response.dart';

part 'admin_teams_client.g.dart';

@RestApi()
abstract class AdminTeamsClient {
  factory AdminTeamsClient(Dio dio, {String? baseUrl}) = _AdminTeamsClient;

  /// List all teams.
  ///
  /// Get a paginated list of all teams.
  ///
  /// [domainId] - Filter by domain ID.
  ///
  /// [page] - Page number (0-indexed).
  ///
  /// [size] - Page size.
  @GET('/api/admin/teams')
  Future<AdminTeamListResponse> adminListTeams({
    @Query('page') int? page = 0,
    @Query('size') int? size = 20,
    @Query('domainId') String? domainId,
  });

  /// Get team details.
  ///
  /// Get detailed team information.
  ///
  /// [teamId] - Team ID.
  @GET('/api/admin/teams/{teamId}')
  Future<AdminTeamDto> adminGetTeam({
    @Path('teamId') required String teamId,
  });

  /// Toggle team deleted.
  ///
  /// Toggle team soft-delete status (archive/restore).
  ///
  /// [teamId] - Team ID.
  @POST('/api/admin/teams/{teamId}/toggle-deleted')
  Future<AdminTeamDto> adminToggleTeamDeleted({
    @Path('teamId') required String teamId,
  });
}
