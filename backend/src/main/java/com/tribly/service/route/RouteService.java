package com.tribly.service.route;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.route.GpxTrack;
import com.tribly.domain.route.Route;
import com.tribly.domain.route.RouteClimb;
import com.tribly.domain.route.repository.GpxTrackRepository;
import com.tribly.domain.route.repository.RouteClimbRepository;
import com.tribly.domain.route.repository.RouteQuery;
import com.tribly.domain.route.repository.RouteRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.routes.request.CreateRouteRequest;
import com.tribly.dto.routes.request.UpdateRouteRequest;
import com.tribly.dto.routes.response.*;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.route.response.ProcessedGpx;
import com.tribly.service.route.response.RouteMetadata;
import com.tribly.service.security.TeamSecurityService;
import io.github.glandais.gpx.climb.Climb;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

/**
 * Business logic service for route management.
 * Handles CRUD operations, GPX processing orchestration, and security checks.
 */
@ApplicationScoped
public class RouteService extends AbstractAuthenticatedResource {

  private static final Logger LOG = Logger.getLogger(RouteService.class);

  @Inject RouteRepository routeRepository;

  @Inject GpxTrackRepository gpxTrackRepository;

  @Inject RouteClimbRepository routeClimbRepository;

  @Inject TeamRepository teamRepository;

  @Inject UserRepository userRepository;

  @Inject TeamSecurityService securityService;

  @Inject GpxProcessingService gpxProcessingService;

  /**
   * Create a new route with GPX upload.
   * Processes GPX file, extracts metadata, generates files, and stores everything.
   */
  @Transactional
  public RouteDto createRoute(
      String teamSlug,
      CreateRouteRequest request,
      InputStream gpxFile,
      String fileName,
      Long creatorId) {

    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    // Security check: reuse ride permissions (admins & organizers can create routes)
    securityService.requireOrganizer(creatorId, team.getSlug());

    User creator =
        userRepository
            .findActiveById(creatorId)
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
    LOG.infov("Route '{0}' created by user {1} for team {2}", route.getName(), creatorId, teamSlug);

    try {
      // Process GPX file
      ProcessedGpx processed =
          gpxProcessingService.processGpxUpload(route.getId(), gpxFile, fileName);

      // Update route with extracted metadata
      RouteMetadata metadata = processed.metadata();
      route.setDistance(metadata.distance());
      route.setElevationGain(metadata.elevationGain());
      route.setElevationLoss(metadata.elevationLoss());
      route.setStartLat(BigDecimal.valueOf(metadata.startLat()));
      route.setStartLng(BigDecimal.valueOf(metadata.startLng()));
      route.setEndLat(BigDecimal.valueOf(metadata.endLat()));
      route.setEndLng(BigDecimal.valueOf(metadata.endLng()));
      route.setThumbnailUrl(
          "/api/teams/"
              + team.getSlug()
              + "/routes/"
              + TsidUtils.toString(route.getId())
              + "/thumbnail");

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
      return RouteDto.from(route);

    } catch (Exception e) {
      LOG.errorv("GPX processing failed for route {0}, cleaning up files", route.getId());
      gpxProcessingService.deleteRouteFiles(route.getId());
      throw e;
    }
  }

  /**
   * Get a route by ID with access control.
   */
  public RouteDto getRoute(String teamSlug, Long routeId, @Nullable Long userId) {
    return RouteDto.from(getRouteEntity(teamSlug, routeId, userId));
  }

  public RouteDetailDto getRouteDetail(String teamSlug, Long routeId, @Nullable Long userId) {
    return RouteDetailDto.from(getRouteEntity(teamSlug, routeId, userId));
  }

  private Route getRouteEntity(String teamSlug, Long routeId, @Nullable Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    RouteQueryParams routeQueryParams = getRouteQueryParams(userId, team);

    RouteQuery routeQuery =
        new RouteQuery(team.getId(), 0, 1, routeId, routeQueryParams.visibility());
    TriblyPage<Route> triblyPage = routeRepository.find(routeQuery);
    if (triblyPage.items().isEmpty()) {
      throw BusinessException.notFound("Route", routeId);
    } else {
      return triblyPage.items().getFirst();
    }
  }

