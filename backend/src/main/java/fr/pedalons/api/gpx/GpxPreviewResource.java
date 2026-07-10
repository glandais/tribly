package fr.pedalons.api.gpx;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.gps.response.RouteUploadResponse;
import fr.pedalons.dto.gpx.response.GpxPreviewDto;
import fr.pedalons.dto.routes.request.RouteRequest;
import fr.pedalons.dto.routes.response.RouteDto;
import fr.pedalons.enums.GpsServiceType;
import fr.pedalons.service.gpx.GpxPreviewService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.jspecify.annotations.Nullable;

/**
 * REST API for the GPX tools page: analyse a GPX file without attaching it to a team.
 */
@Path("/api/gpx-previews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "GPX previews", description = "Analyse a GPX file outside of any team")
public class GpxPreviewResource {

  /**
   * Well under {@code quarkus.http.limits.max-body-size}, which is a global ceiling rather than a
   * per-upload guard. A 10 MB GPX is already several hundred thousand points.
   */
  private static final long MAX_GPX_SIZE_BYTES = 10L * 1024 * 1024;

  @Inject GpxPreviewService gpxPreviewService;

  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Operation(
      summary = "Analyse a GPX file",
      description =
          "Uploads a GPX file, runs the elevation and climb pipeline, and stores the result under"
              + " an unguessable identifier for 30 days")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "GPX analysed successfully",
        content = @Content(schema = @Schema(implementation = GpxPreviewDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Missing, oversized or unparseable GPX file",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response createPreview(@RestForm("gpxFile") @Nullable FileUpload gpxFile) {
    if (gpxFile == null) {
      throw new BusinessException(ErrorCode.FILE_REQUIRED);
    }
    if (gpxFile.size() > MAX_GPX_SIZE_BYTES) {
      throw new BusinessException(ErrorCode.FILE_TOO_LARGE);
    }
    GpxPreviewDto preview = gpxPreviewService.createPreview(gpxFile.filePath(), gpxFile.fileName());
    return Response.status(Response.Status.CREATED).entity(preview).build();
  }

  @GET
  @Path("/{previewId}")
  @Operation(
      summary = "Get an analysed GPX file",
      description =
          "Anyone holding the link may read the preview: the unguessable identifier is what grants"
              + " access. Creating and deleting require an account.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Preview retrieved successfully",
        content = @Content(schema = @Schema(implementation = GpxPreviewDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "Preview not found or expired",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PermitAll
  public GpxPreviewDto getPreview(
      @Parameter(description = "Public preview identifier") @PathParam("previewId")
          String previewId) {
    return gpxPreviewService.getPreview(previewId);
  }

  @DELETE
  @Path("/{previewId}")
  @Operation(summary = "Delete an analysed GPX file", description = "Only the creator may delete")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Preview deleted successfully"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not the creator",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Preview not found or expired",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response deletePreview(
      @Parameter(description = "Public preview identifier") @PathParam("previewId")
          String previewId) {
    gpxPreviewService.delete(previewId);
    return Response.noContent().build();
  }

  @POST
  @Path("/{previewId}/gps/{serviceType}")
  @Operation(
      summary = "Send an analysed GPX file to a GPS service",
      description = "Uploads the preview to the current user's connected Garmin or Hammerhead")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Upload completed",
        content = @Content(schema = @Schema(implementation = RouteUploadResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "GPS service not connected",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Preview not found or expired",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public RouteUploadResponse uploadToGpsService(
      @Parameter(description = "Public preview identifier") @PathParam("previewId")
          String previewId,
      @Parameter(description = "GPS service type") @PathParam("serviceType")
          GpsServiceType serviceType) {
    return gpxPreviewService.uploadToGpsService(previewId, serviceType);
  }

  @POST
  @Path("/{previewId}/routes/{teamSlug}")
  @Operation(
      summary = "Save an analysed GPX file as a route",
      description = "Creates a team route from the preview's original upload")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Route created successfully",
        content = @Content(schema = @Schema(implementation = RouteDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not a team member",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Preview or team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response createRouteFromPreview(
      @Parameter(description = "Public preview identifier") @PathParam("previewId")
          String previewId,
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Valid @NotNull RouteRequest request) {
    RouteDto route = gpxPreviewService.createRoute(previewId, teamSlug, request);
    return Response.status(Response.Status.CREATED).entity(route).build();
  }
}
