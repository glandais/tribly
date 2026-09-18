package fr.pedalons.api.notifications;

import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.notifications.request.NotificationPreferencesRequest;
import fr.pedalons.dto.notifications.response.NotificationListResponse;
import fr.pedalons.dto.notifications.response.NotificationPreferencesDto;
import fr.pedalons.dto.notifications.response.UnreadCountDto;
import fr.pedalons.service.notification.NotificationService;
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
 * The current user's notification inbox and preferences.
 *
 * <p>Every body here describes one user: {@code private, no-store}, as for the preferences of
 * {@code /api/users/me}.
 */
@Path("/api/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Notifications", description = "Notification inbox and preferences")
public class NotificationResource {

  private static final String PRIVATE = "private, no-store";

  @Inject NotificationService notificationService;

  @GET
  @Operation(
      operationId = "listMyNotifications",
      summary = "List my notifications",
      description =
          "The current user's notifications, newest first. Each carries a type and structured"
              + " fields, not rendered text: the client words it in its own language.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "A page of notifications",
        content = @Content(schema = @Schema(implementation = NotificationListResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response list(
      @Parameter(description = "Page number (0-indexed)") @QueryParam("page") @DefaultValue("0")
          int page,
      @Parameter(description = "Page size (max 200)") @QueryParam("size") @DefaultValue("20")
          int size,
      @Parameter(description = "Only unread notifications")
          @QueryParam("unreadOnly")
          @DefaultValue("false")
          boolean unreadOnly) {
    return Response.ok(notificationService.list(page, size, unreadOnly))
        .header(HttpHeaders.CACHE_CONTROL, PRIVATE)
        .build();
  }

  @GET
  @Path("/unread-count")
  @Operation(
      operationId = "countMyUnreadNotifications",
      summary = "Count my unread notifications",
      description =
          "The badge on the bell. Cheap by design: clients poll it (on focus, at most once a"
              + " minute) rather than reloading the list.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Unread count",
        content = @Content(schema = @Schema(implementation = UnreadCountDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response unreadCount() {
    return Response.ok(notificationService.unreadCount())
        .header(HttpHeaders.CACHE_CONTROL, PRIVATE)
        .build();
  }

  @POST
  @Path("/{notificationId}/read")
  @Operation(
      operationId = "markNotificationRead",
      summary = "Mark a notification read",
      description = "Idempotent: marking an already-read notification read succeeds.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Marked read"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "No such notification for the current user",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response markRead(@PathParam("notificationId") String notificationId) {
    notificationService.markRead(notificationId);
    return Response.noContent().build();
  }

  @POST
  @Path("/read-all")
  @Operation(operationId = "markAllNotificationsRead", summary = "Mark all my notifications read")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "All marked read"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response markAllRead() {
    notificationService.markAllRead();
    return Response.noContent().build();
  }

  @GET
  @Path("/preferences")
  @Operation(
      operationId = "getMyNotificationPreferences",
      summary = "Get my notification preferences",
      description =
          "Every notification type on every channel this server can deliver on. The inbox is not"
              + " listed: it always receives everything.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "The preference matrix",
        content = @Content(schema = @Schema(implementation = NotificationPreferencesDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getPreferences() {
    return Response.ok(notificationService.getPreferences())
        .header(HttpHeaders.CACHE_CONTROL, PRIVATE)
        .build();
  }

  @PUT
  @Path("/preferences")
  @Operation(
      operationId = "updateMyNotificationPreferences",
      summary = "Update my notification preferences",
      description = "A partial update: only the cells sent change. The full matrix is returned.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Preferences updated",
        content = @Content(schema = @Schema(implementation = NotificationPreferencesDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request (e.g. an attempt to switch the IN_APP channel off)",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updatePreferences(@Valid NotificationPreferencesRequest request) {
    return Response.ok(notificationService.updatePreferences(request))
        .header(HttpHeaders.CACHE_CONTROL, PRIVATE)
        .build();
  }
}
