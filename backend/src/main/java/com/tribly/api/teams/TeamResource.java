package com.tribly.api.teams;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.common.request.SlugChangeRequest;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.teams.request.TeamRequest;
import com.tribly.dto.teams.response.TeamDetailDto;
import com.tribly.dto.teams.response.TeamListResponse;
import com.tribly.service.team.TeamService;
import com.tribly.service.team.request.MinRole;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
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
      @Parameter(description = "Minimum role in team") @QueryParam(value = "minRole")
          @Nullable MinRole minRole,
      @Parameter(description = "Page number (0-indexed)") @QueryParam("page") @DefaultValue("0")
          int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    User user = getCurrentUserOrNull();
    TeamListResponse teams =
        teamService.listTeams(
            user, minRole == null ? MinRole.NOT_MEMBER : minRole, search, page, size);
    return Response.ok(teams).build();
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
    User user = getCurrentUserOrNull();
    Team team = teamService.getTeam(slug);
    TeamDetailDto teamDetailDto = teamService.getTeamDetailDto(team, user);
    return Response.ok(teamDetailDto).build();
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
  public Response createTeam(@Valid TeamRequest request) {
    User user = getCurrentUser();

    TeamDetailDto teamDetailDto = teamService.createTeam(request, user);

    return Response.created(URI.create("/api/teams/" + teamDetailDto.slug()))
        .entity(teamDetailDto)
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
      @Valid TeamRequest request) {
    User user = getCurrentUser();
    Team team = teamService.getTeam(slug);

    TeamDetailDto teamDetailDto = teamService.updateTeam(team, request, user);

    return Response.ok(teamDetailDto).build();
  }

  @PATCH
  @Path("/{slug}/slug")
  @RolesAllowed("user")
  @Operation(
      operationId = "changeTeamSlug",
      summary = "Change team slug",
      description = "Change team URL slug. Requires ADMIN role.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Slug changed successfully",
        content = @Content(schema = @Schema(implementation = TeamDetailDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid slug format",
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
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "409",
        description = "Slug already in use",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response changeSlug(
      @Parameter(description = "Current team URL slug") @PathParam("slug") String currentSlug,
      @Valid SlugChangeRequest request) {
    User user = getCurrentUser();
    Team team = teamService.getTeam(currentSlug);
    TeamDetailDto teamDetailDto = teamService.updateSlug(team, request.slug(), user);
    return Response.ok(teamDetailDto).build();
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
    User user = getCurrentUser();
    Team team = teamService.getTeam(slug);
    teamService.deleteTeam(team, user);
    return Response.noContent().build();
  }
}
