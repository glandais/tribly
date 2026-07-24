package fr.pedalons.api.config;

import fr.pedalons.dto.config.VersionDto;
import fr.pedalons.service.config.VersionService;
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

@Path("/api/version")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
// Tag name drives the generated client names. Not "Version": the mobile generator already puts a
// `static String get version` (the contract's info.version) on its root client, and an instance
// getter of the same name is a Dart compile error.
@Tag(name = "Server Version", description = "Server version and build information")
public class VersionResource {

  @Inject VersionService versionService;

  @GET
  @Operation(
      summary = "Get server version",
      description =
          "Get the API contract version the server implements, plus the git commit and build time"
              + " it was built from")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Version retrieved successfully",
        content = @Content(schema = @Schema(implementation = VersionDto.class)))
  })
  public Response getVersion() {
    return Response.ok(versionService.getVersion()).build();
  }
}
