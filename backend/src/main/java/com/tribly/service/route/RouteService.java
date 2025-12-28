package com.tribly.service.route;

import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.route.GpxTrack;
import com.tribly.domain.route.Route;
import com.tribly.domain.route.RouteClimb;
import com.tribly.domain.route.repository.GpxTrackRepository;
import com.tribly.domain.route.repository.RouteClimbRepository;
import com.tribly.domain.route.repository.RouteRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.routes.request.RouteRequest;
import com.tribly.dto.routes.response.*;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.common.SlugService;
import com.tribly.service.common.TeamEntityService;
import com.tribly.service.route.response.FileResult;
import com.tribly.service.route.response.ProcessedGpx;
import com.tribly.service.route.response.RouteMetadata;
import io.github.glandais.gpx.climb.Climb;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

/**
 * Business logic service for route management.
 * Handles CRUD operations, GPX processing orchestration, and security checks.
 */
@ApplicationScoped
public class RouteService extends TeamEntityService {

  private static final Logger LOG = Logger.getLogger(RouteService.class);

  @Inject RouteRepository routeRepository;

  @Inject GpxTrackRepository gpxTrackRepository;

  @Inject RouteClimbRepository routeClimbRepository;

  @Inject TeamRepository teamRepository;

  @Inject UserRepository userRepository;

  @Inject GpxProcessingService gpxProcessingService;

  @Inject SlugService slugService;

  /**
   * Create a new route with GPX upload.
   * Processes GPX file, extracts metadata, generates files, and stores everything.
   */
  @Transactional
  public RouteDto createRoute(
      String teamSlug, RouteRequest request, InputStream gpxFile, String fileName, Long creatorId)
      throws Exception {

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

    String slug =
        slugService.generateSlug(
            request.name(), s -> routeRepository.existsByTeamAndSlug(team.getId(), s));

    // Create route entity
    Route route = new Route(creator, team, request.name(), slug, request.visibility());
    route.setMarkdown(request.media().markdown());
    route.setSurfaceType(request.surfaceType());

    // Persist to get ID for file storage
    routeRepository.persistAndFlush(route);
    LOG.infov("Route '{0}' created by user {1} for team {2}", route.getName(), creatorId, teamSlug);

    try {
      // Process GPX file and update route
      processAndUpdateGpx(creator, route, gpxFile);
      routeRepository.persist(route);
      return RouteDto.from(route);

    } catch (Exception e) {
      LOG.errorv("GPX processing failed for route {0}, cleaning up files", route.getId());
      gpxProcessingService.deleteRouteFiles(route);
      throw e;
    }
  }

  /**
   * Process GPX file and update route with metadata, track, and climbs.
   * Shared logic between create and update operations.
   */
  private void processAndUpdateGpx(User creator, Route route, InputStream gpxFile) {
    // Process GPX file
    ProcessedGpx processed = gpxProcessingService.processGpxUpload(creator, route, gpxFile);

    // Update route with extracted metadata
    RouteMetadata metadata = processed.metadata();
    route.setDistance(metadata.distance());
    route.setElevationGain(metadata.elevationGain());
    route.setElevationLoss(metadata.elevationLoss());
    route.setStartLat(BigDecimal.valueOf(metadata.startLat()));
    route.setStartLng(BigDecimal.valueOf(metadata.startLng()));
    route.setEndLat(BigDecimal.valueOf(metadata.endLat()));
    route.setEndLng(BigDecimal.valueOf(metadata.endLng()));

    // Save GPX track
    GpxTrack track =
        new GpxTrack(creator, route, processed.wkt(), processed.trackPoints(), Instant.now());
    gpxTrackRepository.persist(track);
    LOG.infov("GPX track saved for route {0}", route.getId());

    // Save climbs
    for (Climb climb : processed.climbs()) {
      RouteClimb climbEntity =
          new RouteClimb(
              creator,
              route,
              (int) Math.round(climb.startDist()),
              (int) Math.round(climb.endDist()),
              (int) Math.round(climb.positiveElevation()),
              BigDecimal.valueOf(climb.grade()),
              BigDecimal.valueOf(climb.climbingGrade()));
      climbEntity.setCategory(gpxProcessingService.categorizeClimb(climb));
      routeClimbRepository.persist(climbEntity);
    }
    LOG.infov("{0} climbs saved for route {1}", processed.climbs().size(), route.getId());
  }

  /**
   * Get a route by ID with access control.
   */
  public RouteDto getRoute(String teamSlug, String slug, @Nullable Long userId) {
    return RouteDto.from(getRouteEntity(teamSlug, slug, userId));
  }

  public RouteDetailDto getRouteDetail(String teamSlug, String slug, @Nullable Long userId) {
    Route route = getRouteEntity(teamSlug, slug, userId);
    List<RouteClimb> climbs = routeClimbRepository.findByRoute(route.getId());
    GpxTrack gpxTrack = gpxTrackRepository.findByRoute(route.getId());
    return RouteDetailDto.from(route, climbs, gpxTrack);
  }

