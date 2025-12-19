package com.tribly.api.routes;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.api.dto.ErrorResponse;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.route.RouteService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.io.File;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * REST API for route file downloads and images.
 * Uses cookie-based authentication via download tenant (HTTP auth permission).
 */
@Tag(name = "Route Downloads", description = "Route file downloads and images")
public abstract class AbstractDownloadResource extends AbstractAuthenticatedResource {

  @Inject RouteService routeService;

  /**
   * Download filtered GPX file.
   */
  @GET
  @Path("/gpx")
  @Produces("application/gpx+xml")
  @PermitAll
  @Operation(
      summary = "Download GPX file",
      description = "Download the route as a GPX file",
      hidden = true)
  @APIResponses({
    @APIResponse(responseCode = "200", description = "GPX file downloaded successfully"),
    @APIResponse(
        responseCode = "404",
        description = "Team or route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response downloadGpx(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Route ID (TSID)") @PathParam("routeId") String routeId) {

    Long userId = getCurrentUserIdOrNull();
    Long routeIdLong = TsidUtils.toLong(routeId);

    File gpxFile = routeService.getFilteredGpxFile(teamSlug, routeIdLong, userId);
    return Response.ok(gpxFile)
        // FIXME route slug
        .header("Content-Disposition", "attachment; filename=\"route.gpx\"")
        .build();
  }

  /**
   * Download FIT file.
   */
  @GET
  @Path("/fit")
  @Produces("application/octet-stream")
  @PermitAll
  @Operation(
      summary = "Download FIT file",
      description = "Download the route as a FIT file for Garmin devices",
      hidden = true)
  @APIResponses({
    @APIResponse(responseCode = "200", description = "FIT file downloaded successfully"),
    @APIResponse(
        responseCode = "404",
        description = "Team or route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response downloadFit(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Route ID (TSID)") @PathParam("routeId") String routeId) {

    Long userId = getCurrentUserIdOrNull();
    Long routeIdLong = TsidUtils.toLong(routeId);

    File fitFile = routeService.getFitFile(teamSlug, routeIdLong, userId);
    return Response.ok(fitFile)
        // FIXME route slug
        .header("Content-Disposition", "attachment; filename=\"route.fit\"")
        .build();
  }

  /**
   * Get route thumbnail image.
   */
  @GET
  @Path("/thumbnail")
  @Produces("image/png")
  @PermitAll
  @Operation(
      summary = "Get route thumbnail",
      description = "Get a thumbnail image of the route",
      hidden = true)
  @APIResponses({
    @APIResponse(responseCode = "200", description = "Thumbnail retrieved successfully"),
    @APIResponse(
        responseCode = "404",
        description = "Team or route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getThumbnail(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Route ID (TSID)") @PathParam("routeId") String routeId) {

    Long userId = getCurrentUserIdOrNull();
    Long routeIdLong = TsidUtils.toLong(routeId);

    File thumbnail = routeService.getThumbnailFile(teamSlug, routeIdLong, userId);
    return Response.ok(thumbnail).build();
  }
}
