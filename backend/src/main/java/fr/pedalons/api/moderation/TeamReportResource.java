package fr.pedalons.api.moderation;

import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.moderation.request.ModerationDecisionRequest;
import fr.pedalons.dto.moderation.response.ModerationQueueResponse;
import fr.pedalons.enums.ReportQueueStatus;
import fr.pedalons.service.moderation.ModerationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
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
import org.jspecify.annotations.Nullable;

/** A team's moderation queue, for its organizers and administrators. */
@Path("/api/teams/{teamSlug}/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Moderation", description = "Reports, moderation queues and blocks")
public class TeamReportResource {

  @Inject ModerationService moderationService;

  @GET
  @Operation(
      operationId = "listTeamReports",
      summary = "List the team's moderation queue",
      description =
          "Reports grouped by target, without the reporters' identities. OPEN lists every target"
              + " waiting for a decision; RESOLVED the 100 most recently decided. Reports about the"
              + " caller — their content, or themselves — are left out.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "The queue",
        content = @Content(schema = @Schema(implementation = ModerationQueueResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Not an organizer or administrator of the team",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "No such team",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listTeamReports(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "OPEN (default) or RESOLVED") @QueryParam("status")
          @Nullable ReportQueueStatus status) {
    return Response.ok(moderationService.teamQueue(teamSlug, status))
        .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
        .build();
  }

  @POST
  @Path("/resolve")
  @Operation(
      operationId = "resolveTeamReports",
      summary = "Decide about a reported target",
      description =
          "Applies the decision to every open report of the target. REMOVE_CONTENT deletes the"
              + " content (not allowed on a member); DISMISS keeps it, and shows it again if"
              + " reports had hidden it.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Decided"),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request, or REMOVE_CONTENT on a member",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Not an organizer or administrator of the team",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "No open report on this target in the caller's queue",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response resolveTeamReports(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Valid ModerationDecisionRequest request) {
    moderationService.resolveTeamReports(teamSlug, request);
    return Response.noContent().build();
  }
}
