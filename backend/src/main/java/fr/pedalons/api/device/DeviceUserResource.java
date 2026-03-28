package fr.pedalons.api.device;

import fr.pedalons.dto.device.response.DeviceUserStatusResponse;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.service.device.DeviceRouteService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/** User status API for device applications. Provides information about connected services. */
@Path("/api/device")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Device User", description = "User status for device applications")
@SecurityRequirement(name = "bearerAuth")
public class DeviceUserResource {

  @Inject DeviceRouteService deviceRouteService;

  @GET
  @Path("/me")
  @RolesAllowed("user")
  @Operation(
      operationId = "deviceGetMe",
      summary = "Get current user status",
      description = "Get authenticated user's status including connected GPS services")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "User status",
        content = @Content(schema = @Schema(implementation = DeviceUserStatusResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public DeviceUserStatusResponse getMe() {
    return deviceRouteService.getUserStatus();
  }
}
