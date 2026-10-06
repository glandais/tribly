package fr.pedalons.api.teams;

import fr.pedalons.dto.dashboard.response.TeamDashboardDto;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.service.team.TeamDashboardService;
import jakarta.annotation.security.PermitAll;
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
  @PermitAll
  @Operation(
      operationId = "getTeamDashboard",
      summary = "Get the team dashboard",
      description =
          "Everything a « Tableau de bord » shows, in one call, graded by the caller's role. A"
              + " visitor (anonymous, or signed in without belonging to the team) gets the public"
              + " part only: upcoming rides, latest posts and new routes, under the usual"
              + " visibility rules (PUBLIC entities only), with role, myUpcoming, latestAds,"
              + " organizer and admin null. A member gets the member sections, an organizer the"
              + " organizer block too, an administrator the admin block too. Each section is a"
              + " short page of the matching list, and is null when the team has disabled its"
              + " module. The teams switcher, the unread notification count and the calendar token"
              + " are not part of it.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Dashboard built",
        content = @Content(schema = @Schema(implementation = TeamDashboardDto.class))),
    @APIResponse(
        responseCode = "403",
        description = "The team is visible to its members only and the caller is not one",
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
