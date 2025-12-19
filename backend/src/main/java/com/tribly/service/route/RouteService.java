package com.tribly.service.route;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.common.Visibility;
import com.tribly.domain.route.*;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.security.TeamSecurityService;
import io.github.glandais.gpx.climb.Climb;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Business logic service for route management.
 * Handles CRUD operations, GPX processing orchestration, and security checks.
 */
@ApplicationScoped
public class RouteService extends AbstractAuthenticatedResource {

    private static final Logger LOG = Logger.getLogger(RouteService.class);

    @Inject
    RouteRepository routeRepository;

    @Inject
    GpxTrackRepository gpxTrackRepository;

    @Inject
    RouteClimbRepository routeClimbRepository;

    @Inject
    TeamRepository teamRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    TeamSecurityService securityService;

    @Inject
    GpxProcessingService gpxProcessingService;

    /**
     * Create a new route with GPX upload.
     * Processes GPX file, extracts metadata, generates files, and stores everything.
     */
    @Transactional
    public Route createRoute(Long teamId, CreateRouteRequest request,
                             InputStream gpxFile, String fileName, Long creatorId) {
        // Security check: reuse ride permissions (admins & organizers can create routes)
        securityService.requireCanCreateRide(creatorId, teamId);

        Team team = teamRepository.findActiveById(teamId)
                .orElseThrow(() -> BusinessException.notFound("Team", teamId));

        User creator = userRepository.findActiveById(creatorId)
                .orElseThrow(() -> BusinessException.notFound("User", creatorId));

        // Create route entity
        Route route = new Route();
        route.setTeam(team);
        route.setCreatedBy(creator);
        route.setName(request.name());
        route.setDescription(request.description());
        route.setDifficulty(request.difficulty());
        route.setSurfaceType(request.surfaceType());
        route.setVisibility(request.visibility());

        // Persist to get ID for file storage
        routeRepository.persistAndFlush(route);
        LOG.infov("Route '{0}' created by user {1} for team {2}", route.getName(), creatorId, teamId);

        try {
            // Process GPX file
            GpxProcessingService.ProcessedGpx processed = gpxProcessingService.processGpxUpload(
                    route.getId(), gpxFile, fileName);

            // Update route with extracted metadata
            GpxProcessingService.RouteMetadata metadata = processed.metadata();
            route.setDistance(metadata.distance());
            route.setElevationGain(metadata.elevationGain());
            route.setElevationLoss(metadata.elevationLoss());
            route.setStartLat(BigDecimal.valueOf(metadata.startLat()));
            route.setStartLng(BigDecimal.valueOf(metadata.startLng()));
            route.setEndLat(BigDecimal.valueOf(metadata.endLat()));
            route.setEndLng(BigDecimal.valueOf(metadata.endLng()));
            route.setThumbnailUrl("/api/teams/" + team.getSlug() + "/routes/" +
                    TsidUtils.toString(route.getId()) + "/thumbnail");

            // Save GPX track
            GpxTrack track = new GpxTrack();
            track.setRoute(route);
            track.setName(request.name());
            track.setGeometry(processed.wkt());
            track.setTrackPoints(processed.trackPoints());
            track.setOriginalFileName(fileName);
            track.setProcessedAt(Instant.now());
            gpxTrackRepository.persist(track);
            LOG.infov("GPX track saved for route {0}", route.getId());

            // Save climbs
            for (Climb climb : processed.climbs()) {
                RouteClimb climbEntity = new RouteClimb();
                climbEntity.setRoute(route);
                climbEntity.setName(null); // TODO: could extract name from climb detection
                climbEntity.setStartDistance((int) Math.round(climb.startDist()));
                climbEntity.setEndDistance((int) Math.round(climb.endDist()));
                climbEntity.setElevationGain((int) Math.round(climb.positiveElevation()));
                climbEntity.setAverageGradient(BigDecimal.valueOf(climb.grade()));
                climbEntity.setMaxGradient(BigDecimal.valueOf(climb.climbingGrade()));
                climbEntity.setCategory(gpxProcessingService.categorizeClimb(climb));
                routeClimbRepository.persist(climbEntity);
            }
            LOG.infov("{0} climbs saved for route {1}", processed.climbs().size(), route.getId());

            routeRepository.persist(route);
            return route;

        } catch (Exception e) {
            LOG.errorv("GPX processing failed for route {0}, cleaning up files", route.getId());
            gpxProcessingService.deleteRouteFiles(route.getId());
            throw e;
        }
    }

