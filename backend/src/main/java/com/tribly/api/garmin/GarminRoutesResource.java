package com.tribly.api.garmin;

import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.garmin.response.GarminRoutesResponse;
import com.tribly.service.garmin.GarminRouteService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.File;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jspecify.annotations.Nullable;

/**
 * Routes API for Garmin Connect IQ devices. Provides route listing and FIT file downloads.
 */
@Path("/api/garmin/routes")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Garmin Routes", description = "Route management for Garmin Connect IQ devices")
@SecurityRequirement(name = "bearerAuth")
public class GarminRoutesResource {

  @Inject GarminRouteService garminRouteService;

  @GET
  @RolesAllowed("user")
  @Operation(
      summary = "List routes for user",
      description =
          "Get routes for authenticated user. Prioritizes routes from upcoming rides, then latest"
              + " routes from user's teams.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Routes list",
        content = @Content(schema = @Schema(implementation = GarminRoutesResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getRoutes(
      @Parameter(description = "User's current latitude (for proximity sorting)") @QueryParam("lat")
          @Nullable Double lat,
      @Parameter(description = "User's current longitude (for proximity sorting)")
          @QueryParam("lon")
          @Nullable Double lon) {

    GarminRoutesResponse response = garminRouteService.getRoutesForUser(lat, lon);
    return Response.ok(response).build();
  }

  @GET
  @Path("/{teamSlug}/{routeSlug}/fit")
  @RolesAllowed("user")
  @Produces("application/vnd.ant.fit")
  @Operation(
      summary = "Download FIT file",
      description = "Download route as FIT file for Garmin GPS")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "FIT file download"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Access denied",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response downloadFit(
      @Parameter(description = "Team URL slug", required = true) @PathParam("teamSlug")
          String teamSlug,
      @Parameter(description = "Route URL slug", required = true) @PathParam("routeSlug")
          String routeSlug) {

    File fitFile = garminRouteService.getFitFile(teamSlug, routeSlug);

    String fileName = routeSlug + ".fit";
    return Response.ok(fitFile)
        .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
        .header("Content-Type", "application/vnd.ant.fit")
        .build();
  }
}
