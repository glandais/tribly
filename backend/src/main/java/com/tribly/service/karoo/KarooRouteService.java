package com.tribly.service.karoo;

import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.dto.karoo.response.KarooRouteDto;
import com.tribly.dto.karoo.response.KarooRoutesResponse;
import com.tribly.enums.Status;
import com.tribly.repository.common.TeamEntityQueryBasic;
import com.tribly.repository.ride.RideRepository;
import com.tribly.repository.route.RouteQuery;
import com.tribly.repository.route.RouteRepository;
import com.tribly.repository.team.UserTeamRepository;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.security.annotation.Logged;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

/**
 * Service for aggregating routes for Karoo devices. Reuses logic from GarminRouteService but
 * returns lightweight DTOs optimized for Karoo's 100KB response limit.
 */
@ApplicationScoped
public class KarooRouteService {

  private static final Logger LOG = Logger.getLogger(KarooRouteService.class);
  private static final int MAX_ROUTES = 20;
  private static final int LATEST_ROUTES_PER_TEAM = 10;
  private static final DateTimeFormatter DATE_FORMAT =
      DateTimeFormatter.ofPattern("dd/MM HH:mm").withZone(ZoneId.systemDefault());

  @Inject TriblyQueryContext triblyContext;
  @Inject UserTeamRepository userTeamRepository;
  @Inject RideRepository rideRepository;
  @Inject RouteRepository routeRepository;

  /**
   * Get routes for the authenticated user. Prioritizes routes from upcoming rides, then latest
   * routes. Returns lightweight DTOs suitable for Karoo's response size limits.
   *
   * @param lat User's latitude (optional, for proximity sorting)
   * @param lon User's longitude (optional, for proximity sorting)
   * @return List of routes suitable for Karoo device
   */
  @Logged
  public KarooRoutesResponse getRoutesForUser(@Nullable Double lat, @Nullable Double lon) {
    Long userId = triblyContext.getUserId();
    List<UserTeam> memberships = userTeamRepository.findByUserId(userId);

    if (memberships.isEmpty()) {
      return KarooRoutesResponse.builder().routes(List.of()).build();
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
      List<RouteWithDistance> rideRoutes =
          getRoutesFromRides(teamId, teamIds, userId, from, to, lat, lon);

      if (!rideRoutes.isEmpty()) {
        allRoutes.addAll(rideRoutes);
      } else {
        // No upcoming rides, get latest routes
        List<RouteWithDistance> latestRoutes = getLatestRoutes(team, teamIds, userId, lat, lon);
        allRoutes.addAll(latestRoutes);
      }
    }

    // Sort: rides first (by dateTime proximity), then by distance
    Instant sortNow = now;
    allRoutes.sort(
        Comparator
            // Rides first (those with rideDateTime)
            .<RouteWithDistance>comparingInt(r -> r.dto().rideDateTime() == null ? 1 : 0)
            // Then by proximity to current time
            .thenComparingLong(
                r ->
                    r.dto().rideDateTime() != null
                        ? Math.abs(r.dto().rideDateTime().toEpochMilli() - sortNow.toEpochMilli())
                        : Long.MAX_VALUE)
            // Then by distance from user (if provided)
            .thenComparingDouble(r -> r.distanceFromUser() != null ? r.distanceFromUser() : Double.MAX_VALUE));

    // Limit results and extract DTOs (dropping distanceFromUser to save bytes)
    List<KarooRouteDto> routes =
        allRoutes.stream().limit(MAX_ROUTES).map(RouteWithDistance::dto).collect(Collectors.toList());

    return KarooRoutesResponse.builder().routes(routes).build();
  }

  private record RouteWithDistance(KarooRouteDto dto, @Nullable Double distanceFromUser) {}

  private List<RouteWithDistance> getRoutesFromRides(
      Long teamId,
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
      for (RideGroup group : ride.getGroups()) {
        if (group.isDeleted()) continue;

        Route route = group.getRoute();
        if (route == null || route.isDeleted()) {
          route = ride.getRoute();
        }

        if (route != null && !route.isDeleted()) {
          String label = formatRideLabel(group.getName(), ride.getDateTime());
          RouteWithDistance rwd = toDto(route, label, ride.getDateTime(), lat, lon);
          routes.add(rwd);
        }
      }

      if (routes.isEmpty() && ride.getRoute() != null && !ride.getRoute().isDeleted()) {
        String label = formatRideLabel(ride.getName(), ride.getDateTime());
        RouteWithDistance rwd = toDto(ride.getRoute(), label, ride.getDateTime(), lat, lon);
        routes.add(rwd);
      }
    }

    return routes;
  }

  private List<RouteWithDistance> getLatestRoutes(
      Team team, Set<Long> teamIds, Long userId, @Nullable Double lat, @Nullable Double lon) {

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
        .map(route -> toDto(route, null, null, lat, lon))
        .toList();
  }

  private RouteWithDistance toDto(
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

    KarooRouteDto dto =
        KarooRouteDto.builder()
            .teamSlug(route.getTeam().getSlug())
            .routeSlug(route.getSlug())
            .name(route.getName())
            .label(label)
            .rideDateTime(rideDateTime)
            .distance(route.getDistance() != null ? route.getDistance() : 0f)
            .elevationGain(route.getElevationGain() != null ? route.getElevationGain() : 0f)
            .startLat(startLat)
            .startLon(startLon)
            .build();

    return new RouteWithDistance(dto, distanceFromUser);
  }

  private String formatRideLabel(String groupName, Instant dateTime) {
    String date = DATE_FORMAT.format(dateTime);
    return groupName + " - " + date;
  }

  private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
    final double R = 6371000;

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
