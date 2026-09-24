package fr.pedalons.api.users;

import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.users.response.BlockedUsersResponse;
import fr.pedalons.service.moderation.UserBlockService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
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
 * The members the current user blocked. A block is domain-wide, one-way and silent: the blocked
 * member is never told.
 */
@Path("/api/users/me/blocks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Moderation", description = "Reports, moderation queues and blocks")
public class UserBlockResource {

  @Inject UserBlockService userBlockService;

  @GET
  @Operation(
      operationId = "listMyBlockedUsers",
      summary = "List the members I blocked",
      description = "Most recent first.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "The blocked members",
        content = @Content(schema = @Schema(implementation = BlockedUsersResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listMyBlockedUsers() {
    return Response.ok(userBlockService.listBlockedUsers())
        .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
        .build();
  }

  @PUT
  @Path("/{userId}")
  @Operation(
      operationId = "blockUser",
      summary = "Block a member",
      description =
          "Hides the member's comments, posts and ads from the caller, and the comment"
              + " notifications they cause. Idempotent.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Blocked"),
    @APIResponse(
        responseCode = "400",
        description = "BLOCK_SELF: the caller tried to block themselves",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "No such user on this site",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response blockUser(
      @Parameter(description = "User ID (TSID)") @PathParam("userId") String userId) {
    userBlockService.blockUser(userId);
    return Response.noContent().build();
  }

  @DELETE
  @Path("/{userId}")
  @Operation(operationId = "unblockUser", summary = "Unblock a member", description = "Idempotent.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Unblocked"),
    @APIResponse(
        responseCode = "400",
        description = "BLOCK_SELF: the caller named themselves",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "No such user on this site",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response unblockUser(
      @Parameter(description = "User ID (TSID)") @PathParam("userId") String userId) {
    userBlockService.unblockUser(userId);
    return Response.noContent().build();
  }
}
