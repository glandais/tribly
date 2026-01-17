package com.tribly.service.garmin;

import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.dto.garmin.response.GarminRouteDto;
import com.tribly.dto.garmin.response.GarminRoutesResponse;
import com.tribly.enums.EntityType;
import com.tribly.enums.Status;
import com.tribly.infrastructure.exception.NotFoundException;
import com.tribly.repository.common.TeamEntityQueryBasic;
import com.tribly.repository.ride.RideRepository;
import com.tribly.repository.route.RouteQuery;
import com.tribly.repository.route.RouteRepository;
import com.tribly.repository.team.UserTeamRepository;
import com.tribly.service.route.GpxProcessingService;
import com.tribly.service.security.TriblyQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

/**
 * Service for aggregating routes for Garmin devices. Prioritizes routes from upcoming rides, then
 * falls back to latest published routes.
 */
@ApplicationScoped
public class GarminRouteService {

  private static final Logger LOG = Logger.getLogger(GarminRouteService.class);
  private static final int MAX_ROUTES = 20;
  private static final int LATEST_ROUTES_PER_TEAM = 10;
  private static final DateTimeFormatter DATE_FORMAT =
      DateTimeFormatter.ofPattern("EEE d MMM HH:mm").withZone(ZoneId.systemDefault());

  @Inject TriblyQueryContext triblyContext;
  @Inject UserTeamRepository userTeamRepository;
  @Inject RideRepository rideRepository;
  @Inject RouteRepository routeRepository;
  @Inject GpxProcessingService gpxProcessingService;

  /**
   * Get routes for the authenticated user. Prioritizes routes from upcoming rides, then latest
   * routes.
   *
   * @param lat User's latitude (optional, for proximity sorting)
   * @param lon User's longitude (optional, for proximity sorting)
   * @return List of routes suitable for Garmin device
   */
  public GarminRoutesResponse getRoutesForUser(@Nullable Double lat, @Nullable Double lon) {
    Long userId = triblyContext.getUserId();
    List<UserTeam> memberships = userTeamRepository.findByUserId(userId);

    if (memberships.isEmpty()) {
      return GarminRoutesResponse.builder().routes(List.of()).build();
    }

    List<GarminRouteDto> allRoutes = new ArrayList<>();
    Instant now = Instant.now();
    Instant from = now.minus(1, ChronoUnit.DAYS);
    Instant to = now.plus(7, ChronoUnit.DAYS);

    for (UserTeam membership : memberships) {
      Team team = membership.getTeam();
      Long teamId = team.getId();
      Set<Long> teamIds = Set.of(teamId);

      // Find upcoming rides with routes
      List<GarminRouteDto> rideRoutes =
          getRoutesFromRides(teamId, teamIds, userId, from, to, lat, lon);

      if (!rideRoutes.isEmpty()) {
        allRoutes.addAll(rideRoutes);
      } else {
        // No upcoming rides, get latest routes
        List<GarminRouteDto> latestRoutes = getLatestRoutes(team, teamIds, userId, lat, lon);
        allRoutes.addAll(latestRoutes);
      }
    }

    // Sort: rides first (by dateTime proximity), then by distance
    Instant sortNow = now;
    allRoutes.sort(
        Comparator
            // Rides first (those with rideDateTime)
            .<GarminRouteDto>comparingInt(r -> r.rideDateTime() == null ? 1 : 0)
            // Then by proximity to current time
            .thenComparingLong(
                r ->
                    r.rideDateTime() != null
                        ? Math.abs(r.rideDateTime().toEpochMilli() - sortNow.toEpochMilli())
                        : Long.MAX_VALUE)
            // Then by distance from user (if provided)
            .thenComparingDouble(
                r -> r.distanceFromUser() != null ? r.distanceFromUser() : Double.MAX_VALUE));

    // Limit results
    List<GarminRouteDto> routes = allRoutes.stream().limit(MAX_ROUTES).collect(Collectors.toList());

    return GarminRoutesResponse.builder().routes(routes).build();
  }

