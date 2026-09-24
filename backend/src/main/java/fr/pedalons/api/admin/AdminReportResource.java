package fr.pedalons.api.admin;

import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.moderation.request.ModerationDecisionRequest;
import fr.pedalons.dto.moderation.response.ModerationQueueResponse;
import fr.pedalons.enums.ReportQueueStatus;
import fr.pedalons.service.moderation.ModerationService;
import fr.pedalons.service.security.annotation.Admin;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
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

/** The platform's moderation queue: every team of the domain, reporters included. */
@Path("/api/admin/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Admin - Reports", description = "Platform moderation queue")
public class AdminReportResource {

  @Inject ModerationService moderationService;

  @GET
  @Admin
  @Operation(
      operationId = "listAdminReports",
      summary = "List the platform moderation queue",
      description =
          "Reports of every team of the domain, grouped by target, with the reporters. OPEN lists"
              + " every target waiting for a decision; RESOLVED the 100 most recently decided.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "The queue",
        content = @Content(schema = @Schema(implementation = ModerationQueueResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listAdminReports(
      @Parameter(description = "OPEN (default) or RESOLVED") @QueryParam("status")
          @Nullable ReportQueueStatus status) {
    return Response.ok(moderationService.platformQueue(status))
        .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
        .build();
  }

  @POST
  @Path("/resolve")
  @Admin
  @Operation(
      operationId = "resolveAdminReports",
      summary = "Decide about a reported target, in any team",
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
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "No open report on this target",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response resolveAdminReports(@Valid ModerationDecisionRequest request) {
    moderationService.resolvePlatformReports(request);
    return Response.noContent().build();
  }
}
