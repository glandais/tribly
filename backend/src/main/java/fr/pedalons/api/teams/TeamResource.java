package fr.pedalons.api.teams;

import fr.pedalons.dto.common.request.SlugChangeRequest;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.teams.request.TeamRequest;
import fr.pedalons.dto.teams.response.TeamDetailDto;
import fr.pedalons.dto.teams.response.TeamListResponse;
import fr.pedalons.service.team.TeamService;
import fr.pedalons.service.team.request.MinRole;
import jakarta.annotation.security.PermitAll;
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
import org.jspecify.annotations.Nullable;

@Path("/api/teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Teams", description = "Team management operations")
public class TeamResource {

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
      @Parameter(
              description =
                  "Keep only teams that accept a join request from any domain user (true), or only"
                      + " those that do not (false). Omitted keeps both. A filter on top of the"
                      + " visibility rules, never instead of them.")
          @QueryParam("joinable")
          @Nullable Boolean joinable,
      @Parameter(description = "Page number (0-indexed)") @QueryParam("page") @DefaultValue("0")
          int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    TeamListResponse teams = teamService.listTeams(minRole, search, joinable, page, size);
    return Response.ok(teams).build();
  }

  @GET
  @Path("/{teamSlug}")
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
  @PermitAll
  public Response getTeam(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug) {

    TeamDetailDto teamDetailDto = teamService.getTeamDetailDto(teamSlug);
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
    TeamDetailDto teamDetailDto = teamService.createTeam(request);

    return Response.status(Response.Status.CREATED).entity(teamDetailDto).build();
  }

  @PUT
  @Path("/{teamSlug}")
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
  @RolesAllowed("user")
  public Response updateTeam(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Valid TeamRequest request) {
    TeamDetailDto teamDetailDto = teamService.updateTeam(teamSlug, request);

    return Response.ok(teamDetailDto).build();
  }

  @PATCH
  @Path("/{teamSlug}/slug")
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
  @RolesAllowed("user")
  public Response changeSlug(
      @Parameter(description = "Current team URL slug") @PathParam("teamSlug") String teamSlug,
      @Valid SlugChangeRequest request) {
    TeamDetailDto teamDetailDto = teamService.updateSlug(teamSlug, request.slug());
    return Response.ok(teamDetailDto).build();
  }

  @DELETE
  @Path("/{teamSlug}")
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
  @RolesAllowed("user")
  public Response deleteTeam(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug) {
    teamService.deleteTeam(teamSlug);
    return Response.noContent().build();
  }
}
