package com.tribly.api.users;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.users.request.UpdateUserRequest;
import com.tribly.dto.users.response.PublicUserDto;
import com.tribly.dto.users.response.UserDto;
import com.tribly.infrastructure.id.TsidUtils;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Users", description = "User profile management operations")
public class UserResource extends AbstractAuthenticatedResource {

  private static final Logger LOG = Logger.getLogger(UserResource.class);

  @GET
  @Path("/me")
  @Operation(
      summary = "Get current user",
      description = "Get the current authenticated user's profile")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "User profile retrieved successfully",
        content = @Content(schema = @Schema(implementation = UserDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getCurrentUser() {
    Long userId = getCurrentUserId();
    UserDto user = userService.getUserDto(userId);
    return Response.ok(user).build();
  }

  @PUT
  @Path("/me")
  @Operation(summary = "Update current user", description = "Update the current user's profile")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "User updated successfully",
        content = @Content(schema = @Schema(implementation = UserDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updateCurrentUser(@Valid UpdateUserRequest request) {
    Long userId = getCurrentUserId();
    UserDto user =
        userService.updateUser(userId, request.displayName(), request.locale(), request.timezone());
    return Response.ok(user).build();
  }

  @GET
  @Path("/{id}")
  @Operation(summary = "Get user by ID", description = "Get public user information by ID")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "User retrieved successfully",
        content = @Content(schema = @Schema(implementation = PublicUserDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getUserById(
      @Parameter(description = "User ID (TSID)") @PathParam("id") String id) {
    Long userId = TsidUtils.toLong(id);
    PublicUserDto user = userService.getPublicUserDto(userId);
    return Response.ok(user).build();
  }

  @GET
  @Path("/search")
  @Operation(summary = "Search users", description = "Search users by display name")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Search results",
        content = @Content(schema = @Schema(implementation = PublicUserDto[].class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response searchUsers(
      @Parameter(description = "Search query") @QueryParam("q") @Nullable String query,
      @Parameter(description = "Maximum results (max 20)") @QueryParam("limit") @DefaultValue("10")
          int limit) {
    if (query == null || query.trim().isEmpty()) {
      return Response.ok(List.of()).build();
    }

    List<PublicUserDto> users = userService.searchByDisplayName(query.trim(), Math.min(limit, 20));
    return Response.ok(users).build();
  }

  @DELETE
  @Path("/me")
  @Operation(summary = "Delete current user", description = "Delete the current user's account")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "User deleted successfully"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response deleteCurrentUser() {
    Long userId = getCurrentUserId();
    userService.deleteUser(userId);

    LOG.infov("User {0} deleted their account", userId);
    return Response.noContent().build();
  }
}
