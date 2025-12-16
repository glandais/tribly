package com.tribly.api.routes;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.route.Route;
import com.tribly.domain.route.RouteClimb;
import com.tribly.domain.route.RouteDifficulty;
import com.tribly.domain.route.SurfaceType;
import com.tribly.domain.route.GpxTrack;
import com.tribly.domain.team.Team;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.route.RouteService;
import com.tribly.service.team.TeamService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.File;
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
@RolesAllowed("user")
public class RouteResource extends AbstractAuthenticatedResource {

    @Inject
    TeamService teamService;

    @Inject
    RouteService routeService;

    /**
     * List routes for a team.
     */
    @GET
    public Response listRoutes(
            @PathParam("slug") String teamSlug,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

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
    public Response createRoute(
            @PathParam("slug") String teamSlug,
            @RestForm @NotBlank String name,
            @RestForm String description,
            @RestForm @PartType(MediaType.TEXT_PLAIN) RouteDifficulty difficulty,
            @RestForm @PartType(MediaType.TEXT_PLAIN) SurfaceType surfaceType,
            @RestForm @PartType(MediaType.TEXT_PLAIN) Boolean isPublic,
            @RestForm("gpxFile") FileUpload gpxFile) throws Exception {

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
    public Response getRoute(
            @PathParam("slug") String teamSlug,
            @PathParam("routeId") String routeId) {

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
    public Response updateRoute(
            @PathParam("slug") String teamSlug,
            @PathParam("routeId") String routeId,
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
    public Response deleteRoute(
            @PathParam("slug") String teamSlug,
            @PathParam("routeId") String routeId) {

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
    public Response getClimbs(
            @PathParam("slug") String teamSlug,
            @PathParam("routeId") String routeId) {

        Team team = getTeamBySlug(teamSlug);
        Long routeIdLong = TsidUtils.toLong(routeId);

        List<RouteClimb> climbs = routeService.getClimbs(routeIdLong);
        List<RouteClimbDto> dtos = climbs.stream().map(RouteClimbDto::from).toList();
        return Response.ok(new ClimbListResponse(dtos)).build();
    }

    /**
     * Get GPX track points for a route.
     */
    @GET
    @Path("/{routeId}/track")
    public Response getTrack(
            @PathParam("slug") String teamSlug,
            @PathParam("routeId") String routeId) {

        Team team = getTeamBySlug(teamSlug);
        Long routeIdLong = TsidUtils.toLong(routeId);

        GpxTrack track = routeService.getTrack(routeIdLong);
        return Response.ok(GpxTrackDto.from(track)).build();
    }

    /**
     * Download filtered GPX file.
     */
    @GET
    @Path("/{routeId}/download/gpx")
    @Produces("application/gpx+xml")
    public Response downloadGpx(
            @PathParam("slug") String teamSlug,
            @PathParam("routeId") String routeId) {

        Team team = getTeamBySlug(teamSlug);
        Long routeIdLong = TsidUtils.toLong(routeId);

        File gpxFile = routeService.getFilteredGpxFile(routeIdLong);
        return Response.ok(gpxFile)
                .header("Content-Disposition", "attachment; filename=\"route.gpx\"")
                .build();
    }

    /**
     * Download FIT file.
     */
    @GET
    @Path("/{routeId}/download/fit")
    @Produces("application/octet-stream")
    public Response downloadFit(
            @PathParam("slug") String teamSlug,
            @PathParam("routeId") String routeId) {

        Team team = getTeamBySlug(teamSlug);
        Long routeIdLong = TsidUtils.toLong(routeId);

        File fitFile = routeService.getFitFile(routeIdLong);
        return Response.ok(fitFile)
                .header("Content-Disposition", "attachment; filename=\"route.fit\"")
                .build();
    }

    /**
     * Get route thumbnail image.
     */
    @GET
    @Path("/{routeId}/thumbnail")
    @Produces("image/png")
    public Response getThumbnail(
            @PathParam("slug") String teamSlug,
            @PathParam("routeId") String routeId) {

        Team team = getTeamBySlug(teamSlug);
        Long routeIdLong = TsidUtils.toLong(routeId);

        File thumbnail = routeService.getThumbnailFile(routeIdLong);
        return Response.ok(thumbnail)
                .header("Cache-Control", "public, max-age=86400")
                .build();
    }

    private Team getTeamBySlug(String slug) {
        return teamService.getTeamBySlug(slug)
                .orElseThrow(() -> BusinessException.notFound("Team with slug '" + slug + "' not found"));
    }

    // Request/Response DTOs

    public record UpdateRouteRequest(
            String name,
            String description,
            RouteDifficulty difficulty,
            SurfaceType surfaceType,
            Boolean isPublic
    ) {}

    public record RouteListResponse(
            List<RouteDto> routes,
            long total,
            int page,
            int size
    ) {}

    public record ClimbListResponse(
            List<RouteClimbDto> climbs
    ) {}
}
