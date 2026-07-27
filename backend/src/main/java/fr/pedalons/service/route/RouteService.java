package fr.pedalons.service.route;

import fr.pedalons.common.GeoPoint;
import fr.pedalons.common.exception.BadRequestException;
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
import fr.pedalons.dto.routes.response.BoundsDto;
import fr.pedalons.dto.routes.response.ElevationProfileDto;
import fr.pedalons.dto.routes.response.RouteBoundsResponse;
import fr.pedalons.dto.routes.response.RouteDetailDto;
import fr.pedalons.dto.routes.response.RouteDto;
import fr.pedalons.dto.routes.response.RouteListResponse;
import fr.pedalons.dto.routes.response.RouteUsageDto;
import fr.pedalons.dto.routes.response.RouteUsagesResponse;
import fr.pedalons.dto.routes.response.RoutesBulkResponse;
import fr.pedalons.dto.routes.response.TrackDto;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.geolatte.geom.G3DM;
import org.geolatte.geom.PositionSequence;
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

  /**
   * A route's track points are the heaviest column of the schema; a batch beyond this is not "a
   * few routes for one screen" any more, it is a listing wearing a detail endpoint's clothes.
   */
  public static final int MAX_BULK_SLUGS = 50;

  /**
   * The per-route point budget a multi-route bulk request is held to, regardless of what geometry
   * the caller asks for.
   *
   * <p>Decimation on {@link #getDto(String, String, GeometryOptions)} is opt-in — {@link
   * GeometryOptions#FULL} means the stored track, untouched — which is fine for one route at a
   * time. It is not fine for {@link #getRoutesBulk}: the slug cap only bounds the number of rows,
   * not the work each row costs, and there is no read-path rate limiting in front of this endpoint
   * ({@code @PermitAll}). Applying this only as the default for "nothing asked" is not enough: an
   * anonymous caller can just as well request a huge {@code points} (e.g. 100000, still inside
   * {@link GeometryOptions#MAX_POINTS}) or a near-zero {@code simplify} tolerance (which keeps
   * essentially every point, and on top forces a Douglas–Peucker pass over the whole stored track)
   * to get effectively the same *full* tracks back — one point every ~10 m, potentially a million
   * points and hundreds of MB of JSON in one response, across up to {@link #MAX_BULK_SLUGS}
   * routes. So this is a hard ceiling on the effective {@code maxPoints}: once the batch resolves
   * to more than one distinct route, whatever {@code maxPoints} the caller's {@link
   * GeometryOptions} resolves to is clamped down to this value (unset counts as unlimited, so it
   * is clamped too). A single-route batch is exempt and keeps behaving exactly like {@code GET
   * /{routeSlug}}, so nothing regresses there.
   *
   * <p>This is a budget per ROUTE, not per track — a {@link fr.pedalons.domain.route.Route} can
   * hold any number of {@link fr.pedalons.domain.route.GpxTrack}s (one per GPX {@code trk}/{@code
   * rte} element, uncapped at import), and {@link
   * fr.pedalons.dto.routes.response.RouteDetailDto#from} hands the same {@link GeometryOptions}
   * to every one of them independently. Left as a flat clamp, a route with N tracks would return
   * up to N times this value — see {@link #perRouteBudget} for the division that keeps the *sum*
   * within this bound instead.
   */
  public static final int DEFAULT_BULK_MAX_POINTS_PER_ROUTE = 1_000;

  /**
   * The floor {@link #DEFAULT_BULK_MAX_POINTS_PER_ROUTE}'s clamp never goes below, even when the
   * caller's own {@code points} asked for less.
   *
   * <p>{@link TrackGeometry#decimateTo} now honours a small {@code maxPoints} correctly (a
   * request for 1 or 2 comes back as the two endpoints, not the untouched source — see its
   * javadoc), so this floor is no longer load-bearing for that: it exists purely so a multi-route
   * batch's line is never reduced to bare endpoints just because the caller passed a tiny {@code
   * points}, and is deliberately a notch above {@link GeometryOptions#MIN_POINTS} (2) for that
   * reason.
   */
  private static final int MIN_EFFECTIVE_BULK_MAX_POINTS = 3;

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
   * The detail of every requested slug the caller may read, one query for the whole batch.
   *
   * <p>No {@code @CheckAccess} here: unlike every other method of this service, the caller passes
   * a <em>list</em> of slugs, which {@code RouteAccessChecker} (built around a single positional
   * slug parameter) cannot express. Read authorization instead comes straight from {@link
   * RouteRepository#findByTeamAndSlugs}, which reuses the exact visibility clauses {@link
   * #findBySlug} would apply to any one of those slugs — the same SQL predicate the single-route
   * {@code READ} check ultimately delegates to (see {@code RouteAccessChecker}, "filtering done via
   * SQL"). A slug that is unknown or unreadable simply is not among the rows that query returns, so
   * it silently drops out of the answer rather than 404ing the whole batch.
   *
   * <p>The response is reordered to follow {@code slugs} (first occurrence, duplicates collapsed)
   * rather than whatever order the database returned rows in — the natural contract for a caller
   * that is fetching several named routes at once.
   *
   * <p>Only track points are budgeted this way. {@link
   * fr.pedalons.dto.routes.response.RouteDetailDto#waypoints() Waypoints} and {@link
   * fr.pedalons.dto.routes.response.TrackDto#climbs() climbs} are left uncapped on purpose: both
   * are stored once at GPX import time and returned as-is, with no per-request knob (unlike
   * track points, which are decimated on every read) — so unlike the track-point budget above,
   * there is nothing here for a caller to defeat by asking for more. Neither amplifies the way
   * track points did either: a waypoint is one GPX {@code <wpt>} in, one {@code WaypointDto} out
   * (no ~10 m resampling multiplying it up), and a climb is a real, threshold-gated feature of
   * the elevation profile, not one emitted per point. Both do still scale with a route's track
   * count — the same lever {@link #perRouteBudget} closes for points — but at roughly 1:1 with
   * the bytes an authenticated uploader already had to supply to create them (unlike points,
   * where ~30 KB of GPX resamples into hundreds of MB), so bounding them here would not close a
   * comparable amplification, only add complexity.
   *
   * @param slugs capped at {@link #MAX_BULK_SLUGS}: the track points column this walks is the
   *     heaviest of the schema, once per route in the batch
   * @param geometry the decimation the caller asked for. Once the batch resolves to more than one
   *     distinct route, the effective {@code maxPoints} is clamped to at most {@link
   *     #DEFAULT_BULK_MAX_POINTS_PER_ROUTE} <em>per route, summed across all of that route's
   *     tracks</em> (see {@link #perRouteBudget}) regardless of what was requested: unset is
   *     treated as unlimited and clamped down to the budget, an explicit request above it is
   *     capped to it, and a {@code simplify} tolerance — however small — composes with the clamp
   *     rather than replacing it, so it cannot be used to dodge it. A single-route batch is
   *     exempt and keeps returning exactly what {@link #getDto(String, String, GeometryOptions)}
   *     would for the same {@code geometry}
   */
  public RoutesBulkResponse getRoutesBulk(
      String teamSlug,
      List<String> slugs,
      GeometryOptions geometry,
      boolean elevation,
      int elevationSamples) {
    if (slugs.size() > MAX_BULK_SLUGS) {
      throw new BadRequestException(ErrorCode.BAD_REQUEST);
    }
    // Resolved even for an empty slug list: every other endpoint under this team-scoped path
    // 404s on an unknown team regardless of its other parameters, and this one must not be the
    // exception just because it has nothing else to look up.
    Team team = teamService.getTeam(teamSlug);
    if (slugs.isEmpty()) {
      return new RoutesBulkResponse(List.of(), null);
    }

    Long domainId = pedalonsContext.getDomainId();
    Long userId = pedalonsContext.getUserIdNullable();
    boolean includeDeleted = isIncludeDeleted(team);
    boolean platformAdmin = isPlatformAdmin();

    Set<String> distinctSlugs = Set.copyOf(slugs);
    List<Route> routes =
        routeRepository.findByTeamAndSlugs(
            domainId, team.getId(), userId, distinctSlugs, includeDeleted, platformAdmin);

    // Two queries for the whole batch — the comment count of a bulk row must not cost a
    // round-trip per row, same rule as any other page.
    CommentCounts commentCounts = commentCountLookup.forEntities(routes);

    // See DEFAULT_BULK_MAX_POINTS_PER_ROUTE. Past one *resolved* route — routes.size(), not
    // distinctSlugs.size(): ?slug=real&slug=ghost must behave like the single-route endpoint too,
    // same as ?slug=real alone — the effective point budget is clamped, not defaulted only when
    // the caller asked for nothing. Gating this on geometry.isFull() (a prior version did) let a
    // caller who named ANY knob — an oversized `points`, or a near-zero `simplify` — skip the
    // clamp entirely, since isFull() only tests "did the caller pass nothing", not "how much did
    // it resolve to". min(requested, budget), "unset" (0, meaning "not asked") treated as
    // unlimited. This is a per-ROUTE total, not per-track yet — perRouteBudget() divides it across
    // each route's own tracks below, once we know how many each has. See
    // MIN_EFFECTIVE_BULK_MAX_POINTS's javadoc for why this floors at 3, not GeometryOptions
    // .MIN_POINTS (2).
    GeometryOptions effectiveGeometry = geometry;
    if (routes.size() > 1) {
      int requestedMaxPoints = geometry.maxPoints();
      int cappedMaxPoints =
          requestedMaxPoints <= 0
              ? DEFAULT_BULK_MAX_POINTS_PER_ROUTE
              : Math.min(
                  Math.max(requestedMaxPoints, MIN_EFFECTIVE_BULK_MAX_POINTS),
                  DEFAULT_BULK_MAX_POINTS_PER_ROUTE);
      effectiveGeometry = new GeometryOptions(geometry.simplifyMeters(), cappedMaxPoints);
    }

    int clampedSamples = Math.clamp(elevationSamples, MIN_PROFILE_SAMPLES, MAX_PROFILE_SAMPLES);
    // effectiveGeometry above is a per-ROUTE budget; perRouteBudget divides it across each
    // route's own tracks so the clamp bounds the SUM a route returns, not each track
    // independently — see that method's javadoc. Only worth computing past one route: a
    // single-route batch keeps returning exactly geometry (== effectiveGeometry here), same as
    // the single-route endpoint, tracks and all.
    boolean multiRoute = routes.size() > 1;
    Map<String, RouteDetailDto> bySlug = new LinkedHashMap<>();
    for (Route route : routes) {
      GeometryOptions routeGeometry =
          multiRoute
              ? perRouteBudget(effectiveGeometry, route.getTracks().size())
              : effectiveGeometry;
      RouteDetailDto dto = RouteDetailDto.from(route, assetService, routeGeometry, commentCounts);
      if (elevation) {
        dto = dto.withElevationProfile(ElevationProfileDto.from(route, clampedSamples));
      }
      bySlug.put(route.getSlug(), dto);
    }

    List<RouteDetailDto> ordered =
        slugs.stream().distinct().map(bySlug::get).filter(Objects::nonNull).toList();

    return new RoutesBulkResponse(ordered, computeExtent(ordered));
  }

  /**
   * {@code geometry}'s {@code maxPoints}, divided across one route's tracks so a multi-track
   * route cannot multiply {@link #DEFAULT_BULK_MAX_POINTS_PER_ROUTE} by its track count.
   *
   * <p>A {@link Route} holds one {@link fr.pedalons.domain.route.GpxTrack} per GPX {@code
   * trk}/{@code rte} element, and nothing caps how many a route may have. {@link
   * fr.pedalons.dto.routes.response.RouteDetailDto#from} maps every track through {@link
   * fr.pedalons.dto.routes.response.TrackDto#from(fr.pedalons.domain.route.GpxTrack,
   * GeometryOptions)} with the *same* {@link GeometryOptions} for each — so left undivided, a
   * route with N real, densely-resampled tracks returns up to N times the promised per-route
   * budget from a single GPX upload: no extra bytes of input per extra point of output, unlike
   * every other size dimension of this response. Dividing the budget by the track count closes
   * that for free, at ~10 km/track (the concrete case this method exists for) landing squarely
   * inside the budget.
   *
   * <p>Floors each track's share at {@link GeometryOptions#MIN_POINTS} (2) so every track still
   * renders as a line and not a single dot. That floor means a route with more tracks than
   * {@code geometry.maxPoints() / GeometryOptions.MIN_POINTS} still returns more than the
   * nominal budget — every track keeps its two endpoints regardless of how thin its share gets.
   * That residual is accepted rather than engineered away: unlike a dense track's points, a raw
   * {@code <trk>} element is not free to manufacture — getting there costs an attacker roughly
   * proportional XML per point ultimately returned (one {@code <trkpt>} per track at minimum),
   * nothing like the amplification this method closes for a realistic upload. See {@code
   * RouteBulkResourceTest#getRoutesBulk_withAManyTracksRouteAndMoreThanOneRoute_shouldBoundTheTotalPerRoute}
   * for the numbers.
   *
   * <p>A single-track route — by far the common case — gets {@code geometry} back unchanged.
   *
   * @param trackCount 1 or fewer returns {@code geometry} untouched
   */
  private static GeometryOptions perRouteBudget(GeometryOptions geometry, int trackCount) {
    if (trackCount <= 1 || geometry.maxPoints() <= 0) {
      return geometry;
    }
    int perTrackBudget = Math.max(GeometryOptions.MIN_POINTS, geometry.maxPoints() / trackCount);
    return new GeometryOptions(geometry.simplifyMeters(), perTrackBudget);
  }

  /**
   * The bounding box of every track {@code routes} carries, computed in memory — exactly what
   * {@link #getRoutesBulk} already loaded, so this costs no extra query. Mirrors {@code
   * RouteRepository.bounds}, which computes the same box in SQL for a filtered listing; this one
   * instead walks the (possibly decimated) geometry actually being returned, because a simplified
   * line's extent is not guaranteed to equal the stored track's.
   *
   * <p>Deliberately ignores waypoints, even though both clients do render them (the web's {@code
   * WaypointMarker} in {@code RouteTrackMap.tsx}, the waypoint dots and labels in mobile's {@code
   * route_map.dart}): {@code RoutesBulkResponse.extent} exists so a client can fit its map directly
   * on the track geometry it was just handed, and a waypoint imported from a GPX for a meeting
   * cafe or a car park can sit tens of km off the actual loop — including it would widen the
   * extent around geometry the track itself never reaches.
   */
  private static @Nullable BoundsDto computeExtent(List<RouteDetailDto> routes) {
    double minLon = Double.POSITIVE_INFINITY;
    double minLat = Double.POSITIVE_INFINITY;
    double maxLon = Double.NEGATIVE_INFINITY;
    double maxLat = Double.NEGATIVE_INFINITY;
    boolean any = false;
    for (RouteDetailDto route : routes) {
      for (TrackDto track : route.tracks()) {
        PositionSequence<G3DM> positions = track.line().getPositions();
        for (int i = 0; i < positions.size(); i++) {
          G3DM p = positions.getPositionN(i);
          minLon = Math.min(minLon, p.getLon());
          maxLon = Math.max(maxLon, p.getLon());
          minLat = Math.min(minLat, p.getLat());
          maxLat = Math.max(maxLat, p.getLat());
          any = true;
        }
      }
    }
    return any ? new BoundsDto(minLon, minLat, maxLon, maxLat) : null;
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
