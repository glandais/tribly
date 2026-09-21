package fr.pedalons.api.teams;

import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.teams.request.TeamWebhookRequest;
import fr.pedalons.dto.teams.response.TeamWebhookDto;
import fr.pedalons.dto.teams.response.TeamWebhookTestDto;
import fr.pedalons.service.notification.TeamWebhookService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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

/**
 * A team's outgoing webhook: its announcements posted to Slack, Discord or any HTTPS endpoint.
 * Administrators only — the right to edit the team's settings.
 */
@Path("/api/teams/{teamSlug}/webhook")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Team Webhook", description = "Post a team's announcements to a chat channel")
public class TeamWebhookResource {

  private static final String PRIVATE = "private, no-store";

  @Inject TeamWebhookService webhookService;

  @GET
  @Operation(
      operationId = "getTeamWebhook",
      summary = "Get the team's webhook",
      description =
          "The webhook with its URL masked — the URL is a secret. `configured` is false when the"
              + " team has none.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "The webhook",
        content = @Content(schema = @Schema(implementation = TeamWebhookDto.class))),
    @APIResponse(
        responseCode = "403",
        description = "Not an administrator of the team",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response get(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug) {
    return Response.ok(webhookService.get(teamSlug))
        .header(HttpHeaders.CACHE_CONTROL, PRIVATE)
        .build();
  }

  @PUT
  @Operation(
      operationId = "saveTeamWebhook",
      summary = "Create or change the team's webhook",
      description =
          "The message format is read from the URL: Slack, Discord, or a structured JSON document"
              + " for anything else. Only https URLs to public addresses are accepted. Omitting"
              + " the URL keeps the current one.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "The webhook as saved",
        content = @Content(schema = @Schema(implementation = TeamWebhookDto.class))),
    @APIResponse(
        responseCode = "400",
        description =
            "URL missing, not https, or pointing at a private address (WEBHOOK_URL_INVALID)",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Not an administrator of the team",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response save(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Valid TeamWebhookRequest request) {
    return Response.ok(webhookService.save(teamSlug, request))
        .header(HttpHeaders.CACHE_CONTROL, PRIVATE)
        .build();
  }

  @DELETE
  @Operation(operationId = "deleteTeamWebhook", summary = "Remove the team's webhook")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Removed, or there was none"),
    @APIResponse(
        responseCode = "403",
        description = "Not an administrator of the team",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response delete(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug) {
    webhookService.delete(teamSlug);
    return Response.noContent().build();
  }

  @POST
  @Path("/test")
  @Operation(
      operationId = "testTeamWebhook",
      summary = "Send a test message to the team's webhook",
      description = "Posts a test message now and reports how the endpoint answered.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "The endpoint's answer",
        content = @Content(schema = @Schema(implementation = TeamWebhookTestDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "The team has no webhook (WEBHOOK_URL_INVALID)",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Not an administrator of the team",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response test(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug) {
    return Response.ok(webhookService.test(teamSlug)).build();
  }
}
