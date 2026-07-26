package fr.pedalons.api.routes;

import fr.pedalons.dto.common.CountResponse;
import fr.pedalons.dto.routes.request.RouteFilterParams;
import fr.pedalons.dto.routes.request.RouteListParams;
import fr.pedalons.dto.routes.response.RouteBoundsResponse;
import fr.pedalons.dto.routes.response.RouteListResponse;
import fr.pedalons.infrastructure.jaxrs.PedalonsMediaType;
import fr.pedalons.service.route.RouteService;
import fr.pedalons.service.team.request.MinRole;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
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

  /** Route payloads carry commentCount, which depends on who is asking. */
  static final String PRIVATE_NO_STORE = "private, no-store";

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
      @Parameter(
              description =
                  "Only routes from teams where the user has at least this role. Yields nothing"
                      + " for an anonymous visitor.")
          @QueryParam("minRole")
          @Nullable MinRole minRole,
      @BeanParam RouteListParams params) {

    RouteListResponse routes =
        routeService.getAllRoutes(params.toBuilder().minRole(minRole).build());

    // Rows carry a per-user field (commentCount, which follows the caller's team
    // membership): never let a shared cache keep one user's answer for the next one.
    return Response.ok(routes).header(HttpHeaders.CACHE_CONTROL, PRIVATE_NO_STORE).build();
  }

  /**
   * How many routes the same filters match, across every accessible team.
   */
  @GET
  @PermitAll
  @Path("/count")
  @Operation(
      operationId = "countAllRoutes",
      summary = "Count all routes",
      description =
          "How many routes of all accessible teams match the filters, with none of them read."
              + " Accepts exactly the same filters as the route list, minus sorting and pagination,"
              + " so the figure and the list it opens can never disagree. Meant for a filter sheet"
              + " that wants to announce its result count before the user commits to it.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Count computed successfully",
        content = @Content(schema = @Schema(implementation = CountResponse.class)))
  })
  public Response countAllRoutes(
      @Parameter(
              description =
                  "Only routes from teams where the user has at least this role. Yields zero for an"
                      + " anonymous visitor.")
          @QueryParam("minRole")
          @Nullable MinRole minRole,
      @BeanParam RouteFilterParams params) {

    return Response.ok(routeService.countAllRoutes(params.toBuilder().minRole(minRole).build()))
        .build();
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
      @Parameter(
              description =
                  "Only routes from teams where the user has at least this role. Yields an empty"
                      + " tile for an anonymous visitor.")
          @QueryParam("minRole")
          @Nullable MinRole minRole,
      @BeanParam RouteFilterParams params) {

    return RouteTiles.response(
        routeService.getAllRoutesTile(params.toBuilder().minRole(minRole).build(), z, x, y));
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
      @Parameter(
              description =
                  "Only routes from teams where the user has at least this role. Yields a null box"
                      + " for an anonymous visitor.")
          @QueryParam("minRole")
          @Nullable MinRole minRole,
      @BeanParam RouteFilterParams params) {

    return Response.ok(routeService.getAllRoutesBounds(params.toBuilder().minRole(minRole).build()))
        .build();
  }
}