    /**
     * Get a route by ID with access control.
     */
    public Optional<Route> getRoute(Long teamId, Long routeId, @Nullable Long userId) {
        Route route = routeRepository.findByIdAndTeam(routeId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Route", routeId));

        // Access control: public routes accessible to all, private routes only to members
        if (route.getVisibility() != Visibility.PUBLIC && !securityService.isMember(userId, teamId)) {
            throw BusinessException.forbidden("You are not a member of this team");
        }

        return Optional.of(route);
    }

    /**
     * List routes for a team with pagination and access control.
     */
    public List<Route> getRoutes(Long teamId, @Nullable Long userId, int page, int size) {
        Team team = teamRepository.findActiveById(teamId)
                .orElseThrow(() -> BusinessException.notFound("Team", teamId));

        boolean isMember = securityService.isMember(userId, teamId);

        // Private team - no access for non-members
        if (!isMember && team.getVisibility() != Visibility.PUBLIC) {
            throw BusinessException.forbidden("You are not a member of this team");
        }

        // Members see all routes (public + private), non-members see only public
        if (isMember) {
            return routeRepository.findByTeam(teamId, page, size);
        } else {
            return routeRepository.findPublicByTeam(teamId, page, size);
        }
    }

    /**
     * Count routes for a team with access control.
     */
    public long countRoutes(Long teamId, @Nullable Long userId) {
        Team team = teamRepository.findActiveById(teamId)
                .orElseThrow(() -> BusinessException.notFound("Team", teamId));

        boolean isMember = securityService.isMember(userId, teamId);

        // Private team - no access for non-members
        if (!isMember && team.getVisibility() != Visibility.PUBLIC) {
            throw BusinessException.forbidden("You are not a member of this team");
        }

        if (isMember) {
            return routeRepository.countByTeam(teamId);
        } else {
            return routeRepository.countPublicByTeam(teamId);
        }
    }

    /**
     * Update route metadata (not GPX file).
     */
    @Transactional
    public Route updateRoute(Long teamId, Long routeId, UpdateRouteRequest request, Long userId) {
        Route route = routeRepository.findByIdAndTeam(routeId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Route", routeId));

        // Security check: must be admin or organizer to edit routes
        securityService.requireCanCreateRide(userId, teamId);

        if (request.name() != null) {
            route.setName(request.name());
        }
        if (request.description() != null) {
            route.setDescription(request.description());
        }
        if (request.difficulty() != null) {
            route.setDifficulty(request.difficulty());
        }
        if (request.surfaceType() != null) {
            route.setSurfaceType(request.surfaceType());
        }
        if (request.visibility() != null) {
            route.setVisibility(request.visibility());
        }

        routeRepository.persist(route);
        LOG.infov("Route {0} updated by user {1}", routeId, userId);
        return route;
    }

    /**
     * Delete route (soft delete) and cleanup files.
     */
    @Transactional
    public void deleteRoute(Long teamId, Long routeId, Long userId) {
        Route route = routeRepository.findByIdAndTeam(routeId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Route", routeId));

        // Security check: must be admin or organizer to delete routes
        securityService.requireCanCreateRide(userId, teamId);

        route.softDelete();
        routeRepository.persist(route);

        // Delete associated files
        gpxProcessingService.deleteRouteFiles(routeId);

        LOG.infov("Route {0} deleted by user {1}", routeId, userId);
    }

    /**
     * Get climbs for a route.
     */
    public List<RouteClimb> getClimbs(Long teamId, Long routeId, @Nullable Long userId) {
        Route route = getRoute(teamId, routeId, userId)
                .orElseThrow(() -> BusinessException.notFound("Route", routeId));
        return routeClimbRepository.findByRoute(route.getId());
    }

    /**
     * Get GPX track for a route.
     */
    public GpxTrack getTrack(Long teamId, Long routeId, @Nullable Long userId) {
        Route route = getRoute(teamId, routeId, userId)
                .orElseThrow(() -> BusinessException.notFound("Route", routeId));
        return gpxTrackRepository.findByRoute(route.getId())
                .orElseThrow(() -> BusinessException.notFound("GPX track not found for route " + routeId));
    }

    /**
     * Get filtered GPX file for download.
     */
    public File getFilteredGpxFile(Long teamId, Long routeId, @Nullable Long userId) {
        Route route = getRoute(teamId, routeId, userId)
                .orElseThrow(() -> BusinessException.notFound("Route", routeId));
        return gpxProcessingService.getFilteredGpxFile(route.getId());
    }

    /**
     * Get FIT file for download.
     */
    public File getFitFile(Long teamId, Long routeId, @Nullable Long userId) {
        Route route = getRoute(teamId, routeId, userId)
                .orElseThrow(() -> BusinessException.notFound("Route", routeId));
        return gpxProcessingService.getFitFile(route.getId());
    }

    /**
     * Get thumbnail image.
     */
    public File getThumbnailFile(Long teamId, Long routeId, @Nullable Long userId) {
        Route route = getRoute(teamId, routeId, userId)
                .orElseThrow(() -> BusinessException.notFound("Route", routeId));
        return gpxProcessingService.getThumbnailFile(route.getId());
    }

    // Request DTOs

    public record CreateRouteRequest(
            String name,
            String description,
            RouteDifficulty difficulty,
            SurfaceType surfaceType,
            Visibility visibility
    ) {
    }

    public record UpdateRouteRequest(
            @Nullable String name,
            @Nullable String description,
            @Nullable RouteDifficulty difficulty,
            @Nullable SurfaceType surfaceType,
            @Nullable Visibility visibility
    ) {
    }
}
