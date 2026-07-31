// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint, unused_import, invalid_annotation_target, unnecessary_import

import 'package:dio/dio.dart' hide Headers;
import 'package:retrofit/retrofit.dart';
import 'package:retrofit/error_logger.dart';

import '../models/create_invitation_request.dart';
import '../models/invitation_status.dart';
import '../models/team_invitation_dto.dart';
import '../models/team_invitation_list_response.dart';

part 'team_invitations_client.g.dart';

@RestApi()
abstract class TeamInvitationsClient {
  factory TeamInvitationsClient(Dio dio, {String? baseUrl}) =
      _TeamInvitationsClient;

  /// List a team's invitations.
  ///
  /// Administrators only. This is where a mistyped address shows up: since the creation call cannot tell the admin that an address is unknown, the pending list is what makes a typo visible and revocable.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [page] - Page number.
  ///
  /// [size] - Page size.
  ///
  /// [status] - Filter by status.
  @GET('/api/teams/{teamSlug}/invitations')
  Future<TeamInvitationListResponse> listInvitations({
    @Path('teamSlug') required String teamSlug,
    @Query('status') InvitationStatus? status,
    @Query('page') int? page = 0,
    @Query('size') int? size = 20,
  });

  /// Invite an e-mail address.
  ///
  /// Sends an invitation to join the team. The response is the same whether or not an account already exists for the address — only the wording of the e-mail differs — because answering differently would give anyone who administers a team an account-existence probe for the whole domain. Re-inviting the same address is not an error: the previous pending invitation is revoked and a fresh one issued, which is how 'resend' works. Nobody becomes a member until they accept.
  ///
  /// [teamSlug] - Team URL slug.
  ///
  /// [body] - Name not received - field will be skipped.
  @POST('/api/teams/{teamSlug}/invitations')
  Future<TeamInvitationDto> invite({
    @Path('teamSlug') required String teamSlug,
    @Body() required CreateInvitationRequest body,
  });

  /// Revoke an invitation.
  ///
  /// Withdraws a pending invitation. This is also the recourse when an invitation should no longer stand — the inviter having left the team, say — since acceptance does not re-check who sent it.
  ///
  /// [invitationId] - Invitation ID (TSID).
  ///
  /// [teamSlug] - Team URL slug.
  @DELETE('/api/teams/{teamSlug}/invitations/{invitationId}')
  Future<void> revoke({
    @Path('invitationId') required String invitationId,
    @Path('teamSlug') required String teamSlug,
  });
}
