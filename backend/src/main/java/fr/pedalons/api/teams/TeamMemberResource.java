package fr.pedalons.api.teams;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.teams.request.AddMemberRequest;
import fr.pedalons.dto.teams.request.UpdateMemberRoleRequest;
import fr.pedalons.dto.teams.response.MemberDto;
import fr.pedalons.dto.teams.response.MemberListResponse;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.service.team.TeamMembershipService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/teams/{teamSlug}/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Team Members", description = "Team membership management operations")
public class TeamMemberResource {

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
  @RolesAllowed("user")
  public Response getMembers(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("50") int size) {

    MemberListResponse members = membershipService.getTeamMembers(teamSlug, page, size);
    return Response.ok(members).build();
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
  @RolesAllowed("user")
  public Response joinTeam(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug) {

    MemberDto membership = membershipService.joinTeam(teamSlug);
    return Response.status(Response.Status.CREATED).entity(membership).build();
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
  @RolesAllowed("user")
  public Response leaveTeam(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug) {

    membershipService.leaveTeam(teamSlug);
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
  @RolesAllowed("user")
  public Response addMember(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Valid AddMemberRequest request) {
    TeamRole role = request.role() != null ? request.role() : TeamRole.MEMBER;
    Long targetUserId = TsidUtils.toLong(request.userId());
    MemberDto membership = membershipService.addMember(teamSlug, targetUserId, role);

    return Response.status(Response.Status.CREATED).entity(membership).build();
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
  @RolesAllowed("user")
  public Response updateMemberRole(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Member user ID (TSID)") @PathParam("memberId") String memberId,
      @Valid UpdateMemberRoleRequest request) {
    MemberDto membership =
        membershipService.updateMemberRole(teamSlug, TsidUtils.toLong(memberId), request.role());
    return Response.ok(membership).build();
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
  @RolesAllowed("user")
  public Response removeMember(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Member user ID (TSID)") @PathParam("memberId") String memberId) {
    membershipService.removeMember(teamSlug, TsidUtils.toLong(memberId));
    return Response.noContent().build();
  }
}
