package fr.pedalons.api.routes;

import fr.pedalons.dto.common.request.SlugChangeRequest;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.routes.request.RouteRequest;
import fr.pedalons.dto.routes.request.RouteSearchParams;
import fr.pedalons.dto.routes.response.*;
import fr.pedalons.enums.*;
import fr.pedalons.infrastructure.jaxrs.PedalonsMediaType;
import fr.pedalons.service.route.RouteService;
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
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.jspecify.annotations.Nullable;

/**
 * REST API for route management.
 * Handles GPX upload, route CRUD, and file downloads.
 */
@Path("/api/teams/{teamSlug}/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Routes", description = "GPX route management operations")
public class RouteResource {

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
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Search by name/markdown") @QueryParam("search")
          @Nullable String search,
      @Parameter(description = "Page number (0-indexed)") @QueryParam("page") @DefaultValue("0")
          int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size,
      @Parameter(description = "Minimum distance in meters") @QueryParam("minDistance")
          @Nullable Float minDistance,
      @Parameter(description = "Maximum distance in meters") @QueryParam("maxDistance")
          @Nullable Float maxDistance,
      @Parameter(description = "Minimum elevation gain in meters") @QueryParam("minElevationGain")
          @Nullable Float minElevationGain,
      @Parameter(description = "Maximum elevation gain in meters") @QueryParam("maxElevationGain")
          @Nullable Float maxElevationGain,
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
          @Nullable Double nearRadius,
      @Parameter(description = "Search near START, END, or START_OR_END (default)")
          @QueryParam("nearType")
          @Nullable NearType nearType,
      @Parameter(description = "Sort by field (DISTANCE, ELEVATION_GAIN, HILLINESS, DATE_TIME)")
          @QueryParam("sortBy")
          @Nullable RouteSortBy sortBy,
      @Parameter(description = "Sort direction (ASC, DESC)") @QueryParam("sortDir")
          @Nullable SortDirection sortDir) {

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

    RouteListResponse routes = routeService.getRoutes(teamSlug, params);

    return Response.ok(routes).build();
  }

  /**
   * Vector tile of a team's routes.
   */
  @GET
  @PermitAll
  @Path("/tiles/{z}/{x}/{y}.mvt")
  @Produces(PedalonsMediaType.MAPBOX_VECTOR_TILE)
  @Operation(
      summary = "Team routes vector tile",
      description =
          "Mapbox vector tile holding the team's routes, layer 'routes'. Accepts the same filters"
              + " as the route list, minus sorting and pagination, which a tile has no use for."
              + " Fetched directly by the map renderer, so it authenticates with the session cookie"
              + " rather than a bearer token.")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "Tile retrieved successfully"),
    @APIResponse(responseCode = "400", description = "Invalid tile coordinates"),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response routesTile(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Zoom level") @PathParam("z") int z,
      @Parameter(description = "Tile column") @PathParam("x") int x,
      @Parameter(description = "Tile row") @PathParam("y") int y,
      @Parameter(description = "Search by name/markdown") @QueryParam("search")
          @Nullable String search,
      @Parameter(description = "Minimum distance in meters") @QueryParam("minDistance")
          @Nullable Float minDistance,
      @Parameter(description = "Maximum distance in meters") @QueryParam("maxDistance")
          @Nullable Float maxDistance,
      @Parameter(description = "Minimum elevation gain in meters") @QueryParam("minElevationGain")
          @Nullable Float minElevationGain,
      @Parameter(description = "Maximum elevation gain in meters") @QueryParam("maxElevationGain")
          @Nullable Float maxElevationGain,
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
          @Nullable Double nearRadius,
      @Parameter(description = "Search near START, END, or START_OR_END (default)")
          @QueryParam("nearType")
          @Nullable NearType nearType) {

    RouteSearchParams params =
        RouteSearchParams.builder()
            .search(search)
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
            .build();

    return RouteTiles.response(routeService.getRoutesTile(teamSlug, params, z, x, y));
  }

  /**
   * Bounding box of a team's routes.
   */
  @GET
  @PermitAll
  @Path("/bounds")
  @Operation(
      summary = "Team routes bounding box",
      description =
          "Extent enclosing the team's routes, so a map can open framed on them. Accepts the same"
              + " filters as the route list, minus sorting and pagination. Yields a null box when"
              + " no route matches.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Bounding box retrieved successfully",
        content = @Content(schema = @Schema(implementation = RouteBoundsResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getRoutesBounds(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Search by name/markdown") @QueryParam("search")
          @Nullable String search,
      @Parameter(description = "Minimum distance in meters") @QueryParam("minDistance")
          @Nullable Float minDistance,
      @Parameter(description = "Maximum distance in meters") @QueryParam("maxDistance")
          @Nullable Float maxDistance,
      @Parameter(description = "Minimum elevation gain in meters") @QueryParam("minElevationGain")
          @Nullable Float minElevationGain,
      @Parameter(description = "Maximum elevation gain in meters") @QueryParam("maxElevationGain")
          @Nullable Float maxElevationGain,
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
          @Nullable Double nearRadius,
      @Parameter(description = "Search near START, END, or START_OR_END (default)")
          @QueryParam("nearType")
          @Nullable NearType nearType) {

    RouteSearchParams params =
        RouteSearchParams.builder()
            .search(search)
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
            .build();

    return Response.ok(routeService.getRoutesBounds(teamSlug, params)).build();
  }

  /**
   * Create a new route with GPX upload.
   * Uses multipart/form-data for file upload.
   */
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
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
  @RolesAllowed("user")
  public Response createRoute(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @RestForm("route") @PartType(MediaType.APPLICATION_JSON) @Valid @NotNull
          RouteRequest routeRequest,
      @RestForm("gpxFile") @Nullable FileUpload gpxFile)
      throws Exception {

    java.nio.file.Path gpxPath = null;
    if (gpxFile != null) {
      gpxPath = gpxFile.filePath();
    }

    RouteDto route = routeService.createRoute(teamSlug, routeRequest, gpxPath);

    return Response.status(Response.Status.CREATED).entity(route).build();
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
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Route slug") @PathParam("routeSlug") String routeSlug) {

    RouteDetailDto route = routeService.getDto(teamSlug, routeSlug);
    return Response.ok(route).build();
  }

  /**
   * List the rides and trips that use this route.
   */
  @GET
  @Path("/{routeSlug}/usages")
  @PermitAll
  @Operation(
      operationId = "getRouteUsages",
      summary = "List route usages",
      description =
          "Rides and trips that reference this route, directly or via a group/stage. Results are"
              + " visibility filtered for the caller.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Usages retrieved successfully",
        content = @Content(schema = @Schema(implementation = RouteUsagesResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  public Response getRouteUsages(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Route slug") @PathParam("routeSlug") String routeSlug) {

    RouteUsagesResponse usages = routeService.getUsages(teamSlug, routeSlug);
    return Response.ok(usages).build();
  }

  /**
   * Update route metadata and optionally GPX file.
   */
  @PUT
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Path("/{routeSlug}")
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
  @RolesAllowed("user")
  public Response updateRoute(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Route slug") @PathParam("routeSlug") String routeSlug,
      @RestForm("route") @PartType(MediaType.APPLICATION_JSON) @Valid @NotNull RouteRequest request,
      @RestForm("gpxFile") @Nullable FileUpload gpxFile) {

    java.nio.file.Path gpxPath = null;
    if (gpxFile != null) {
      gpxPath = gpxFile.filePath();
    }

    RouteDto route = routeService.updateRoute(teamSlug, routeSlug, request, gpxPath);
    return Response.ok(route).build();
  }

  /**
   * Delete route.
   */
  @DELETE
  @Path("/{routeSlug}")
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
  @RolesAllowed("user")
  public Response deleteRoute(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Route slug") @PathParam("routeSlug") String routeSlug) {

    routeService.deleteRoute(teamSlug, routeSlug);
    return Response.noContent().build();
  }

  @POST
  @Path("/{routeSlug}/undelete")
  @Operation(
      operationId = "undeleteRoute",
      summary = "Restore route",
      description =
          "Restore a soft-deleted route. Requires route creator or team admin permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Route restored successfully",
        content = @Content(schema = @Schema(implementation = RouteDetailDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to restore this route",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response undeleteRoute(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Route slug") @PathParam("routeSlug") String routeSlug) {
    RouteDetailDto dto = routeService.undeleteRoute(teamSlug, routeSlug);
    return Response.ok(dto).build();
  }

  @PATCH
  @Path("/{routeSlug}/slug")
  @Operation(
      operationId = "changeRouteSlug",
      summary = "Change route slug",
      description = "Change route URL slug. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Slug changed successfully",
        content = @Content(schema = @Schema(implementation = RouteDetailDto.class))),
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
  @RolesAllowed("user")
  public Response changeSlug(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Current route URL slug") @PathParam("routeSlug") String currentSlug,
      @Valid SlugChangeRequest request) {

    RouteDetailDto route = routeService.updateSlug(teamSlug, currentSlug, request.slug());
    return Response.ok(route).build();
  }
}
