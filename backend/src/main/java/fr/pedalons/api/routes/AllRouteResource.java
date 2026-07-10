package fr.pedalons.api.routes;

import fr.pedalons.dto.routes.request.RouteSearchParams;
import fr.pedalons.dto.routes.response.RouteBoundsResponse;
import fr.pedalons.dto.routes.response.RouteListResponse;
import fr.pedalons.enums.*;
import fr.pedalons.infrastructure.jaxrs.PedalonsMediaType;
import fr.pedalons.service.route.RouteService;
import fr.pedalons.service.team.request.MinRole;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
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

/**
 * REST API for listing routes across all accessible teams.
 */
@Path("/api/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Routes", description = "GPX route management operations")
public class AllRouteResource {

  @Inject RouteService routeService;

  /**
   * List all routes from all accessible teams with filtering and sorting.
   */
  @GET
  @PermitAll
  @Operation(
      summary = "List all routes",
      description =
          "Get paginated list of routes from all accessible teams (user's teams + public teams)")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Routes retrieved successfully",
        content = @Content(schema = @Schema(implementation = RouteListResponse.class)))
  })
  public Response listAllRoutes(
      @Parameter(description = "Search by name/markdown") @QueryParam("search")
          @Nullable String search,
      @Parameter(description = "Page number (0-indexed)") @QueryParam("page") @DefaultValue("0")
          int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size,
      @Parameter(
              description =
                  "Only routes from teams where the user has at least this role. Yields nothing"
                      + " for an anonymous visitor.")
          @QueryParam("minRole")
          @Nullable MinRole minRole,
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
            .minRole(minRole)
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

    RouteListResponse routes = routeService.getAllRoutes(params);

    return Response.ok(routes).build();
  }

  /**
   * Vector tile of the routes of every accessible team.
   */
  @GET
  @PermitAll
  @Path("/tiles/{z}/{x}/{y}.mvt")
  @Produces(PedalonsMediaType.MAPBOX_VECTOR_TILE)
  @Operation(
      summary = "All routes vector tile",
      description =
          "Mapbox vector tile holding the routes of all accessible teams, layer 'routes'. Accepts"
              + " the same filters as the route list, minus sorting and pagination, which a tile"
              + " has no use for. Fetched directly by the map renderer, so it authenticates with"
              + " the session cookie rather than a bearer token.")
  @APIResponses({
    @APIResponse(responseCode = "200", description = "Tile retrieved successfully"),
    @APIResponse(responseCode = "400", description = "Invalid tile coordinates")
  })
  public Response allRoutesTile(
      @Parameter(description = "Zoom level") @PathParam("z") int z,
      @Parameter(description = "Tile column") @PathParam("x") int x,
      @Parameter(description = "Tile row") @PathParam("y") int y,
      @Parameter(description = "Search by name/markdown") @QueryParam("search")
          @Nullable String search,
      @Parameter(
              description =
                  "Only routes from teams where the user has at least this role. Yields an empty"
                      + " tile for an anonymous visitor.")
          @QueryParam("minRole")
          @Nullable MinRole minRole,
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
            .minRole(minRole)
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

    return RouteTiles.response(routeService.getAllRoutesTile(params, z, x, y));
  }

  /**
   * Bounding box of the routes of every accessible team.
   */
  @GET
  @PermitAll
  @Path("/bounds")
  @Operation(
      summary = "All routes bounding box",
      description =
          "Extent enclosing the routes of all accessible teams, so a map can open framed on them."
              + " Accepts the same filters as the route list, minus sorting and pagination. Yields"
              + " a null box when no route matches.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Bounding box retrieved successfully",
        content = @Content(schema = @Schema(implementation = RouteBoundsResponse.class)))
  })
  public Response getAllRoutesBounds(
      @Parameter(description = "Search by name/markdown") @QueryParam("search")
          @Nullable String search,
      @Parameter(
              description =
                  "Only routes from teams where the user has at least this role. Yields a null box"
                      + " for an anonymous visitor.")
          @QueryParam("minRole")
          @Nullable MinRole minRole,
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
            .minRole(minRole)
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

    return Response.ok(routeService.getAllRoutesBounds(params)).build();
  }
}
