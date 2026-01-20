package com.tribly.api.admin;

import com.tribly.dto.admin.AdminUserDto;
import com.tribly.dto.admin.AdminUserListResponse;
import com.tribly.dto.admin.AssignPlatformRoleRequest;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.repository.common.TriblyPage;
import com.tribly.service.admin.AdminUserService;
import com.tribly.service.security.annotation.Admin;
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
import org.jspecify.annotations.Nullable;

@Path("/api/admin/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Admin - Users", description = "Platform admin user management")
public class AdminUserResource {

  @Inject AdminUserService adminUserService;

  @GET
  @Admin
  @Operation(
      operationId = "listUsers",
      summary = "List all users",
      description = "Get a paginated list of all users")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Users retrieved successfully",
        content = @Content(schema = @Schema(implementation = AdminUserListResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listUsers(
      @Parameter(description = "Filter by domain ID") @QueryParam("domainId")
          @Nullable String domainId,
      @Parameter(description = "Search by email or name") @QueryParam("search")
          @Nullable String search,
      @Parameter(description = "Show only platform admins") @QueryParam("adminOnly")
          @Nullable Boolean adminOnly,
      @Parameter(description = "Page number (0-indexed)") @QueryParam("page") @DefaultValue("0")
          int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    TriblyPage<AdminUserDto> result =
        adminUserService.listUsers(domainId, search, adminOnly, page, size);
    AdminUserListResponse response =
        new AdminUserListResponse(result.items(), result.total(), page, size);
    return Response.ok(response).build();
  }

  @GET
  @Path("/{userId}")
  @Admin
  @Operation(
      operationId = "getAdminUser",
      summary = "Get user details",
      description = "Get detailed user information")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "User retrieved successfully",
        content = @Content(schema = @Schema(implementation = AdminUserDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getUser(@Parameter(description = "User ID") @PathParam("userId") String userId) {

    AdminUserDto user = adminUserService.getUser(userId);
    return Response.ok(user).build();
  }

  @PUT
  @Path("/{userId}/platform-role")
  @Admin
  @Operation(
      operationId = "assignPlatformRole",
      summary = "Assign platform role",
      description = "Assign or remove a platform role from a user")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Platform role updated successfully",
        content = @Content(schema = @Schema(implementation = AdminUserDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "User not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Forbidden - not a platform admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response assignPlatformRole(
      @Parameter(description = "User ID") @PathParam("userId") String userId,
      @Valid AssignPlatformRoleRequest request) {

    AdminUserDto user = adminUserService.assignPlatformRole(userId, request.role());
    return Response.ok(user).build();
  }
}
