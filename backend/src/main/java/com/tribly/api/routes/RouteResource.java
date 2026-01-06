package com.tribly.api.routes;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.common.request.SlugChangeRequest;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.routes.request.RouteRequest;
import com.tribly.dto.routes.response.*;
import com.tribly.enums.*;
import com.tribly.service.route.RouteSearchParams;
import com.tribly.service.route.RouteService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.jspecify.annotations.Nullable;

/**
 * REST API for route management.
 * Handles GPX upload, route CRUD, and file downloads.
 */
@Path("/api/teams/{slug}/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Routes", description = "GPX route management operations")
public class RouteResource extends AbstractAuthenticatedResource {

  @Inject RouteService routeService;

  /**
   * List routes for a team with filtering and sorting.
   */
  @GET
  @PermitAll
  @Operation(
      summary = "List routes",
      description = "Get paginated list of routes for a team with optional filters and sorting")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Routes retrieved successfully",
        content = @Content(schema = @Schema(implementation = RouteListResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  public Response listRoutes(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Search by name/markdown") @QueryParam("search")
          @Nullable String search,
      @Parameter(description = "Page number (0-indexed)") @QueryParam("page") @DefaultValue("0")
          int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size,
      @Parameter(description = "Minimum distance in meters") @QueryParam("minDistance")
          @Nullable Integer minDistance,
      @Parameter(description = "Maximum distance in meters") @QueryParam("maxDistance")
          @Nullable Integer maxDistance,
      @Parameter(description = "Minimum elevation gain in meters") @QueryParam("minElevationGain")
          @Nullable Integer minElevationGain,
      @Parameter(description = "Maximum elevation gain in meters") @QueryParam("maxElevationGain")
          @Nullable Integer maxElevationGain,
      @Parameter(description = "Hilliness preset (FLAT, HILLY, MOUNTAINOUS)")
          @QueryParam("hilliness")
          @Nullable Hilliness hilliness,
      @Parameter(description = "Filter by surface type") @QueryParam("surfaceType")
          @Nullable SurfaceType surfaceType,
      @Parameter(description = "Filter by wind direction") @QueryParam("windDirection")
          @Nullable WindDirection windDirection,
      @Parameter(description = "Latitude for proximity search") @QueryParam("nearLat")
          @Nullable Double nearLat,
      @Parameter(description = "Longitude for proximity search") @QueryParam("nearLon")
          @Nullable Double nearLon,
      @Parameter(description = "Search radius in meters (default: 25000)") @QueryParam("nearRadius")
          @Nullable Integer nearRadius,
      @Parameter(description = "Search near START, END, or START_OR_END (default)")
          @QueryParam("nearType")
          @Nullable NearType nearType,
      @Parameter(description = "Sort by field (DISTANCE, ELEVATION_GAIN, HILLINESS, DATE_TIME)")
          @QueryParam("sortBy")
          @Nullable RouteSortBy sortBy,
      @Parameter(description = "Sort direction (ASC, DESC)") @QueryParam("sortDir")
          @Nullable SortDirection sortDir) {

    User user = getCurrentUserOrNull();
    Team team = teamService.getTeam(teamSlug);

    RouteSearchParams params =
        RouteSearchParams.builder()
            .search(search)
            .page(page)
            .size(size)
            .minDistance(minDistance)
            .maxDistance(maxDistance)
            .minElevationGain(minElevationGain)
            .maxElevationGain(maxElevationGain)
            .hilliness(hilliness)
            .surfaceType(surfaceType)
            .windDirection(windDirection)
            .nearLat(nearLat)
            .nearLon(nearLon)
            .nearRadius(nearRadius)
            .nearType(nearType)
            .sortBy(sortBy)
            .sortDir(sortDir)
            .build();

    RouteListResponse routes = routeService.getRoutes(team, user, params);

    return Response.ok(routes).build();
  }

  /**
   * Create a new route with GPX upload.
   * Uses multipart/form-data for file upload.
   */
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @RolesAllowed("user")
  @Operation(summary = "Create route", description = "Create a new route by uploading a GPX file")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Route created successfully",
        content = @Content(schema = @Schema(implementation = RouteDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request or GPX file",
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
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response createRoute(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @RestForm("route") @PartType(MediaType.APPLICATION_JSON) @Valid @NotNull
          RouteRequest routeRequest,
      @RestForm("gpxFile") @Nullable FileUpload gpxFile)
      throws Exception {

    User user = getCurrentUser();
    Team team = teamService.getTeam(teamSlug);

    java.nio.file.Path gpxPath = null;
    if (gpxFile != null) {
      gpxPath = gpxFile.filePath();
    }

    RouteDto route = routeService.createRoute(team, routeRequest, gpxPath, user);

    return Response.created(
            URI.create("/api/teams/" + route.team().slug() + "/routes/" + route.slug()))
        .entity(route)
        .build();
  }

  /**
   * Get route details by ID.
   */
  @GET
  @Path("/{routeSlug}")
  @PermitAll
  @Operation(
      summary = "Get route details",
      description = "Get detailed route information including GPS coordinates and statistics")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Route retrieved successfully",
        content = @Content(schema = @Schema(implementation = RouteDetailDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  public Response getRoute(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Route slug") @PathParam("routeSlug") String routeSlug) {

    User user = getCurrentUserOrNull();
    Team team = teamService.getTeam(teamSlug);

    RouteDetailDto route = routeService.getRouteDetail(team, routeSlug, user);
    return Response.ok(route).build();
  }

  /**
   * Update route metadata and optionally GPX file.
   */
  @PUT
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Path("/{routeSlug}")
  @RolesAllowed("user")
  @Operation(
      summary = "Update route",
      description =
          "Update route metadata (name, markdown, etc.) and optionally replace the GPX file. "
              + "If a new GPX file is provided, the old track data and climbs will be replaced.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Route updated successfully",
        content = @Content(schema = @Schema(implementation = RouteDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request or GPX file",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to update this route",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updateRoute(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Route slug") @PathParam("routeSlug") String routeSlug,
      @RestForm("route") @PartType(MediaType.APPLICATION_JSON) @Valid @NotNull RouteRequest request,
      @RestForm("gpxFile") @Nullable FileUpload gpxFile) {

    User user = getCurrentUser();
    Team team = teamService.getTeam(teamSlug);

    java.nio.file.Path gpxPath = null;
    if (gpxFile != null) {
      gpxPath = gpxFile.filePath();
    }

    RouteDto route = routeService.updateRoute(team, routeSlug, request, gpxPath, user);
    return Response.ok(route).build();
  }

  /**
   * Delete route.
   */
  @DELETE
  @Path("/{routeSlug}")
  @RolesAllowed("user")
  @Operation(
      summary = "Delete route",
      description = "Soft delete a route. Requires route creator or team admin permissions.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Route deleted successfully"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to delete this route",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response deleteRoute(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Route slug") @PathParam("routeSlug") String routeSlug) {

    User user = getCurrentUser();
    Team team = teamService.getTeam(teamSlug);

    routeService.deleteRoute(team, routeSlug, user);
    return Response.noContent().build();
  }

  @PATCH
  @Path("/{routeSlug}/slug")
  @RolesAllowed("user")
  @Operation(
      operationId = "changeRouteSlug",
      summary = "Change route slug",
      description = "Change route URL slug. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Slug changed successfully",
        content = @Content(schema = @Schema(implementation = RouteDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid slug format",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to change this route's slug",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "409",
        description = "Slug already in use",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response changeSlug(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Current route URL slug") @PathParam("routeSlug") String currentSlug,
      @Valid SlugChangeRequest request) {
    User user = getCurrentUser();
    Team team = teamService.getTeam(teamSlug);
    RouteDto route = routeService.updateSlug(team, currentSlug, request.slug(), user);
    return Response.ok(route).build();
  }
}
