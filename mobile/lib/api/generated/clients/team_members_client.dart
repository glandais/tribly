// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/add_member_request.dart';
import '../models/member_dto.dart';
import '../models/member_list_response.dart';
import '../models/team_role.dart';
import '../models/update_member_role_request.dart';

part 'team_members_client.g.dart';

@RestApi()
abstract class TeamMembersClient {
  factory TeamMembersClient(Dio dio, {String? baseUrl}) = _TeamMembersClient;

  /// Get team members.
  ///
  /// Paginated list of team members. Administrators always see it; so do organisers, who need a member list to designate a ride group's leader. Everyone else needs the team to have set enableMemberDirectory. What is returned is graded too: 'role' and 'joinedAt' are null unless the caller is an administrator or the directory is open, and 'search' only matches an e-mail address for an administrator.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [page] - Page number.
  ///
  /// [role] - Filter by role.
  ///
  /// [search] - Search by display name. Also matches the e-mail address, for administrators only.
  ///
  /// [size] - Page size.
  @GET('/api/teams/{teamSlug}/members')
  Future<MemberListResponse> getMembers({
    @Path('teamSlug') required String teamSlug,
    @Query('role') TeamRole? role,
    @Query('search') String? search,
    @Query('page') int? page = 0,
    @Query('size') int? size = 50,
  });

  /// Add team member.
  ///
  /// Add a member to the team. Requires ADMIN role on team.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/teams/{teamSlug}/members')
  Future<MemberDto> addMember({
    @Path('teamSlug') required String teamSlug,
    @Body() required AddMemberRequest body,
  });

  /// Join team.
  ///
  /// Request to join a team.
  ///
  /// [teamSlug] - Team URL slug.
  @POST('/api/teams/{teamSlug}/members/join')
  Future<MemberDto> joinTeam({
    @Path('teamSlug') required String teamSlug,
  });

  /// Leave team.
  ///
  /// Leave a team.
  ///
  /// [teamSlug] - Team URL slug.
  @POST('/api/teams/{teamSlug}/members/leave')
  Future<void> leaveTeam({
    @Path('teamSlug') required String teamSlug,
  });

  /// Update member role.
  ///
  /// Update a team member's role. Requires ADMIN role.
  ///
  /// [memberId] - Member user ID (TSID).
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @PUT('/api/teams/{teamSlug}/members/{memberId}')
  Future<MemberDto> updateMemberRole({
    @Path('memberId') required String memberId,
    @Path('teamSlug') required String teamSlug,
    @Body() required UpdateMemberRoleRequest body,
  });

  /// Remove team member.
  ///
  /// Remove a member from the team. Requires ADMIN role.
  ///
  /// [memberId] - Member user ID (TSID).
  ///
  /// [teamSlug] - Team URL slug.
  @DELETE('/api/teams/{teamSlug}/members/{memberId}')
  Future<void> removeMember({
    @Path('memberId') required String memberId,
    @Path('teamSlug') required String teamSlug,
  });
}
