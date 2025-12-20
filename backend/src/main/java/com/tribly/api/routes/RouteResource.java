package com.tribly.api.routes;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.route.*;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.routes.request.UpdateRouteRequest;
import com.tribly.dto.routes.response.*;
import com.tribly.enums.RouteDifficulty;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.route.RouteService;
import com.tribly.service.route.request.CreateRouteRequest;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.FileInputStream;
import java.net.URI;
import java.util.List;
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
   * List routes for a team.
   */
  @GET
  @PermitAll
  @Operation(summary = "List routes", description = "Get paginated list of routes for a team")
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
      @Parameter(description = "Page number (0-indexed)") @QueryParam("page") @DefaultValue("0")
          int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    Long userId = getCurrentUserIdOrNull();

    TriblyPage<Route> routes = routeService.getRoutes(teamSlug, userId, page, size);

    List<RouteDto> dtos = routes.items().stream().map(RouteDto::from).toList();
    return Response.ok(new RouteListResponse(dtos, routes.total(), page, size)).build();
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
      @RestForm @NotBlank String name,
      @RestForm String description,
      @RestForm @PartType(MediaType.TEXT_PLAIN) RouteDifficulty difficulty,
      @RestForm @PartType(MediaType.TEXT_PLAIN) SurfaceType surfaceType,
      @RestForm @PartType(MediaType.TEXT_PLAIN) Visibility visibility,
      @RestForm("gpxFile") @Nullable FileUpload gpxFile)
      throws Exception {

    Long userId = getCurrentUserId();

    // Validate GPX file
    if (gpxFile == null || gpxFile.filePath() == null) {
      throw BusinessException.validation("GPX file is required");
    }

    CreateRouteRequest request =
        new CreateRouteRequest(name, description, difficulty, surfaceType, visibility);

    Route route =
        routeService.createRoute(
            teamSlug,
            request,
            new FileInputStream(gpxFile.filePath().toFile()),
            gpxFile.fileName(),
            userId);

    return Response.created(
            URI.create("/api/teams/" + teamSlug + "/routes/" + TsidUtils.toString(route.getId())))
        .entity(RouteDto.from(route))
        .build();
  }

  /**
   * Get route details by ID.
   */
  @GET
  @Path("/{routeId}")
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
      @Parameter(description = "Route ID (TSID)") @PathParam("routeId") String routeId) {

    Long userId = getCurrentUserIdOrNull();
    Long routeIdLong = TsidUtils.toLong(routeId);

    Route route = routeService.getRoute(teamSlug, routeIdLong, userId);

    return Response.ok(RouteDetailDto.from(route)).build();
  }

  /**
   * Update route metadata (not GPX file).
   */
  @PATCH
  @Path("/{routeId}")
  @RolesAllowed("user")
  @Operation(
      summary = "Update route",
      description =
          "Update route metadata (name, description, difficulty, etc.). Does not update the GPX"
              + " file.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Route updated successfully",
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
        description = "User is not authorized to update this route",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updateRoute(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Route ID (TSID)") @PathParam("routeId") String routeId,
      UpdateRouteRequest request) {

    Long userId = getCurrentUserId();
    Long routeIdLong = TsidUtils.toLong(routeId);

    com.tribly.service.route.request.UpdateRouteRequest serviceRequest =
        new com.tribly.service.route.request.UpdateRouteRequest(
            request.name(),
            request.description(),
            request.difficulty(),
            request.surfaceType(),
            request.visibility());

    Route route = routeService.updateRoute(teamSlug, routeIdLong, serviceRequest, userId);
    return Response.ok(RouteDto.from(route)).build();
  }

  /**
   * Delete route.
   */
  @DELETE
  @Path("/{routeId}")
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
      @Parameter(description = "Route ID (TSID)") @PathParam("routeId") String routeId) {

    Long userId = getCurrentUserId();
    Long routeIdLong = TsidUtils.toLong(routeId);

    routeService.deleteRoute(teamSlug, routeIdLong, userId);
    return Response.noContent().build();
  }

  /**
   * Get climbs for a route.
   */
  @GET
  @Path("/{routeId}/climbs")
  @PermitAll
  @Operation(
      summary = "Get route climbs",
      description = "Get all categorized climbs (hills) on a route")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Climbs retrieved successfully",
        content = @Content(schema = @Schema(implementation = ClimbListResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  public Response getClimbs(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Route ID (TSID)") @PathParam("routeId") String routeId) {
    Long userId = getCurrentUserIdOrNull();
    Long routeIdLong = TsidUtils.toLong(routeId);

    List<RouteClimb> climbs = routeService.getClimbs(teamSlug, routeIdLong, userId);
    List<RouteClimbDto> dtos = climbs.stream().map(RouteClimbDto::from).toList();
    return Response.ok(new ClimbListResponse(dtos)).build();
  }

  /**
   * Get GPX track points for a route.
   */
  @GET
  @Path("/{routeId}/track")
  @PermitAll
  @Operation(
      summary = "Get route track",
      description =
          "Get GPS track points (latitude, longitude, elevation) for displaying the route on a map")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Track retrieved successfully",
        content = @Content(schema = @Schema(implementation = GpxTrackDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or route not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
  })
  public Response getTrack(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Route ID (TSID)") @PathParam("routeId") String routeId) {
    Long userId = getCurrentUserIdOrNull();
    Long routeIdLong = TsidUtils.toLong(routeId);

    GpxTrack track = routeService.getTrack(teamSlug, routeIdLong, userId);
    return Response.ok(GpxTrackDto.from(track)).build();
  }

  // Request/Response DTOs

}
