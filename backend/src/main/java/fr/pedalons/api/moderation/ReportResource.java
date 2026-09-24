package fr.pedalons.api.moderation;

import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.moderation.request.ReportRequest;
import fr.pedalons.service.moderation.ReportService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/** Reporting content or a member to the team's moderators (App Store guideline 1.2). */
@Path("/api/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Moderation", description = "Reports, moderation queues and blocks")
public class ReportResource {

  @Inject ReportService reportService;

  @POST
  @Operation(
      operationId = "reportContent",
      summary = "Report content or a member",
      description =
          "Files a report in a team, about a comment, a publication, an ad, a route or a member the"
              + " caller can read there. Idempotent: reporting the same target again changes"
              + " nothing. The target disappears from the caller's lists at once; the team's"
              + " organizers and administrators are notified, without the caller's name.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Reported"),
    @APIResponse(
        responseCode = "400",
        description =
            "Invalid request, or REPORT_SELF: the caller reported themselves or their own"
                + " content",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "No such team, or a target the caller cannot read",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response report(@Valid ReportRequest request) {
    reportService.report(request);
    return Response.noContent().build();
  }
}
