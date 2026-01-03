package com.tribly.service.route;

import com.tribly.common.GeoPoint;
import com.tribly.domain.asset.Asset;
import com.tribly.domain.asset.repository.AssetRepository;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.route.Route;
import com.tribly.domain.route.repository.RouteQuery;
import com.tribly.domain.route.repository.RouteRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.routes.request.RouteRequest;
import com.tribly.dto.routes.response.RouteDetailDto;
import com.tribly.dto.routes.response.RouteDto;
import com.tribly.dto.routes.response.RouteListResponse;
import com.tribly.enums.WindDirection;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.common.TeamEntityService;
import com.tribly.service.route.response.TrackMetadata;
import io.github.glandais.gpx.data.GPX;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.nio.file.Path;
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

  @Inject GpxProcessingService gpxProcessingService;

  @Inject AssetRepository assetRepository;

  /**
   * Create a new route with GPX upload.
   * Processes GPX file, extracts metadata, generates files, and stores everything.
   */
  @Transactional
  public RouteDto createRoute(
      String teamSlug, RouteRequest request, @Nullable Path gpxPath, Long creatorId)
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

    // Validate GPX file
    List<GeoPoint> routePoints = request.points();
    if (gpxPath == null && (routePoints == null || routePoints.isEmpty())) {
      throw BusinessException.validation("Route points are required");
    }

    String slug =
        slugService.generateSlug(
            request.name(), s -> routeRepository.existsByTeamAndSlug(team.getId(), s));

    // Create route entity
    Route route =
        new Route(creator, team, request.name(), slug, request.visibility(), request.surfaceType());

    // Persist to get ID for file storage
    routeRepository.persistAndFlush(route);
    LOG.infov("Route '{0}' created by user {1} for team {2}", route.getName(), creatorId, teamSlug);

    try {

      GPX gpx;
      if (gpxPath != null) {
        gpx = gpxProcessingService.parseGpx(gpxPath);
      } else {
        gpx = gpxProcessingService.fromPoints(route.getName(), routePoints);
      }
      // Process GPX file and update route
      TrackMetadata metadata = gpxProcessingService.createTracks(creator, route, gpx);

      route.setDistance(metadata.distance());
      route.setElevationGain(metadata.elevationGain());
      route.setHilliness(metadata.hilliness());
      route.setElevationLoss(metadata.elevationLoss());
      route.setStart(metadata.start());
      route.setEnd(metadata.end());
      route.setWindDirection(getWindDirection(metadata));

      updateMedia(route, request.media());
      routeRepository.persist(route);
      return RouteDto.from(route, assetService);
    } catch (Exception e) {
      LOG.errorv("GPX processing failed for route {0}, cleaning up files", route.getId());
      gpxProcessingService.deleteRouteFiles(route);
      throw e;
    }
  }

  private static WindDirection getWindDirection(TrackMetadata metadata) {
    WindDirection windDirection = metadata.windDirection();
    if (windDirection == null) {
      return WindDirection.NORTH;
    }
    return windDirection;
  }

  /**
   * Update route metadata and optionally GPX file.
   */
  @Transactional
  public RouteDto updateRoute(
      String teamSlug, String slug, RouteRequest request, @Nullable Path gpxPath, Long userId) {
    Route route = getRouteEntity(teamSlug, slug, userId);

    // Security check: must be admin or organizer to edit routes
    securityService.requireOrganizer(userId, teamSlug);

    User user =
        userRepository
            .findActiveById(userId)
            .orElseThrow(() -> BusinessException.notFound("User", userId));

    // Update basic metadata
    route.setName(request.name());
    route.setSurfaceType(request.surfaceType());
    route.setVisibility(request.visibility());
    route.setDateTime(Instant.now());

    try {
      GPX gpx = null;
      if (gpxPath != null) {
        gpx = gpxProcessingService.parseGpx(gpxPath);
      } else {
        List<GeoPoint> points = request.points();
        if (points != null && !points.isEmpty()) {
          gpx = gpxProcessingService.fromPoints(route.getName(), points);
        }
      }

      // If GPX file provided, update track and climbs
      if (gpx != null) {
        // Delete old GPX files
        gpxProcessingService.deleteRouteFiles(route);

        route.getTracks().clear();
        route.getWaypoints().clear();

        // Process GPX file and update route
        TrackMetadata metadata = gpxProcessingService.createTracks(user, route, gpx);

        route.setDistance(metadata.distance());
        route.setElevationGain(metadata.elevationGain());
        route.setHilliness(metadata.hilliness());
        route.setElevationLoss(metadata.elevationLoss());
        route.setStart(metadata.start());
        route.setEnd(metadata.end());
        route.setWindDirection(getWindDirection(metadata));

        LOG.infov("Route {0} GPX file updated by user {1}", slug, userId);
      }
    } catch (Exception e) {
      LOG.errorv("GPX processing failed for route {0}, cleaning up files", route.getId());
      gpxProcessingService.deleteRouteFiles(route);
      throw new RuntimeException(e);
    }

    updateMedia(route, request.media());
    routeRepository.persist(route);
    LOG.infov("Route {0} updated by user {1}", slug, userId);
    return RouteDto.from(route, assetService);
  }

  /**
   * Get a route by ID with access control.
   */
  public RouteDto getRoute(String teamSlug, String slug, @Nullable Long userId) {
    return RouteDto.from(getRouteEntity(teamSlug, slug, userId), assetService);
  }

  public RouteDetailDto getRouteDetail(String teamSlug, String slug, @Nullable Long userId) {
    Route route = getRouteEntity(teamSlug, slug, userId);
    return RouteDetailDto.from(route, assetService);
  }

  public Route getRouteEntity(String teamSlug, String routeSlug, @Nullable Long userId) {
    TriblyPage<Route> routes =
        routeRepository.find(
            RouteQuery.builder()
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
   * List routes for a team with pagination, filtering, and access control.
   */
  public RouteListResponse getRoutes(
      String teamSlug, @Nullable Long userId, RouteSearchParams params) {
    return getRoutesWithTeamSlugs(Set.of(teamSlug), userId, params);
  }

  /**
   * List all routes across all accessible teams with pagination, filtering, and access control.
   */
  public RouteListResponse getAllRoutes(@Nullable Long userId, RouteSearchParams params) {
    return getRoutesWithTeamSlugs(null, userId, params);
  }

  private RouteListResponse getRoutesWithTeamSlugs(
      @Nullable Set<String> teamSlugs, @Nullable Long userId, RouteSearchParams params) {
    TriblyPage<Route> routes =
        routeRepository.find(
            RouteQuery.builder()
                .userId(userId)
                .teamSlugs(teamSlugs)
                .search(params.search())
                .page(params.page())
                .size(params.size())
                .minDistance(params.minDistance())
                .maxDistance(params.maxDistance())
                .minElevationGain(params.minElevationGain())
                .maxElevationGain(params.maxElevationGain())
                .hilliness(params.hilliness())
                .surfaceTypes(params.surfaceTypes())
                .windDirections(params.windDirections())
                .nearLat(params.nearLat())
                .nearLon(params.nearLon())
                .nearRadius(params.nearRadius())
                .nearType(params.nearType())
                .sortBy(params.sortBy())
                .sortDir(params.sortDir())
                .build());
    List<RouteDto> dtos =
        routes.items().stream().map(route -> RouteDto.from(route, assetService)).toList();
    return new RouteListResponse(dtos, routes.total(), params.page(), params.size());
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

  @Override
  protected void updateMedia(TeamEntity teamEntity, MediaDto mediaDto) {
    super.updateMedia(teamEntity, mediaDto);
    Route route = (Route) teamEntity;
    for (Asset asset : teamEntity.getAssets()) {
      switch (asset.getType()) {
        case ROUTE_ORIGINAL_GPX, ROUTE_FILTERED_GPX -> asset.setFileName(route.getSlug() + ".gpx");
        case ROUTE_FIT -> asset.setFileName(route.getSlug() + ".fit");
        case ROUTE_THUMBNAIL -> asset.setFileName(route.getSlug() + ".png");
      }
      assetRepository.persist(asset);
    }
  }
}
