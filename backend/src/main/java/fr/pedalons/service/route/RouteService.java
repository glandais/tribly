package fr.pedalons.service.route;

import fr.pedalons.common.GeoPoint;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.domain.asset.Asset;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.comments.response.CommentCounts;
import fr.pedalons.dto.common.CountResponse;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.routes.request.GeometryOptions;
import fr.pedalons.dto.routes.request.RouteRequest;
import fr.pedalons.dto.routes.request.RouteSearchParams;
import fr.pedalons.dto.routes.response.ElevationProfileDto;
import fr.pedalons.dto.routes.response.RouteBoundsResponse;
import fr.pedalons.dto.routes.response.RouteDetailDto;
import fr.pedalons.dto.routes.response.RouteDto;
import fr.pedalons.dto.routes.response.RouteListResponse;
import fr.pedalons.dto.routes.response.RouteUsageDto;
import fr.pedalons.dto.routes.response.RouteUsagesResponse;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.WindDirection;
import fr.pedalons.repository.asset.AssetRepository;
import fr.pedalons.repository.common.TeamEntityQueryBasic;
import fr.pedalons.repository.ride.RideRepository;
import fr.pedalons.repository.route.RouteQuery;
import fr.pedalons.repository.route.RouteRepository;
import fr.pedalons.repository.trip.TripRepository;
import fr.pedalons.service.comment.CommentCountLookup;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * Business logic service for route management.
 * Handles CRUD operations, GPX processing orchestration, and security checks.
 */
@ApplicationScoped
public class RouteService extends TeamEntityService<Route, RouteRepository, RouteDetailDto> {

  /** A profile below two points is not a curve. */
  public static final int MIN_PROFILE_SAMPLES = 2;

  /**
   * Past a thousand points a profile chart is drawing several samples per pixel; the ceiling exists
   * so no request can ask the server to build an arbitrarily large answer.
   */
  public static final int MAX_PROFILE_SAMPLES = 1_000;

  /**
   * What a client gets when it asks for a profile without saying how detailed. A string because it
   * feeds a JAX-RS {@code @DefaultValue}, which is where the default belongs — the resource and the
   * service must not disagree about it.
   */
  public static final String DEFAULT_PROFILE_SAMPLES = "300";

  @Inject RouteRepository routeRepository;

  @Inject CommentCountLookup commentCountLookup;

  @Inject GpxProcessingService gpxProcessingService;

  @Inject AssetRepository assetRepository;

  @Inject RideRepository rideRepository;

  @Inject TripRepository tripRepository;

  @Override
  protected RouteRepository getRepository() {
    return routeRepository;
  }

  @Override
  protected RouteDetailDto toDto(Route entity) {
    return RouteDetailDto.from(
        entity, assetService, GeometryOptions.FULL, commentCountLookup.forEntity(entity));
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
   * The route detail, with its tracks decimated as {@code geometry} asks.
   *
   * <p>{@link GeometryOptions#FULL} yields byte for byte what {@link #getDto(String, String)}
   * returns — the decimation is opt-in, and a caller that asks for nothing keeps the stored track.
   */
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.READ)
  public RouteDetailDto getDto(String teamSlug, String entitySlug, GeometryOptions geometry) {
    Team team = teamService.getTeam(teamSlug);
    Route route = findBySlug(team, entitySlug);
    return RouteDetailDto.from(route, assetService, geometry, commentCountLookup.forEntity(route));
  }

  /**
   * The route's elevation profile, resampled to {@code samples} points.
   *
   * <p>One route is loaded and resampled in memory — the sampling deliberately happens after the
   * read, never across a listing: a profile is a detail view, and the jsonb track points it needs
   * are the heaviest column of the whole schema.
   *
   * @param samples the requested resolution, clamped into {@link #MIN_PROFILE_SAMPLES}..{@link
   *     #MAX_PROFILE_SAMPLES} here rather than trusted
   */
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.READ)
  public ElevationProfileDto getElevationProfile(String teamSlug, String entitySlug, int samples) {
    Team team = teamService.getTeam(teamSlug);
    Route route = findBySlug(team, entitySlug);
    return ElevationProfileDto.from(
        route, Math.clamp(samples, MIN_PROFILE_SAMPLES, MAX_PROFILE_SAMPLES));
  }

