package fr.pedalons.api.users;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.publications.response.PublicationListResponse;
import fr.pedalons.dto.users.request.UpdateUserRequest;
import fr.pedalons.dto.users.request.UserPreferencesRequest;
import fr.pedalons.dto.users.response.PublicUserDto;
import fr.pedalons.dto.users.response.UserDto;
import fr.pedalons.dto.users.response.UserExportDto;
import fr.pedalons.enums.ListViewMode;
import fr.pedalons.enums.Status;
import fr.pedalons.service.common.PublicationService;
import fr.pedalons.service.user.UserAvatarService;
import fr.pedalons.service.user.UserExportService;
import fr.pedalons.service.user.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.FileInputStream;
import java.time.Instant;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
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

  @Inject PublicationService publicationService;

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

  @PATCH
  @Path("/me/preferences")
  @Operation(
      operationId = "updateMyPreferences",
      summary = "Update display preferences",
      description =
          "Set the current user's unit system, colour scheme and language. A partial update: fields"
              + " omitted (or sent null) are left unchanged. These live on the server so that a"
              + " member who picks imperial units on their phone sees them on the web too, and so"
              + " that reinstalling the app does not lose them.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Preferences updated; the full, refreshed profile is returned",
        content = @Content(schema = @Schema(implementation = UserDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request (e.g. a language that is not a BCP-47 tag)",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updateMyPreferences(@Valid UserPreferencesRequest request) {
    UserDto userDto = userService.updatePreferences(request);
    // The body describes exactly one user; no shared cache may ever hand it to another.
    return Response.ok(userDto).header(HttpHeaders.CACHE_CONTROL, "private, no-store").build();
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
  @Operation(
      summary = "Search users",
      description =
          "Search users by display name. Pass 'teamSlug' to keep only the members of that team —"
              + " useful for a picker that must yield a member, such as a ride group's leader. The"
              + " parameter only ever removes results, and requires the caller to belong to the"
              + " team.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Search results",
        content = @Content(schema = @Schema(implementation = PublicUserDto[].class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Not a member of the requested team",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response searchUsers(
      @Parameter(description = "Search query") @QueryParam("q") @Nullable String query,
      @Parameter(description = "Maximum results (max 20)") @QueryParam("limit") @DefaultValue("10")
          int limit,
      @Parameter(description = "Keep only members of this team") @QueryParam("teamSlug")
          @Nullable String teamSlug) {
    if (query == null || query.trim().isEmpty()) {
      return Response.ok(List.of()).build();
    }

    List<PublicUserDto> users =
        userService.searchByDisplayName(query.trim(), Math.min(limit, 20), teamSlug);
    return Response.ok(users).build();
  }

  @GET
  @Path("/me/participations")
  @Operation(
      summary = "List my participations",
      description =
          "The rides and trips the current user is registered to, soonest first. Only publications"
              + " the user may still see are returned: leaving a team removes its outings from this"
              + " list.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Participations retrieved successfully",
        content = @Content(schema = @Schema(implementation = PublicationListResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listMyParticipations(
      @Parameter(description = "Start date filter (ISO format)") @QueryParam("from")
          @Nullable String fromStr,
      @Parameter(description = "End date filter (ISO format)") @QueryParam("to")
          @Nullable String toStr,
      @Parameter(description = "Only publications with this status") @QueryParam("status")
          @Nullable Status status,
      @Parameter(
              description =
                  "How much of each row to send. COMPACT (case-insensitive) returns media.markdown"
                      + " empty and media.assets trimmed to the logo, the first image and the"
                      + " themed thumbnails — read 'excerpt' and 'thumbnailUrl' instead, both of"
                      + " which are present either way. The markdown body, the attachments, the GPX"
                      + " and FIT files and every image past the first are dropped. Omitted, or"
                      + " FULL, is the previous behaviour, byte for byte.")
          @QueryParam("view")
          @Nullable ListViewMode view,
      @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    Instant from = fromStr != null ? Instant.parse(fromStr) : null;
    Instant to = toStr != null ? Instant.parse(toStr) : null;

    PublicationListResponse response =
        publicationService.listMyParticipations(from, to, status, view, page, size);

    // Answer is specific to the caller by construction.
    return Response.ok(response).header(HttpHeaders.CACHE_CONTROL, "private, no-store").build();
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
        headers =
            @Header(
                name = "Retry-After",
                description = "Seconds until the export can be requested again",
                schema = @Schema(type = SchemaType.INTEGER)),
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
