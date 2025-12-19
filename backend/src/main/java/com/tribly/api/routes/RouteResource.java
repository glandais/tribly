package com.tribly.api.routes;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.api.dto.ErrorResponse;
import com.tribly.domain.route.*;
import com.tribly.domain.team.Team;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.route.RouteService;
import com.tribly.service.team.TeamService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
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

import java.io.FileInputStream;
import java.net.URI;
import java.util.List;

/**
 * REST API for route management.
 * Handles GPX upload, route CRUD, and file downloads.
 */
@Path("/api/teams/{slug}/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Routes", description = "GPX route management operations")
public class RouteResource extends AbstractAuthenticatedResource {

    @Inject
    TeamService teamService;

    @Inject
    RouteService routeService;

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
                    content = @Content(schema = @Schema(implementation = RouteListResponse.class))
            ),
            @APIResponse(responseCode = "404", description = "Team not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public Response listRoutes(
            @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
            @Parameter(description = "Page number (0-indexed)") @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

        Team team = getTeamBySlug(teamSlug);
        Long userId = getCurrentUserIdOrNull();

        List<Route> routes = routeService.getRoutes(team.getId(), userId, page, size);
        long total = routeService.countRoutes(team.getId(), userId);

        List<RouteDto> dtos = routes.stream().map(RouteDto::from).toList();
        return Response.ok(new RouteListResponse(dtos, total, page, size)).build();
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
                    content = @Content(schema = @Schema(implementation = RouteDto.class))
            ),
            @APIResponse(responseCode = "400", description = "Invalid request or GPX file",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "403", description = "User is not a team member",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "404", description = "Team not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response createRoute(
            @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
            @RestForm @NotBlank String name,
            @RestForm String description,
            @RestForm @PartType(MediaType.TEXT_PLAIN) RouteDifficulty difficulty,
            @RestForm @PartType(MediaType.TEXT_PLAIN) SurfaceType surfaceType,
            @RestForm @PartType(MediaType.TEXT_PLAIN) Boolean isPublic,
            @RestForm("gpxFile") @Nullable FileUpload gpxFile) throws Exception {

        Team team = getTeamBySlug(teamSlug);
        Long userId = getCurrentUserId();

        // Validate GPX file
        if (gpxFile == null || gpxFile.filePath() == null) {
            throw BusinessException.validation("GPX file is required");
        }

        RouteService.CreateRouteRequest request = new RouteService.CreateRouteRequest(
                name, description, difficulty, surfaceType, isPublic
        );

        Route route = routeService.createRoute(
                team.getId(),
                request,
                new FileInputStream(gpxFile.filePath().toFile()),
                gpxFile.fileName(),
                userId
        );

        return Response.created(URI.create("/api/teams/" + teamSlug + "/routes/" +
                        TsidUtils.toString(route.getId())))
                .entity(RouteDto.from(route))
                .build();
    }

    /**
     * Get route details by ID.
     */
    @GET
    @Path("/{routeId}")
    @PermitAll
    @Operation(summary = "Get route details", description = "Get detailed route information including GPS coordinates and statistics")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Route retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RouteDetailDto.class))
            ),
            @APIResponse(responseCode = "404", description = "Team or route not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public Response getRoute(
            @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
            @Parameter(description = "Route ID (TSID)") @PathParam("routeId") String routeId) {

        Team team = getTeamBySlug(teamSlug);
        Long userId = getCurrentUserIdOrNull();
        Long routeIdLong = TsidUtils.toLong(routeId);

        Route route = routeService.getRoute(team.getId(), routeIdLong, userId)
                .orElseThrow(() -> BusinessException.notFound("Route", routeId));

        return Response.ok(RouteDetailDto.from(route)).build();
    }

    /**
     * Update route metadata (not GPX file).
     */
    @PATCH
    @Path("/{routeId}")
    @RolesAllowed("user")
    @Operation(summary = "Update route", description = "Update route metadata (name, description, difficulty, etc.). Does not update the GPX file.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Route updated successfully",
                    content = @Content(schema = @Schema(implementation = RouteDto.class))
            ),
            @APIResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "403", description = "User is not authorized to update this route",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "404", description = "Team or route not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response updateRoute(
            @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
            @Parameter(description = "Route ID (TSID)") @PathParam("routeId") String routeId,
            UpdateRouteRequest request) {

        Team team = getTeamBySlug(teamSlug);
        Long userId = getCurrentUserId();
        Long routeIdLong = TsidUtils.toLong(routeId);

        RouteService.UpdateRouteRequest serviceRequest = new RouteService.UpdateRouteRequest(
                request.name(),
                request.description(),
                request.difficulty(),
                request.surfaceType(),
                request.isPublic()
        );

        Route route = routeService.updateRoute(team.getId(), routeIdLong, serviceRequest, userId);
        return Response.ok(RouteDto.from(route)).build();
    }

    /**
     * Delete route.
     */
    @DELETE
    @Path("/{routeId}")
    @RolesAllowed("user")
    @Operation(summary = "Delete route", description = "Soft delete a route. Requires route creator or team admin permissions.")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Route deleted successfully"),
            @APIResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "403", description = "User is not authorized to delete this route",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @APIResponse(responseCode = "404", description = "Team or route not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response deleteRoute(
            @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
            @Parameter(description = "Route ID (TSID)") @PathParam("routeId") String routeId) {

        Team team = getTeamBySlug(teamSlug);
        Long userId = getCurrentUserId();
        Long routeIdLong = TsidUtils.toLong(routeId);

        routeService.deleteRoute(team.getId(), routeIdLong, userId);
        return Response.noContent().build();
    }

    /**
     * Get climbs for a route.
     */
    @GET
    @Path("/{routeId}/climbs")
    @PermitAll
    @Operation(summary = "Get route climbs", description = "Get all categorized climbs (hills) on a route")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Climbs retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ClimbListResponse.class))
            ),
            @APIResponse(responseCode = "404", description = "Team or route not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public Response getClimbs(
            @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
            @Parameter(description = "Route ID (TSID)") @PathParam("routeId") String routeId) {
        Team team = getTeamBySlug(teamSlug);
        Long userId = getCurrentUserIdOrNull();
        Long routeIdLong = TsidUtils.toLong(routeId);

        List<RouteClimb> climbs = routeService.getClimbs(team.getId(), routeIdLong, userId);
        List<RouteClimbDto> dtos = climbs.stream().map(RouteClimbDto::from).toList();
        return Response.ok(new ClimbListResponse(dtos)).build();
    }

    /**
     * Get GPX track points for a route.
     */
    @GET
    @Path("/{routeId}/track")
    @PermitAll
    @Operation(summary = "Get route track", description = "Get GPS track points (latitude, longitude, elevation) for displaying the route on a map")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Track retrieved successfully",
                    content = @Content(schema = @Schema(implementation = GpxTrackDto.class))
            ),
            @APIResponse(responseCode = "404", description = "Team or route not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public Response getTrack(
            @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
            @Parameter(description = "Route ID (TSID)") @PathParam("routeId") String routeId) {
        Team team = getTeamBySlug(teamSlug);
        Long userId = getCurrentUserIdOrNull();
        Long routeIdLong = TsidUtils.toLong(routeId);

        GpxTrack track = routeService.getTrack(team.getId(), routeIdLong, userId);
        return Response.ok(GpxTrackDto.from(track)).build();
    }

    private Team getTeamBySlug(String slug) {
        return teamService.getTeamBySlug(slug)
                .orElseThrow(() -> BusinessException.notFound("Team with slug '" + slug + "' not found"));
    }

    // Request/Response DTOs

    @Schema(description = "Route update request")
    public record UpdateRouteRequest(
            @Nullable @Schema(description = "Route name", nullable = true)
            String name,

            @Nullable @Schema(description = "Route description", nullable = true)
            String description,

            @Nullable @Schema(description = "Route difficulty", nullable = true)
            RouteDifficulty difficulty,

            @Nullable @Schema(description = "Surface type", nullable = true)
            SurfaceType surfaceType,

            @Nullable @Schema(description = "Whether the route is publicly visible", nullable = true)
            Boolean isPublic
    ) {
    }

    @Schema(description = "Paginated route list response")
    public record RouteListResponse(
            @Schema(description = "List of routes", required = true)
            List<RouteDto> routes,

            @Schema(description = "Total number of routes", required = true)
            long total,

            @Schema(description = "Current page number", required = true)
            int page,

            @Schema(description = "Page size", required = true)
            int size
    ) {
    }

    @Schema(description = "Climb list response")
    public record ClimbListResponse(
            @Schema(description = "List of climbs on the route", required = true)
            List<RouteClimbDto> climbs
    ) {
    }
}
