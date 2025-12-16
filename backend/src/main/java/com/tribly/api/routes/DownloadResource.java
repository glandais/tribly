package com.tribly.api.routes;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.team.Team;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.route.RouteService;
import com.tribly.service.team.TeamService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.File;

/**
 * REST API for route file downloads and images.
 * Uses cookie-based authentication via download tenant (HTTP auth permission).
 */
@Path("/api/download/teams/{slug}/routes/{routeId}")
public class DownloadResource extends AbstractAuthenticatedResource  {

    @Inject
    TeamService teamService;

    @Inject
    RouteService routeService;

    /**
     * Download filtered GPX file.
     */
    @GET
    @Path("/gpx")
    @Produces("application/gpx+xml")
    @PermitAll
    public Response downloadGpx(
            @PathParam("slug") String teamSlug,
            @PathParam("routeId") String routeId) {

        Team team = getTeamBySlug(teamSlug);
        Long userId = getCurrentUserIdOrNull();
        Long routeIdLong = TsidUtils.toLong(routeId);

        File gpxFile = routeService.getFilteredGpxFile(team.getId(), routeIdLong, userId);
        return Response.ok(gpxFile)
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
    public Response downloadFit(
            @PathParam("slug") String teamSlug,
            @PathParam("routeId") String routeId) {

        Team team = getTeamBySlug(teamSlug);
        Long userId = getCurrentUserIdOrNull();
        Long routeIdLong = TsidUtils.toLong(routeId);

        File fitFile = routeService.getFitFile(team.getId(), routeIdLong, userId);
        return Response.ok(fitFile)
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
    public Response getThumbnail(
            @PathParam("slug") String teamSlug,
            @PathParam("routeId") String routeId) {

        Team team = getTeamBySlug(teamSlug);
        Long userId = getCurrentUserIdOrNull();
        Long routeIdLong = TsidUtils.toLong(routeId);

        File thumbnail = routeService.getThumbnailFile(team.getId(), routeIdLong, userId);
        return Response.ok(thumbnail)
                .header("Cache-Control", "public, max-age=86400")
                .build();
    }

    private Team getTeamBySlug(String slug) {
        return teamService.getTeamBySlug(slug)
                .orElseThrow(() -> BusinessException.notFound("Team with slug '" + slug + "' not found"));
    }
}