  private Route getRouteEntity(String teamSlug, String routeSlug, @Nullable Long userId) {
    TriblyPage<Route> routes =
        routeRepository.find(
            TeamEntityQueryBasic.builder()
                .userId(userId)
                .teamSlugs(Set.of(teamSlug))
                .slug(routeSlug)
                .size(1)
                .build());
    if (routes.items().isEmpty()) {
      throw BusinessException.notFound("Route", routeSlug);
    } else {
      return routes.items().getFirst();
    }
  }

  /**
   * List routes for a team with pagination and access control.
   */
  public RouteListResponse getRoutes(
      String teamSlug, @Nullable Long userId, int page, int size, @Nullable String search) {
    TriblyPage<Route> routes =
        routeRepository.find(
            TeamEntityQueryBasic.builder()
                .userId(userId)
                .teamSlugs(Set.of(teamSlug))
                .search(search)
                .page(page)
                .size(size)
                .build());
    List<RouteDto> dtos = routes.items().stream().map(RouteDto::from).toList();
    return new RouteListResponse(dtos, routes.total(), page, size);
  }

  /**
   * Update route metadata and optionally GPX file.
   */
  @Transactional
  public RouteDto updateRoute(
      String teamSlug,
      String slug,
      RouteRequest request,
      @Nullable InputStream gpxFile,
      Long userId) {
    Route route = getRouteEntity(teamSlug, slug, userId);

    // Security check: must be admin or organizer to edit routes
    securityService.requireOrganizer(userId, teamSlug);

    User user =
        userRepository
            .findActiveById(userId)
            .orElseThrow(() -> BusinessException.notFound("User", userId));

    // Update basic metadata
    route.setName(request.name());
    route.setMarkdown(request.media().markdown());
    route.setSurfaceType(request.surfaceType());
    route.setVisibility(request.visibility());
    route.setDateTime(Instant.now());

    // If GPX file provided, update track and climbs
    if (gpxFile != null) {
      try {
        // Delete old GPX track
        GpxTrack oldTrack = gpxTrackRepository.findByRoute(route.getId());
        if (oldTrack != null) {
          gpxTrackRepository.delete(oldTrack);
          LOG.infov("Deleted old GPX track for route {0}", route.getId());
        }

        // Delete old climbs
        List<RouteClimb> oldClimbs = routeClimbRepository.findByRoute(route.getId());
        for (RouteClimb oldClimb : oldClimbs) {
          routeClimbRepository.delete(oldClimb);
        }
        LOG.infov("Deleted {0} old climbs for route {1}", oldClimbs.size(), route.getId());

        // Delete old GPX files
        gpxProcessingService.deleteRouteFiles(route);

        // Process new GPX file
        processAndUpdateGpx(user, route, gpxFile);
        LOG.infov("Route {0} GPX file updated by user {1}", slug, userId);

      } catch (Exception e) {
        LOG.errorv("GPX processing failed for route {0}, cleaning up files", route.getId());
        gpxProcessingService.deleteRouteFiles(route);
        throw e;
      }
    }

    routeRepository.persist(route);
    LOG.infov("Route {0} updated by user {1}", slug, userId);
    return RouteDto.from(route);
  }

  /**
   * Delete route (soft delete) and cleanup files.
   */
  @Transactional
  public void deleteRoute(String teamSlug, String slug, Long userId) {
    Route route = getRouteEntity(teamSlug, slug, userId);

    // Security check: must be admin or organizer to delete routes
    securityService.requireOrganizer(userId, teamSlug);

    route.setDeleted(true);
    routeRepository.persist(route);

    // Delete associated files
    gpxProcessingService.deleteRouteFiles(route);

    LOG.infov("Route {0} deleted by user {1}", slug, userId);
  }

  /**
   * Get filtered GPX file for download.
   */
  public FileResult getFilteredGpxFile(String teamSlug, String slug, @Nullable Long userId) {
    Route route = getRouteEntity(teamSlug, slug, userId);
    File gpxFile = gpxProcessingService.getFilteredGpxFile(route);
    return new FileResult(gpxFile, route.getSlug() + ".gpx");
  }

  /**
   * Get FIT file for download.
   */
  public FileResult getFitFile(String teamSlug, String slug, @Nullable Long userId) {
    Route route = getRouteEntity(teamSlug, slug, userId);
    File fitFile = gpxProcessingService.getFitFile(route);
    return new FileResult(fitFile, route.getSlug() + ".fit");
  }

  /**
   * Get thumbnail image.
   */
  public FileResult getThumbnailFile(String teamSlug, String slug, @Nullable Long userId) {
    Route route = getRouteEntity(teamSlug, slug, userId);
    File thumbnailFile = gpxProcessingService.getThumbnailFile(route);
    return new FileResult(thumbnailFile, route.getSlug() + ".png");
  }
}
