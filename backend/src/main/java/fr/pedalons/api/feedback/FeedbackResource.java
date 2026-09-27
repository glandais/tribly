package fr.pedalons.api.feedback;

import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.feedback.request.ErrorReportRequest;
import fr.pedalons.dto.feedback.request.FeedbackRequest;
import fr.pedalons.service.feedback.FeedbackService;
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
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/** Bug reports and suggestions from the members, and the clients' unhandled errors. */
@Path("/api/feedback")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Feedback", description = "Bug reports, suggestions and automatic error reports")
public class FeedbackResource {

  @Inject FeedbackService feedbackService;

  @POST
  @Operation(
      operationId = "sendFeedback",
      summary = "Report a bug or suggest something",
      description =
          "Files the member's report with the technical context their client attached. It reaches"
              + " the maintainers as an issue of a private repository, naming the member by id"
              + " only; tokens and e-mail addresses are redacted from the context and the log.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Received"),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "429",
        description = "FEEDBACK_RATE_LIMITED: too many reports lately",
        headers =
            @Header(
                name = "Retry-After",
                description = "Seconds before trying again",
                schema = @Schema(type = SchemaType.INTEGER)),
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response sendFeedback(@Valid FeedbackRequest request) {
    feedbackService.submit(request);
    return Response.noContent().build();
  }

  @POST
  @Path("/errors")
  @Operation(
      operationId = "reportClientError",
      summary = "Report an unhandled error",
      description =
          "Sent by a client, without the member's intervention, when it catches an unhandled"
              + " error. The same error reported by many clients becomes one issue. Always 204"
              + " once valid, including past the per-member quota, where it is dropped: a client"
              + " must never retry nor surface this call's failure.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Received, or dropped past the quota"),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response reportClientError(@Valid ErrorReportRequest request) {
    feedbackService.reportError(request);
    return Response.noContent().build();
  }
}
