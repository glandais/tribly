package com.tribly.service.device;

import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.dto.device.response.DeviceRouteDto;
import com.tribly.dto.device.response.DeviceRoutesResponse;
import com.tribly.enums.EntityType;
import com.tribly.enums.Status;
import com.tribly.infrastructure.exception.NotFoundException;
import com.tribly.infrastructure.timezone.TimezoneService;
import com.tribly.repository.common.TeamEntityQueryBasic;
import com.tribly.repository.ride.RideRepository;
import com.tribly.repository.route.RouteQuery;
import com.tribly.repository.route.RouteRepository;
import com.tribly.repository.team.UserTeamRepository;
import com.tribly.service.route.GpxProcessingService;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.security.annotation.Logged;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.InputStream;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jspecify.annotations.Nullable;

/**
 * Unified service for aggregating routes for device applications (Garmin, Karoo, etc.).
 * Prioritizes routes from upcoming rides, then falls back to latest published routes.
 */
@ApplicationScoped
public class DeviceRouteService {

  private static final int MAX_ROUTES = 20;
  private static final int LATEST_ROUTES_PER_TEAM = 10;

  @Inject TriblyQueryContext triblyContext;
  @Inject UserTeamRepository userTeamRepository;
  @Inject RideRepository rideRepository;
  @Inject RouteRepository routeRepository;
  @Inject GpxProcessingService gpxProcessingService;
  @Inject TimezoneService timezoneService;

  /**
   * Internal record for sorting routes by distance while keeping DTO separate.
   */
  private record RouteWithDistance(DeviceRouteDto dto, @Nullable Double distanceFromUser) {}

  /**
   * Get routes for the authenticated user. Prioritizes routes from upcoming rides, then latest
   * routes.
   *
   * @param lat User's latitude (optional, for proximity sorting)
   * @param lon User's longitude (optional, for proximity sorting)
   * @return List of routes suitable for device applications
   */
  @Logged
  public DeviceRoutesResponse getRoutesForUser(@Nullable Double lat, @Nullable Double lon) {
    Long userId = triblyContext.getUserId();
    List<UserTeam> memberships = userTeamRepository.findByUserId(userId);

    if (memberships.isEmpty()) {
      return DeviceRoutesResponse.builder().routes(List.of()).build();
    }

    List<RouteWithDistance> allRoutes = new ArrayList<>();
    Instant now = Instant.now();
    Instant from = now.minus(1, ChronoUnit.DAYS);
    Instant to = now.plus(7, ChronoUnit.DAYS);

    for (UserTeam membership : memberships) {
      Team team = membership.getTeam();
      Long teamId = team.getId();
      Set<Long> teamIds = Set.of(teamId);

      // Find upcoming rides with routes
      List<RouteWithDistance> rideRoutes = getRoutesFromRides(teamIds, userId, from, to, lat, lon);

      if (!rideRoutes.isEmpty()) {
        allRoutes.addAll(rideRoutes);
      } else {
        // No upcoming rides, get latest routes
        List<RouteWithDistance> latestRoutes = getLatestRoutes(teamIds, userId, lat, lon);
        allRoutes.addAll(latestRoutes);
      }
    }

    // Sort: rides first (by dateTime proximity), then by distance
    allRoutes.sort(
        Comparator
            // Rides first (those with rideDateTime)
            .<RouteWithDistance>comparingInt(r -> r.dto().startDateTime() == null ? 1 : 0)
            // Then by proximity to current time
            .thenComparingLong(
                r ->
                    r.dto().startDateTime() != null
                        ? Math.abs(r.dto().startDateTime().toEpochMilli() - now.toEpochMilli())
                        : Long.MAX_VALUE)
            // Then by distance from user (if provided)
            .thenComparingDouble(
                r -> r.distanceFromUser() != null ? r.distanceFromUser() : Double.MAX_VALUE));

    // Limit results and extract DTOs
    List<DeviceRouteDto> routes =
        allRoutes.stream()
            .limit(MAX_ROUTES)
            .map(RouteWithDistance::dto)
            .collect(Collectors.toList());

    return DeviceRoutesResponse.builder().routes(routes).build();
  }

