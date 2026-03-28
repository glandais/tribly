package fr.pedalons.api.config;

import fr.pedalons.dto.config.ConfigDto;
import fr.pedalons.service.config.ConfigService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
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

@Path("/api/config")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@Tag(name = "Configuration", description = "Application configuration endpoints")
public class ConfigResource {

  @Inject ConfigService configService;

  @GET
  @Operation(
      summary = "Get application configuration",
      description = "Get frontend configuration including auth and app settings")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Configuration retrieved successfully",
        content = @Content(schema = @Schema(implementation = ConfigDto.class)))
  })
  public Response getConfig() {
    return Response.ok(configService.getConfig()).build();
  }
}
