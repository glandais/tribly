package fr.pedalons.api.users;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.users.request.UpdateUserRequest;
import fr.pedalons.dto.users.response.PublicUserDto;
import fr.pedalons.dto.users.response.UserDto;
import fr.pedalons.dto.users.response.UserExportDto;
import fr.pedalons.service.user.UserAvatarService;
import fr.pedalons.service.user.UserExportService;
import fr.pedalons.service.user.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.FileInputStream;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.jspecify.annotations.Nullable;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Tag(name = "Users", description = "User profile management operations")
public class UserResource {

  @Inject UserService userService;

  @Inject UserAvatarService userAvatarService;

  @Inject UserExportService userExportService;

  @GET
  @Path("/me")
  @Operation(
      summary = "Get current user",
      description = "Get the current authenticated user's profile.")
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
  public Response getMe() {
    UserDto userDto = userService.getUserDto();
    return Response.ok(userDto).build();
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
  public Response updateMe(@Valid UpdateUserRequest request) {
    UserDto userDto = userService.updateUser(request);
    return Response.ok(userDto).build();
  }

  @POST
  @Path("/me/avatar")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Operation(
      summary = "Upload user avatar",
      description =
          "Upload a new avatar image for the current user. Image will be resized to 256x256.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Avatar uploaded successfully",
        content = @Content(schema = @Schema(implementation = UserDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid image file",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response uploadAvatar(@RestForm("file") @Nullable FileUpload fileUpload) throws Exception {
    if (fileUpload == null || fileUpload.filePath() == null) {
      throw new BusinessException(ErrorCode.FILE_REQUIRED);
    }

    userAvatarService.uploadAvatar(
        new FileInputStream(fileUpload.filePath().toFile()), fileUpload.fileName());

    UserDto userDto = userService.getUserDto();
    return Response.ok(userDto).build();
  }

  @DELETE
  @Path("/me/avatar")
  @Operation(summary = "Delete user avatar", description = "Remove the current user's avatar")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Avatar deleted successfully",
        content = @Content(schema = @Schema(implementation = UserDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response deleteAvatar() {
    userAvatarService.deleteAvatar();
    UserDto userDto = userService.getUserDto();
    return Response.ok(userDto).build();
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

  @POST
  @Path("/me/export")
  // The request carries no body, so no Content-Type is sent either — without this the class-level
  // @Consumes(APPLICATION_JSON) makes RESTEasy answer 415.
  @Consumes(MediaType.WILDCARD)
  @Operation(
      summary = "Request a personal data export",
      description =
          "Queue a GDPR export of the current user's data. The archive is built in the background"
              + " and a download link is emailed when it is ready. Limited to one export per hour.")
  @APIResponses({
    @APIResponse(
        responseCode = "202",
        description = "Export queued",
        content = @Content(schema = @Schema(implementation = UserExportDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "429",
        description = "An export is already running, or the last one was too recent",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response requestExport() {
    UserExportDto export = userExportService.requestExport();
    return Response.accepted(export).build();
  }

  @GET
  @Path("/me/export")
  @Operation(
      summary = "Get the latest data export",
      description = "Status of the current user's most recent export request, if any.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Latest export status",
        content = @Content(schema = @Schema(implementation = UserExportDto.class))),
    @APIResponse(responseCode = "204", description = "The user has never requested an export"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getLatestExport() {
    return userExportService
        .getLatestExport()
        .map(export -> Response.ok(export).build())
        .orElseGet(() -> Response.noContent().build());
  }

  @GET
  @Path("/me/export/{exportId}")
  @Operation(
      summary = "Get a data export",
      description = "Status of one of the current user's export requests.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Export status",
        content = @Content(schema = @Schema(implementation = UserExportDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "No such export for this user",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getExport(
      @Parameter(description = "Export job identifier", required = true) @PathParam("exportId")
          String exportId) {
    return Response.ok(userExportService.getExport(exportId)).build();
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

    userService.deleteUser();

    return Response.noContent().build();
  }
}
