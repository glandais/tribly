package fr.pedalons.api.teams;

import fr.pedalons.dto.dashboard.response.TeamDashboardDto;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.service.team.TeamDashboardService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/teams/{teamSlug}/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Teams", description = "Team management operations")
public class TeamDashboardResource {

  @Inject TeamDashboardService dashboardService;

  @GET
  @RolesAllowed("user")
  @Operation(
      operationId = "getTeamDashboard",
      summary = "Get the team dashboard",
      description =
          "Everything a member's « Tableau de bord » shows, in one call, graded by the caller's"
              + " role: the member sections for everyone, the organizer block for organizers and"
              + " administrators, the admin block for administrators. Each section is a short page"
              + " of the matching list, and is null when the team has disabled its module. The"
              + " teams switcher, the unread notification count and the calendar token are not"
              + " part of it.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Dashboard built",
        content = @Content(schema = @Schema(implementation = TeamDashboardDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "The caller is not a member of the team",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getTeamDashboard(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug) {
    TeamDashboardDto dashboard = dashboardService.getDashboard(teamSlug);
    // Every section depends on who is asking: never let a shared cache keep it.
    return Response.ok(dashboard).header(HttpHeaders.CACHE_CONTROL, "private, no-store").build();
  }
}
