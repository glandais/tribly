package fr.pedalons.service.route;

import fr.pedalons.common.GeoPoint;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.domain.asset.Asset;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.routes.request.RouteRequest;
import fr.pedalons.dto.routes.request.RouteSearchParams;
import fr.pedalons.dto.routes.response.RouteDetailDto;
import fr.pedalons.dto.routes.response.RouteDto;
import fr.pedalons.dto.routes.response.RouteListResponse;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.WindDirection;
import fr.pedalons.repository.asset.AssetRepository;
import fr.pedalons.repository.route.RouteQuery;
import fr.pedalons.repository.route.RouteRepository;
import fr.pedalons.service.common.TeamEntityService;
import fr.pedalons.service.route.response.TrackMetadata;
import fr.pedalons.service.security.annotation.CheckAccess;
import io.github.glandais.gpx.data.GPX;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * Business logic service for route management.
 * Handles CRUD operations, GPX processing orchestration, and security checks.
 */
@ApplicationScoped
public class RouteService extends TeamEntityService<Route, RouteRepository, RouteDetailDto> {

  @Inject RouteRepository routeRepository;

  @Inject GpxProcessingService gpxProcessingService;

  @Inject AssetRepository assetRepository;

  @Override
  protected RouteRepository getRepository() {
    return routeRepository;
  }

  @Override
  protected RouteDetailDto toDto(Route entity) {
    return RouteDetailDto.from(entity, assetService);
  }

  @Override
  public Route findBySlug(Team team, String entitySlug) {
    return super.findBySlug(team, entitySlug);
  }

  public Route get(String teamSlug, String entitySlug) {
    Team team = teamService.getTeam(teamSlug);
    return super.findBySlug(team, entitySlug);
  }

  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.READ)
  public RouteDetailDto getDto(String teamSlug, String entitySlug) {
    Team team = teamService.getTeam(teamSlug);
    return super.getDto(team, entitySlug);
  }

  /**
   * Create a new route with GPX upload.
   * Processes GPX file, extracts metadata, generates files, and stores everything.
   */
  @Transactional
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.CREATE)
  public RouteDto createRoute(String teamSlug, RouteRequest request, @Nullable Path gpxPath) {
    Team team = teamService.getTeam(teamSlug);
    User creator = pedalonsContext.getUser();

    validateVisibility(team, request);

    // Validate GPX file
    List<GeoPoint> routePoints = request.points();
    if (gpxPath == null && (routePoints == null || routePoints.isEmpty())) {
      throw new BusinessException(ErrorCode.GPX_EMPTY);
    }

    String slug = slugService.generateSlug(request.name(), team.getId(), routeRepository);

    // Create route entity
    Route route =
        new Route(creator, team, request.name(), slug, request.visibility(), request.surfaceType());

    // Persist to get ID for file storage
    routeRepository.persistAndFlush(route);

    try {

      GPX gpx;
      if (gpxPath != null) {
        gpx = gpxProcessingService.parseGpx(gpxPath);
      } else {
        gpx = gpxProcessingService.fromPoints(route.getName(), routePoints);
      }
      // Process GPX file and update route
      TrackMetadata metadata = gpxProcessingService.createTracks(route, gpx);

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
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.UPDATE)
  public RouteDto updateRoute(
      String teamSlug, String slug, RouteRequest request, @Nullable Path gpxPath) {
    Team team = teamService.getTeam(teamSlug);
    Route route = findBySlug(team, slug);

    validateVisibility(team, request);

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
        TrackMetadata metadata = gpxProcessingService.createTracks(route, gpx);

        route.setDistance(metadata.distance());
        route.setElevationGain(metadata.elevationGain());
        route.setHilliness(metadata.hilliness());
        route.setElevationLoss(metadata.elevationLoss());
        route.setStart(metadata.start());
        route.setEnd(metadata.end());
        route.setWindDirection(getWindDirection(metadata));
      }
    } catch (Exception e) {
      gpxProcessingService.deleteRouteFiles(route);
      throw new BusinessException(ErrorCode.GPX_FAILURE, e);
    }

    updateMedia(route, request.media());
    routeRepository.persist(route);
    return RouteDto.from(route, assetService);
  }

  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.UPDATE)
  @Transactional
  public RouteDetailDto updateSlug(String teamSlug, String slug, String newSlug) {
    Team team = teamService.getTeam(teamSlug);
    return super.updateSlug(team, slug, newSlug);
  }

  /**
   * List routes for a team with pagination, filtering, and access control.
   */
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.LIST)
  public RouteListResponse getRoutes(String teamSlug, RouteSearchParams params) {
    Team team = teamService.getTeam(teamSlug);
    return getRoutesWithTeamIds(Set.of(team.getId()), pedalonsContext.getUserNullable(), params);
  }

  /**
   * List all routes across all accessible teams with pagination, filtering, and access control.
   */
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.LIST_ALL_TEAMS)
  public RouteListResponse getAllRoutes(RouteSearchParams params) {
    return getRoutesWithTeamIds(null, pedalonsContext.getUserNullable(), params);
  }

  private RouteListResponse getRoutesWithTeamIds(
      @Nullable Set<Long> teamIds, @Nullable User user, RouteSearchParams params) {
    PedalonsPage<Route> routes =
        routeRepository.find(
            RouteQuery.builder()
                .domainId(pedalonsContext.getDomainId())
                .userId(user == null ? null : user.getId())
                .teamIds(teamIds)
                .search(params.search())
                .page(params.page())
                .size(params.size())
                .minDistance(params.minDistance())
                .maxDistance(params.maxDistance())
                .minElevationGain(params.minElevationGain())
                .maxElevationGain(params.maxElevationGain())
                .hilliness(params.hilliness())
                .surfaceType(params.surfaceType())
                .windDirection(params.windDirection())
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
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.DELETE)
  public void deleteRoute(String teamSlug, String slug) {
    Team team = teamService.getTeam(teamSlug);
    Route route = findBySlug(team, slug);

    route.setDeleted(true);
    routeRepository.persist(route);

    // Delete associated files
    gpxProcessingService.deleteRouteFiles(route);
  }

  @Override
  protected void updateMedia(TeamEntity teamEntity, @Valid MediaDto mediaDto) {
    super.updateMedia(teamEntity, mediaDto);
    Route route = (Route) teamEntity;
    for (Asset asset : teamEntity.getAssets()) {
      switch (asset.getType()) {
        case ROUTE_ORIGINAL_GPX, ROUTE_FILTERED_GPX -> asset.setFileName(route.getSlug() + ".gpx");
        case ROUTE_FIT -> asset.setFileName(route.getSlug() + ".fit");
        case ROUTE_THUMBNAIL_LIGHT, ROUTE_THUMBNAIL_DARK ->
            asset.setFileName(route.getSlug() + ".png");
      }
      assetRepository.persist(asset);
    }
  }
}