  /**
   * List routes for a team with pagination and access control.
   */
  public RouteListResponse getRoutes(String slug, @Nullable Long userId, int page, int size) {
    Team team =
        teamRepository.findBySlug(slug).orElseThrow(() -> BusinessException.notFound("Team", slug));

    RouteQueryParams routeQueryParams = getRouteQueryParams(userId, team);

    RouteQuery routeQuery =
        new RouteQuery(team.getId(), page, size, null, routeQueryParams.visibility());
    TriblyPage<Route> routes = routeRepository.find(routeQuery);
    List<RouteDto> dtos = routes.items().stream().map(RouteDto::from).toList();
    return new RouteListResponse(dtos, routes.total(), page, size);
  }

  private record RouteQueryParams(@Nullable Visibility visibility) {}

  private RouteQueryParams getRouteQueryParams(@Nullable Long userId, Team team) {
    boolean isMember = securityService.isMember(userId, team);

    Visibility visibility = getVisibility(isMember, team);
    return new RouteQueryParams(visibility);
  }

  private @Nullable Visibility getVisibility(boolean isMember, Team team) {
    Visibility visibility = null;
    // For non-members of public teams, filter to show only public rides
    if (!isMember && team.getVisibility() == Visibility.PUBLIC) {
      visibility = Visibility.PUBLIC;
    } else if (!isMember) {
      // Private team - no access for non-members
      throw BusinessException.notFound("");
    }
    return visibility;
  }

  /**
   * Update route metadata (not GPX file).
   */
  @Transactional
  public RouteDto updateRoute(
      String teamSlug, Long routeId, UpdateRouteRequest request, Long userId) {
    Route route = getRouteEntity(teamSlug, routeId, userId);

    // Security check: must be admin or organizer to edit routes
    securityService.requireOrganizer(userId, teamSlug);

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
    return RouteDto.from(route);
  }

  /**
   * Delete route (soft delete) and cleanup files.
   */
  @Transactional
  public void deleteRoute(String teamSlug, Long routeId, Long userId) {
    Route route = getRouteEntity(teamSlug, routeId, userId);

    // Security check: must be admin or organizer to delete routes
    securityService.requireOrganizer(userId, teamSlug);

    route.softDelete();
    routeRepository.persist(route);

    // Delete associated files
    gpxProcessingService.deleteRouteFiles(routeId);

    LOG.infov("Route {0} deleted by user {1}", routeId, userId);
  }

  /**
   * Get climbs for a route.
   */
  public ClimbListResponse getClimbs(String teamSlug, Long routeId, @Nullable Long userId) {
    Route route = getRouteEntity(teamSlug, routeId, userId);
    List<RouteClimb> climbs = routeClimbRepository.findByRoute(route.getId());
    List<RouteClimbDto> dtos = climbs.stream().map(RouteClimbDto::from).toList();
    return new ClimbListResponse(dtos);
  }

  /**
   * Get GPX track for a route.
   */
  public GpxTrackDto getTrack(String teamSlug, Long routeId, @Nullable Long userId) {
    Route route = getRouteEntity(teamSlug, routeId, userId);
    return gpxTrackRepository
        .findByRoute(route.getId())
        .map(GpxTrackDto::from)
        .orElseThrow(() -> BusinessException.notFound("GPX track not found for route " + routeId));
  }

  /**
   * Get filtered GPX file for download.
   */
  public File getFilteredGpxFile(String teamSlug, Long routeId, @Nullable Long userId) {
    Route route = getRouteEntity(teamSlug, routeId, userId);
    return gpxProcessingService.getFilteredGpxFile(route.getId());
  }

  /**
   * Get FIT file for download.
   */
  public File getFitFile(String teamSlug, Long routeId, @Nullable Long userId) {
    Route route = getRouteEntity(teamSlug, routeId, userId);
    return gpxProcessingService.getFitFile(route.getId());
  }

  /**
   * Get thumbnail image.
   */
  public File getThumbnailFile(String teamSlug, Long routeId, @Nullable Long userId) {
    Route route = getRouteEntity(teamSlug, routeId, userId);
    return gpxProcessingService.getThumbnailFile(route.getId());
  }

  // Request DTOs

}
