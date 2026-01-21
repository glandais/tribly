package com.tribly.api.karoo;

import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.gps.response.RouteUploadResponse;
import com.tribly.dto.karoo.response.KarooRoutesResponse;
import com.tribly.enums.GpsServiceType;
import com.tribly.service.gps.GpsService;
import com.tribly.service.karoo.KarooRouteService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jspecify.annotations.Nullable;

/**
 * REST endpoints for Karoo route browsing and synchronization.
 *
 * <p>Lists routes from user's teams and triggers upload to Hammerhead cloud.
 */
@Path("/api/karoo/routes")
@Tag(name = "Karoo Routes", description = "Route browsing and sync endpoints for Karoo devices")
@Produces(MediaType.APPLICATION_JSON)
public class KarooRoutesResource {

  @Inject KarooRouteService karooRouteService;
  @Inject GpsService gpsService;

  @GET
  @RolesAllowed("user")
  @Operation(
      summary = "List routes",
      description = "Get routes from user's teams, prioritizing upcoming rides")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Route list",
        content = @Content(schema = @Schema(implementation = KarooRoutesResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public KarooRoutesResponse listRoutes(
      @Parameter(description = "User's latitude for proximity sorting") @QueryParam("lat")
          @Nullable Double lat,
      @Parameter(description = "User's longitude for proximity sorting") @QueryParam("lon")
          @Nullable Double lon) {
    return karooRouteService.getRoutesForUser(lat, lon);
  }

  @POST
  @Path("/{teamSlug}/{routeSlug}/sync")
  @RolesAllowed("user")
  @Operation(
      summary = "Sync route to Hammerhead",
      description = "Upload route GPX to user's Hammerhead cloud account")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Route synced successfully",
        content = @Content(schema = @Schema(implementation = RouteUploadResponse.class))),
    @APIResponse(
        responseCode = "400",
        description = "Not connected to Hammerhead",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public RouteUploadResponse syncRoute(
      @Parameter(description = "Team slug", required = true) @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Route slug", required = true) @PathParam("routeSlug")
          String routeSlug) {
    // Use existing GpsService to upload to Hammerhead
    return gpsService.uploadRoute(GpsServiceType.HAMMERHEAD, teamSlug, routeSlug);
  }
}