  /**
   * List the rides and trips that reference a route — directly, or through a ride group or trip
   * stage. Each referencing ride/trip appears once, most recent first. The listing is visibility
   * filtered per the caller, so an anonymous visitor only sees public published usages.
   */
  @Transactional
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.READ)
  public RouteUsagesResponse getUsages(String teamSlug, String routeSlug) {
    Team team = teamService.getTeam(teamSlug);
    Route route = findBySlug(team, routeSlug);
    Long routeId = route.getId();
    User user = pedalonsContext.getUserNullable();

    TeamEntityQueryBasic base =
        TeamEntityQueryBasic.builder()
            .domainId(pedalonsContext.getDomainId())
            .pinnedTeamId(pedalonsContext.getPinnedTeamIdNullable())
            .userId(user == null ? null : user.getId())
            .teamIds(Set.of(team.getId()))
            .includeDeleted(false)
            .platformAdmin(isPlatformAdmin())
            .build();

    List<RouteUsageDto> usages = new ArrayList<>();
    rideRepository
        .findByRouteId(base, routeId)
        .forEach(ride -> usages.add(RouteUsageDto.fromRide(ride, routeId)));
    tripRepository
        .findByRouteId(base, routeId)
        .forEach(trip -> usages.add(RouteUsageDto.fromTrip(trip, routeId)));
    usages.sort(Comparator.comparing(RouteUsageDto::dateTime).reversed());
    return new RouteUsagesResponse(usages);
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
      // Phase 1: CPU + S3 (transaction suspended, DB connection released)
      GpxProcessingService.GpxProcessingResult gpxResult =
          gpxProcessingService.processGpxData(route, gpx);

      // Phase 2: DB persist (transaction resumed, short burst)
      TrackMetadata metadata = gpxProcessingService.persistGpxData(route, gpxResult);

      route.setDistance(metadata.distance());
      route.setElevationGain(metadata.elevationGain());
      route.setHilliness(metadata.hilliness());
      route.setElevationLoss(metadata.elevationLoss());
      route.setStart(metadata.start());
      route.setEnd(metadata.end());
      route.setWindDirection(getWindDirection(metadata));

      updateMedia(route, request.media());
      routeRepository.persist(route);
      return RouteDto.from(route, assetService, commentCountLookup.forEntity(route));
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

        // Phase 1: CPU + S3 (transaction suspended, DB connection released)
        GpxProcessingService.GpxProcessingResult gpxResult =
            gpxProcessingService.processGpxData(route, gpx);

        // Phase 2: DB persist (transaction resumed, short burst)
        TrackMetadata metadata = gpxProcessingService.persistGpxData(route, gpxResult);

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
    return RouteDto.from(route, assetService, commentCountLookup.forEntity(route));
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
    return getRoutesWithTeamIds(
        Set.of(team.getId()), pedalonsContext.getUserNullable(), params, isIncludeDeleted(team));
  }

  /**
   * List all routes across all accessible teams with pagination, filtering, and access control.
   */
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.LIST_ALL_TEAMS)
  public RouteListResponse getAllRoutes(RouteSearchParams params) {
    return getRoutesWithTeamIds(null, pedalonsContext.getUserNullable(), params, false);
  }

  /**
   * How many routes {@link #getRoutes} would list, without listing them.
   *
   * <p>Built from the same {@link RouteQuery} as the listing, so the two can never disagree: a
   * filter added to one is a filter the other gets for free.
   */
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.LIST)
  public CountResponse countRoutes(String teamSlug, RouteSearchParams params) {
    Team team = teamService.getTeam(teamSlug);
    return new CountResponse(
        routeRepository.countMatching(
            listQuery(
                Set.of(team.getId()),
                pedalonsContext.getUserNullable(),
                params,
                isIncludeDeleted(team))));
  }

  /** How many routes {@link #getAllRoutes} would list, without listing them. */
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.LIST_ALL_TEAMS)
  public CountResponse countAllRoutes(RouteSearchParams params) {
    return new CountResponse(
        routeRepository.countMatching(
            listQuery(null, pedalonsContext.getUserNullable(), params, false)));
  }

  /**
   * Vector tile of a team's routes, subject to the same access control as {@link #getRoutes}.
   */
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.LIST)
  public byte[] getRoutesTile(String teamSlug, RouteSearchParams params, int z, int x, int y) {
    Team team = teamService.getTeam(teamSlug);
    return tile(Set.of(team.getId()), params, z, x, y);
  }

  /**
   * Vector tile of the routes of every accessible team, subject to the same access control as
   * {@link #getAllRoutes}.
   */
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.LIST_ALL_TEAMS)
  public byte[] getAllRoutesTile(RouteSearchParams params, int z, int x, int y) {
    return tile(null, params, z, x, y);
  }

  /**
   * Bounding box of a team's routes, subject to the same access control as {@link #getRoutes}.
   */
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.LIST)
  public RouteBoundsResponse getRoutesBounds(String teamSlug, RouteSearchParams params) {
    Team team = teamService.getTeam(teamSlug);
    return new RouteBoundsResponse(
        routeRepository.bounds(aggregateQuery(Set.of(team.getId()), params)));
  }

  /**
   * Bounding box of the routes of every accessible team, subject to the same access control as
   * {@link #getAllRoutes}.
   */
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.LIST_ALL_TEAMS)
  public RouteBoundsResponse getAllRoutesBounds(RouteSearchParams params) {
    return new RouteBoundsResponse(routeRepository.bounds(aggregateQuery(null, params)));
  }

  private byte[] tile(@Nullable Set<Long> teamIds, RouteSearchParams params, int z, int x, int y) {
    return routeRepository.mvtTile(aggregateQuery(teamIds, params), z, x, y);
  }

  /**
   * The query behind the tile and the bounds: both fold every matching route into a single row. The
   * sort and the pagination of {@code params} are therefore deliberately dropped — an {@code ORDER
   * BY} would break an aggregate projection, and neither answer is paginated.
   */
  private RouteQuery aggregateQuery(@Nullable Set<Long> teamIds, RouteSearchParams params) {
    User user = pedalonsContext.getUserNullable();
    return RouteQuery.builder()
        .domainId(pedalonsContext.getDomainId())
        .pinnedTeamId(pedalonsContext.getPinnedTeamIdNullable())
        .userId(user == null ? null : user.getId())
        .teamIds(teamIds)
        .search(params.search())
        .minRole(params.minRole())
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
        .includeDeleted(false)
        .platformAdmin(isPlatformAdmin())
        .build();
  }

  /**
   * The query behind the paginated listing and behind the count — one builder, so the "Voir N
   * parcours" figure and the list it opens are answering the same question.
   */
  private RouteQuery listQuery(
      @Nullable Set<Long> teamIds,
      @Nullable User user,
      RouteSearchParams params,
      boolean includeDeleted) {
    return RouteQuery.builder()
        .domainId(pedalonsContext.getDomainId())
        .pinnedTeamId(pedalonsContext.getPinnedTeamIdNullable())
        .userId(user == null ? null : user.getId())
        .teamIds(teamIds)
        .search(params.search())
        .page(params.page())
        .size(params.size())
        .minRole(params.minRole())
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
        .includeDeleted(includeDeleted)
        .platformAdmin(isPlatformAdmin())
        .build();
  }

  private RouteListResponse getRoutesWithTeamIds(
      @Nullable Set<Long> teamIds,
      @Nullable User user,
      RouteSearchParams params,
      boolean includeDeleted) {
    PedalonsPage<Route> routes =
        routeRepository.find(listQuery(teamIds, user, params, includeDeleted));
    // Two queries for the whole page — the comment count of a list row must not cost a round-trip
    // per row, and it is only filled in for teams this caller belongs to.
    CommentCounts commentCounts = commentCountLookup.forEntities(routes.items());
    List<RouteDto> dtos =
        routes.items().stream()
            .map(route -> RouteDto.from(route, assetService, commentCounts, params.view()))
            .toList();
    return new RouteListResponse(dtos, routes.total(), params.page(), params.size());
  }

  /**
   * Delete route (soft delete).
   */
  @Transactional
  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.DELETE)
  public void deleteRoute(String teamSlug, String slug) {
    Team team = teamService.getTeam(teamSlug);
    Route route = findBySlug(team, slug);

    route.setDeleted(true);
    routeRepository.persist(route);
  }

  @CheckAccess(entityType = EntityType.ROUTE, action = ActionType.DELETE)
  @Transactional
  public RouteDetailDto undeleteRoute(String teamSlug, String slug) {
    Team team = teamService.getTeam(teamSlug);
    Route route = findBySlugIncludeDeleted(team, slug);
    route.setDeleted(false);
    routeRepository.persist(route);
    return toDto(route);
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
