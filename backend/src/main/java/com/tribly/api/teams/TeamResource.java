package com.tribly.api.teams;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.api.dto.ErrorResponse;
import com.tribly.domain.common.TriblyPage;
import com.tribly.service.team.TeamAndRole;
import com.tribly.service.team.TeamService;
import jakarta.annotation.security.PermitAll;
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
import org.jspecify.annotations.Nullable;

@Path("/api/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Teams", description = "Team management operations")
public class TeamResource extends AbstractAuthenticatedResource {

  @Inject TeamService teamService;

  @GET
  @PermitAll
  @Operation(
      summary = "List public teams",
      description = "Get a paginated list of public teams with optional search")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Teams retrieved successfully",
        content = @Content(schema = @Schema(implementation = TeamListResponse.class)))
  })
  public Response listTeams(
      @Parameter(description = "Search query to filter teams by name") @QueryParam(value = "search")
          @Nullable String search,
      @Parameter(description = "Membership (false : public not member, true : my teams)")
          @QueryParam(value = "member")
          @Nullable Boolean member,
      @Parameter(description = "Page number (0-indexed)") @QueryParam("page") @DefaultValue("0")
          int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    Long userId = getCurrentUserIdOrNull();
    TriblyPage<TeamAndRole> teams = teamService.listTeams(userId, member, search, page, size);

    List<TeamDetailDto> dtos = teams.items().stream().map(TeamDetailDto::from).toList();
    return Response.ok(new TeamListResponse(dtos, teams.total(), page, size)).build();
  }

  @GET
  @Path("/{slug}")
  @PermitAll
  @Operation(
      summary = "Get team by slug",
      description = "Get detailed team information by URL slug")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Team retrieved successfully",
        content = @Content(schema = @Schema(implementation = TeamDetailDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Team is private and user is not a member",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getTeam(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug) {
    Long userId = getCurrentUserIdOrNull();
    TeamAndRole teamAndRole = teamService.getTeam(slug, userId);
    return Response.ok(TeamDetailDto.from(teamAndRole)).build();
  }

  @POST
  @RolesAllowed("user")
  @Operation(
      summary = "Create team",
      description = "Create a new team. The current user will be set as the team owner.")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Team created successfully",
        content = @Content(schema = @Schema(implementation = TeamDetailDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response createTeam(@Valid CreateTeamRequest request) {
    Long userId = getCurrentUserId();

    TeamAndRole teamAndRole =
        teamService.createTeam(
            new com.tribly.service.team.CreateTeamRequest(
                request.name(), request.description(), request.visibility(), request.maxMembers()),
            userId);

    return Response.created(URI.create("/api/teams/" + teamAndRole.team().getSlug()))
        .entity(TeamDetailDto.from(teamAndRole))
        .build();
  }

  @PUT
  @Path("/{slug}")
  @RolesAllowed("user")
  @Transactional
  @Operation(summary = "Update team", description = "Update team information. Requires ADMIN role.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Team updated successfully",
        content = @Content(schema = @Schema(implementation = TeamDetailDto.class))),
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
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updateTeam(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Valid UpdateTeamRequest request) {
    Long userId = getCurrentUserId();

    TeamAndRole updated =
        teamService.updateTeam(
            slug,
            new com.tribly.service.team.UpdateTeamRequest(
                request.name(),
                request.description(),
                request.visibility(),
                request.logoUrl(),
                request.coverImageUrl(),
                request.maxMembers()),
            userId);

    return Response.ok(TeamDetailDto.from(updated)).build();
  }

  @DELETE
  @Path("/{slug}")
  @RolesAllowed("user")
  @Operation(summary = "Delete team", description = "Soft delete a team. Requires OWNER role.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Team deleted successfully"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not the team owner",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response deleteTeam(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug) {
    Long userId = getCurrentUserId();
    teamService.deleteTeam(slug, userId);
    return Response.noContent().build();
  }
}
