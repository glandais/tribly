package fr.pedalons.api.notifications;

import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.notifications.request.PushDeviceRegistration;
import fr.pedalons.service.notification.PushDeviceService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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

/**
 * The current user's push devices — the addresses the {@code PUSH} channel delivers to.
 *
 * <p>Lives beside the inbox rather than under {@code /api/device}, which is the device-code OAuth
 * flow of the head units and has nothing to do with notifications.
 */
@Path("/api/push-devices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Notifications", description = "Notification inbox and preferences")
public class PushDeviceResource {

  @Inject PushDeviceService pushDeviceService;

  @POST
  @Operation(
      operationId = "registerPushDevice",
      summary = "Register a device for push notifications",
      description =
          "Called at every app launch and whenever FCM rotates the token. Idempotent: a token"
              + " already known is refreshed, and moved to the current user if it was someone"
              + " else's.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Device registered"),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response register(@Valid PushDeviceRegistration registration) {
    pushDeviceService.register(registration);
    return Response.noContent().build();
  }

  @DELETE
  @Path("/{token}")
  @Operation(
      operationId = "unregisterPushDevice",
      summary = "Stop sending push notifications to a device",
      description =
          "Called on sign-out. Idempotent, and silent about tokens that are not the caller's.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Device unregistered"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response unregister(
      @Parameter(description = "The FCM registration token to drop") @PathParam("token")
          String token) {
    pushDeviceService.unregister(token);
    return Response.noContent().build();
  }
}
