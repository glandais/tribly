package fr.pedalons.api.admin;

import fr.pedalons.dto.admin.SocialBackfillResponse;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.service.admin.AdminSocialService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
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

@Path("/api/admin/social")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Admin - Social", description = "Platform admin social-identity operations")
public class AdminSocialResource {

  @Inject AdminSocialService adminSocialService;

  @POST
  @Path("/backfill-strava")
  @Operation(
      operationId = "backfillStravaIdentities",
      summary = "Backfill Strava identities",
      description =
          "Create social-identity rows for migrated placeholder Strava accounts so they can log in"
              + " with Strava. Idempotent.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Backfill completed",
        content = @Content(schema = @Schema(implementation = SocialBackfillResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response backfillStrava(
      @Parameter(description = "Target domain ID (defaults to the request's domain)")
          @QueryParam("domainId")
          @Nullable String domainId) {
    int created = adminSocialService.backfillStravaIdentities(domainId);
    return Response.ok(new SocialBackfillResponse(created)).build();
  }
}