  private List<RouteWithDistance> getRoutesFromRides(
      Set<Long> teamIds,
      Long userId,
      Instant from,
      Instant to,
      @Nullable Double lat,
      @Nullable Double lon) {

    TeamEntityQueryBasic query =
        TeamEntityQueryBasic.builder()
            .domainId(triblyContext.getDomainId())
            .teamIds(teamIds)
            .userId(userId)
            .from(from)
            .to(to)
            .page(0)
            .size(50)
            .build();

    List<Ride> rides = rideRepository.findAll(query);

    List<Ride> publishedRides =
        rides.stream().filter(r -> r.getStatus() == Status.PUBLISHED).toList();

    List<RouteWithDistance> routes = new ArrayList<>();

    for (Ride ride : publishedRides) {
      boolean oneWithoutGroup = true;
      for (RideGroup group : ride.getGroups()) {
        if (group.isDeleted()) continue;

        Route route = group.getRoute();
        if (route == null || route.isDeleted()) {
          route = ride.getRoute();
        }

        if (route != null && !route.isDeleted()) {
          RouteWithDistance rwd = toRouteWithDistance(route, ride, group, lat, lon);
          routes.add(rwd);
          oneWithoutGroup = false;
        }
      }

      if (oneWithoutGroup && ride.getRoute() != null && !ride.getRoute().isDeleted()) {
        Route route = ride.getRoute();
        RouteWithDistance rwd = toRouteWithDistance(route, ride, null, lat, lon);
        routes.add(rwd);
      }
    }

    return routes;
  }

  private List<RouteWithDistance> getLatestRoutes(
      Set<Long> teamIds, Long userId, @Nullable Double lat, @Nullable Double lon) {

    RouteQuery query =
        RouteQuery.builder()
            .domainId(triblyContext.getDomainId())
            .teamIds(teamIds)
            .userId(userId)
            .page(0)
            .size(LATEST_ROUTES_PER_TEAM)
            .build();

    List<Route> routes = routeRepository.findAll(query);

    return routes.stream()
        .filter(r -> r.getStatus() == Status.PUBLISHED)
        .map(route -> toRouteWithDistance(route, null, null, lat, lon))
        .toList();
  }

  private RouteWithDistance toRouteWithDistance(
      Route route,
      @Nullable Ride ride,
      @Nullable RideGroup rideGroup,
      @Nullable Double userLat,
      @Nullable Double userLon) {

    Double distanceFromUser = null;

    Point<G2D> start = route.getStart();
    G2D position = start.getPosition();
    double startLat = position.getLat();
    double startLon = position.getLon();

    if (userLat != null && userLon != null) {
      distanceFromUser = haversineDistance(userLat, userLon, startLat, startLon);
    }

    Instant startInstant = null;
    if (ride != null) {
      ZoneId zoneId =
          timezoneService.getZoneId(start.getPosition().getLat(), start.getPosition().getLon());
      ZonedDateTime zonedDateTime = ride.getDateTime().atZone(zoneId);
      if (rideGroup != null && rideGroup.getTime() != null) {
        LocalTime groupTime = rideGroup.getTime();
        zonedDateTime =
            zonedDateTime.withHour(groupTime.getHour()).withMinute(groupTime.getMinute());
      }
      startInstant = zonedDateTime.toInstant();
    }

    DeviceRouteDto dto =
        DeviceRouteDto.builder()
            .teamSlug(route.getTeam().getSlug())
            .routeSlug(route.getSlug())
            .routeName(route.getName())
            .rideName(ride == null ? null : ride.getName())
            .groupName(rideGroup == null ? null : rideGroup.getName())
            .startDateTime(startInstant)
            .distance(route.getDistance())
            .elevationGain(route.getElevationGain())
            .startLat(startLat)
            .startLon(startLon)
            .build();

    return new RouteWithDistance(dto, distanceFromUser);
  }

  /**
   * Get FIT content for a specific route.
   */
  @Logged
  public InputStream getFitContent(String teamSlug, String routeSlug) {
    Route route = findRouteBySlug(teamSlug, routeSlug);
    return gpxProcessingService.getFitContent(route);
  }

  /**
   * Get filtered GPX content for a specific route.
   */
  @Logged
  public InputStream getGpxContent(String teamSlug, String routeSlug) {
    Route route = findRouteBySlug(teamSlug, routeSlug);
    return gpxProcessingService.getFilteredGpxContent(route);
  }

  /**
   * Find a route by team and route slugs, verifying user membership.
   */
  private Route findRouteBySlug(String teamSlug, String routeSlug) {
    Long userId = triblyContext.getUserId();

    // Find team by slug from user's memberships
    List<UserTeam> memberships = userTeamRepository.findByUserId(userId);
    Team team =
        memberships.stream()
            .map(UserTeam::getTeam)
            .filter(t -> t.getSlug().equals(teamSlug))
            .findFirst()
            .orElseThrow(() -> new NotFoundException(EntityType.TEAM, teamSlug));

    // Find route (domainId from team for multi-tenant isolation)
    Long domainId = team.getDomain().getId();
    return routeRepository
        .findByTeamAndSlug(domainId, team.getId(), userId, routeSlug)
        .orElseThrow(() -> new NotFoundException(EntityType.ROUTE, routeSlug));
  }

  /**
   * Calculate Haversine distance between two points in meters.
   */
  private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
    final double R = 6371000; // Earth radius in meters

    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);

    double a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return R * c;
  }
}
