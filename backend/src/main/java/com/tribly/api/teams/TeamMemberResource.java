package com.tribly.api.teams;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.api.dto.ErrorResponse;
import com.tribly.domain.common.TriblyPage;
import com.tribly.domain.team.TeamRole;
import com.tribly.domain.team.UserTeam;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.team.TeamMembershipService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/teams/{slug}/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Team Members", description = "Team membership management operations")
public class TeamMemberResource extends AbstractAuthenticatedResource {

  @Inject TeamMembershipService membershipService;

  @GET
  @Operation(summary = "Get team members", description = "Get paginated list of team members")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Members retrieved successfully",
        content = @Content(schema = @Schema(implementation = MemberListResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getMembers(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("50") int size) {

    Long userId = getCurrentUserId();

    TriblyPage<UserTeam> members = membershipService.getTeamMembers(slug, userId, page, size);

    List<MemberDto> dtos = members.items().stream().map(MemberDto::from).toList();
    return Response.ok(new MemberListResponse(dtos, members.total(), page, size)).build();
  }

  @POST
  @Path("/join")
  @Operation(summary = "Join team", description = "Request to join a team")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Successfully joined team",
        content = @Content(schema = @Schema(implementation = MemberDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Team is full or user is already a member",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response joinTeam(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug) {
    Long userId = getCurrentUserId();

    UserTeam membership = membershipService.joinTeam(slug, userId);
    return Response.created(URI.create("/api/teams/" + slug + "/members/" + userId))
        .entity(MemberDto.from(membership))
        .build();
  }

  @POST
  @Path("/leave")
  @Operation(summary = "Leave team", description = "Leave a team")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Successfully left team"),
    @APIResponse(
        responseCode = "400",
        description = "Cannot leave - user is the owner",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found or user is not a member",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response leaveTeam(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug) {
    Long userId = getCurrentUserId();

    membershipService.leaveTeam(slug, userId);
    return Response.noContent().build();
  }

  @POST
  @Operation(
      summary = "Add team member",
      description = "Add a member to the team. Requires ADMIN role on team.")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Member added successfully",
        content = @Content(schema = @Schema(implementation = MemberDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request or user already a member",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not a team admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or user not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  public Response addMember(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Valid AddMemberRequest request) {
    Long actingUserId = getCurrentUserId();

    TeamRole role = request.role() != null ? request.role() : TeamRole.MEMBER;
    Long targetUserId = TsidUtils.toLong(request.userId());
    UserTeam membership = membershipService.addMember(slug, targetUserId, role, actingUserId);

    return Response.created(URI.create("/api/teams/" + slug + "/members/" + request.userId()))
        .entity(MemberDto.from(membership))
        .build();
  }

  @PUT
  @Path("/{memberId}")
  @Transactional
  @Operation(
      summary = "Update member role",
      description = "Update a team member's role. Requires ADMIN role.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Role updated successfully",
        content = @Content(schema = @Schema(implementation = MemberDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not a team admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or member not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updateMemberRole(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Member user ID (TSID)") @PathParam("memberId") String memberId,
      @Valid UpdateMemberRoleRequest request) {

    Long actingUserId = getCurrentUserId();

    UserTeam membership =
        membershipService.updateMemberRole(
            slug, TsidUtils.toLong(memberId), request.role(), actingUserId);
    return Response.ok(MemberDto.from(membership)).build();
  }

  @DELETE
  @Path("/{memberId}")
  @Operation(
      summary = "Remove team member",
      description = "Remove a member from the team. Requires ADMIN role.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Member removed successfully"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not a team admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or member not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response removeMember(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Member user ID (TSID)") @PathParam("memberId") String memberId) {

    Long actingUserId = getCurrentUserId();

    membershipService.removeMember(slug, TsidUtils.toLong(memberId), actingUserId);
    return Response.noContent().build();
  }
}
