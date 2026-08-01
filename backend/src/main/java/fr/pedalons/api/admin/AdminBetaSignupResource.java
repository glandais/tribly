package fr.pedalons.api.admin;

import fr.pedalons.dto.beta.BetaSignupDto;
import fr.pedalons.dto.beta.BetaSignupListResponse;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.service.beta.BetaSignupService;
import fr.pedalons.service.security.annotation.Admin;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/admin/beta-signups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Admin - Beta signups", description = "Beta program sign-ups, for platform admins")
public class AdminBetaSignupResource {

  @Inject BetaSignupService betaSignupService;

  @GET
  @Admin
  @Operation(
      operationId = "listBetaSignups",
      summary = "List beta sign-ups",
      description = "Get a paginated list of everyone who signed up to be notified about a beta")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Sign-ups retrieved successfully",
        content = @Content(schema = @Schema(implementation = BetaSignupListResponse.class)))
  })
  public Response listBetaSignups(
      @Parameter(description = "Page number (0-indexed)") @QueryParam("page") @DefaultValue("0")
          int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {
    PedalonsPage<BetaSignupDto> result = betaSignupService.listSignups(page, size);
    return Response.ok(new BetaSignupListResponse(result.items(), result.total(), page, size))
        .build();
  }
}