  private List<GarminRouteDto> getRoutesFromRides(
      Long teamId,
      Set<Long> teamIds,
      Long userId,
      Instant from,
      Instant to,
      @Nullable Double lat,
      @Nullable Double lon) {

    // Query rides in date range
    TeamEntityQueryBasic query =
        TeamEntityQueryBasic.builder()
            .teamIds(teamIds)
            .userId(userId)
            .from(from)
            .to(to)
            .page(0)
            .size(50)
            .build();

    List<Ride> rides = rideRepository.findAll(query);

    // Filter to published rides only
    List<Ride> publishedRides =
        rides.stream().filter(r -> r.getStatus() == Status.PUBLISHED).toList();

    List<GarminRouteDto> routes = new ArrayList<>();

    for (Ride ride : publishedRides) {
      // Get routes from ride groups
      for (RideGroup group : ride.getGroups()) {
        if (group.isDeleted()) continue;

        Route route = group.getRoute();
        if (route == null || route.isDeleted()) {
          // Fall back to ride-level route
          route = ride.getRoute();
        }

        if (route != null && !route.isDeleted()) {
          String label = formatRideLabel(group.getName(), ride.getDateTime());
          GarminRouteDto dto = toDto(route, label, ride.getDateTime(), lat, lon);
          routes.add(dto);
        }
      }

      // If ride has no groups with routes, check ride-level route
      if (routes.isEmpty() && ride.getRoute() != null && !ride.getRoute().isDeleted()) {
        String label = formatRideLabel(ride.getName(), ride.getDateTime());
        GarminRouteDto dto = toDto(ride.getRoute(), label, ride.getDateTime(), lat, lon);
        routes.add(dto);
      }
    }

    return routes;
  }

  private List<GarminRouteDto> getLatestRoutes(
      Team team, Set<Long> teamIds, Long userId, @Nullable Double lat, @Nullable Double lon) {

    RouteQuery query =
        RouteQuery.builder()
            .teamIds(teamIds)
            .userId(userId)
            .page(0)
            .size(LATEST_ROUTES_PER_TEAM)
            .build();

    List<Route> routes = routeRepository.findAll(query);

    return routes.stream()
        .filter(r -> r.getStatus() == Status.PUBLISHED)
        .map(route -> toDto(route, null, null, lat, lon))
        .toList();
  }

  private GarminRouteDto toDto(
      Route route,
      @Nullable String label,
      @Nullable Instant rideDateTime,
      @Nullable Double userLat,
      @Nullable Double userLon) {

    Double startLat = null;
    Double startLon = null;
    Double distanceFromUser = null;

    Point<G2D> start = route.getStart();
    if (start != null) {
      G2D position = start.getPosition();
      startLat = position.getLat();
      startLon = position.getLon();

      if (userLat != null && userLon != null) {
        distanceFromUser = haversineDistance(userLat, userLon, startLat, startLon);
      }
    }

    return GarminRouteDto.builder()
        .teamSlug(route.getTeam().getSlug())
        .routeSlug(route.getSlug())
        .name(route.getName())
        .label(label)
        .rideDateTime(rideDateTime)
        .distance(route.getDistance() != null ? route.getDistance() : 0f)
        .elevationGain(route.getElevationGain() != null ? route.getElevationGain() : 0f)
        .startLat(startLat)
        .startLon(startLon)
        .distanceFromUser(distanceFromUser)
        .build();
  }

  private String formatRideLabel(String groupName, Instant dateTime) {
    String date = DATE_FORMAT.format(dateTime);
    return groupName + " - " + date;
  }

  /**
   * Get FIT file for a specific route.
   */
  public File getFitFile(String teamSlug, String routeSlug) {
    Long userId = triblyContext.getUserId();

    // Find team by slug
    List<UserTeam> memberships = userTeamRepository.findByUserId(userId);
    Team team =
        memberships.stream()
            .map(UserTeam::getTeam)
            .filter(t -> t.getSlug().equals(teamSlug))
            .findFirst()
            .orElseThrow(() -> new NotFoundException(EntityType.TEAM, teamSlug));

    // Find route (domainId from team for multi-tenant isolation)
    Long domainId = team.getDomain().getId();
    Route route =
        routeRepository
            .findByTeamAndSlug(domainId, team.getId(), userId, routeSlug)
            .orElseThrow(() -> new NotFoundException(EntityType.ROUTE, routeSlug));

    return gpxProcessingService.getFitFile(route);
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
