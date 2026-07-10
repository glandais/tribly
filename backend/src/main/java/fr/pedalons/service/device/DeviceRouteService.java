package fr.pedalons.service.device;

import fr.pedalons.domain.gps.GpsServiceConnection;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.team.UserTeam;
import fr.pedalons.dto.device.response.DeviceRideDto;
import fr.pedalons.dto.device.response.DeviceRideEntryDto;
import fr.pedalons.dto.device.response.DeviceRouteDto;
import fr.pedalons.dto.device.response.DeviceRoutesResponse;
import fr.pedalons.dto.device.response.DeviceUserStatusResponse;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.GpsServiceType;
import fr.pedalons.enums.Status;
import fr.pedalons.infrastructure.exception.NotFoundException;
import fr.pedalons.infrastructure.timezone.TimezoneService;
import fr.pedalons.repository.common.TeamEntityQueryBasic;
import fr.pedalons.repository.gps.GpsServiceConnectionRepository;
import fr.pedalons.repository.ride.RideRepository;
import fr.pedalons.repository.route.RouteQuery;
import fr.pedalons.repository.route.RouteRepository;
import fr.pedalons.repository.team.UserTeamRepository;
import fr.pedalons.service.route.GpxProcessingService;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Logged;
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
 * Unified service for aggregating routes for device applications (Garmin, Karoo, etc.). Returns
 * upcoming rides (D-1 to D+7) with their route entries, and latest standalone routes independently.
 */
@ApplicationScoped
public class DeviceRouteService {

  private static final int LATEST_ROUTES_PER_TEAM = 10;

  @Inject PedalonsQueryContext pedalonsContext;
  @Inject UserTeamRepository userTeamRepository;
  @Inject RideRepository rideRepository;
  @Inject RouteRepository routeRepository;
  @Inject GpsServiceConnectionRepository gpsServiceConnectionRepository;
  @Inject GpxProcessingService gpxProcessingService;
  @Inject TimezoneService timezoneService;

  /** Internal record for sorting routes by distance while keeping DTO separate. */
  private record RouteWithDistance(DeviceRouteDto dto, @Nullable Double distanceFromUser) {}

  /**
   * Get rides and routes for the authenticated user. Rides contain route entries from upcoming rides
   * (D-1 to D+7). Routes are the latest standalone routes per team. Both lists are independent.
   *
   * @param lat User's latitude (optional, for proximity sorting)
   * @param lon User's longitude (optional, for proximity sorting)
   * @return Response with rides and routes lists
   */
  @Logged
  public DeviceRoutesResponse getRoutesForUser(@Nullable Double lat, @Nullable Double lon) {
    Long userId = pedalonsContext.getUserId();
    List<UserTeam> memberships = userTeamRepository.findByUserId(userId);

    if (memberships.isEmpty()) {
      return DeviceRoutesResponse.builder().rides(List.of()).routes(List.of()).build();
    }

    Instant now = Instant.now();
    Instant from = now.minus(1, ChronoUnit.DAYS);
    Instant to = now.plus(7, ChronoUnit.DAYS);

    List<DeviceRideDto> allRides = new ArrayList<>();
    List<RouteWithDistance> allRoutes = new ArrayList<>();

    for (UserTeam membership : memberships) {
      Team team = membership.getTeam();
      Long teamId = team.getId();
      Set<Long> teamIds = Set.of(teamId);

      // Rides: D-1 to D+7 with route entries
      List<DeviceRideDto> teamRides = getRidesWithEntries(teamIds, userId, from, to);
      allRides.addAll(teamRides);

      // Routes: latest per team (independent of rides)
      List<RouteWithDistance> teamRoutes = getLatestRoutes(teamIds, userId, lat, lon);
      allRoutes.addAll(teamRoutes);
    }

    // Sort rides by proximity to current time
    allRides.sort(
        Comparator.comparingLong(
            r ->
                r.startDateTime() != null
                    ? Math.abs(r.startDateTime().toEpochMilli() - now.toEpochMilli())
                    : Long.MAX_VALUE));

    // Sort routes by distance from user
    allRoutes.sort(
        Comparator.comparingDouble(
            r -> r.distanceFromUser() != null ? r.distanceFromUser() : Double.MAX_VALUE));

    List<DeviceRouteDto> routes =
        allRoutes.stream().map(RouteWithDistance::dto).collect(Collectors.toList());

    return DeviceRoutesResponse.builder().rides(allRides).routes(routes).build();
  }

  /**
   * Get user status including connected GPS services.
   *
   * @return User status with list of connected GPS services
   */
  @Logged
  public DeviceUserStatusResponse getUserStatus() {
    Long userId = pedalonsContext.getUserId();
    List<GpsServiceType> connectedServices =
        gpsServiceConnectionRepository.findByUser(userId).stream()
            .map(GpsServiceConnection::getServiceType)
            .toList();
    return DeviceUserStatusResponse.builder().connectedGpsServices(connectedServices).build();
  }

  private List<DeviceRideDto> getRidesWithEntries(
      Set<Long> teamIds, Long userId, Instant from, Instant to) {

    TeamEntityQueryBasic query =
        TeamEntityQueryBasic.builder()
            .domainId(pedalonsContext.getDomainId())
            .pinnedTeamId(pedalonsContext.getPinnedTeamIdNullable())
            .teamIds(teamIds)
            .userId(userId)
            .from(from)
            .to(to)
            .page(0)
            .size(50)
            .includeDeleted(false)
            .platformAdmin(false)
            .build();

    List<Ride> rides = rideRepository.findAll(query);

    List<Ride> publishedRides =
        rides.stream().filter(r -> r.getStatus() == Status.PUBLISHED).toList();

    List<DeviceRideDto> result = new ArrayList<>();

    for (Ride ride : publishedRides) {
      List<DeviceRideEntryDto> entries = new ArrayList<>();

      // Ride-level route (groupName = null)
      if (ride.getRoute() != null && !ride.getRoute().isDeleted()) {
        entries.add(toRideEntry(ride.getRoute(), null));
      }

      // Group-level routes
      for (RideGroup group : ride.getGroups()) {
        Route groupRoute = group.getRoute();
        if (groupRoute == null || groupRoute.isDeleted()) {
          continue;
        }

        entries.add(toRideEntry(groupRoute, group.getName()));
      }

      // Only include rides that have at least one route entry
      if (!entries.isEmpty()) {
        Instant startInstant = computeRideStartInstant(ride);
        result.add(
            DeviceRideDto.builder()
                .teamSlug(ride.getTeam().getSlug())
                .rideSlug(ride.getSlug())
                .rideName(ride.getName())
                .startDateTime(startInstant)
                .entries(entries)
                .build());
      }
    }

    return result;
  }

  private DeviceRideEntryDto toRideEntry(Route route, @Nullable String groupName) {
    Point<G2D> start = route.getStart();
    G2D position = start.getPosition();

    return DeviceRideEntryDto.builder()
        .routeSlug(route.getSlug())
        .routeName(route.getName())
        .groupName(groupName)
        .distance(route.getDistance())
        .elevationGain(route.getElevationGain())
        .startLat(position.getLat())
        .startLon(position.getLon())
        .build();
  }

  @Nullable
  private Instant computeRideStartInstant(Ride ride) {
    Route route = ride.getRoute();
    if (route == null || route.isDeleted()) {
      // Use first group's route for timezone
      for (RideGroup group : ride.getGroups()) {
        if (group.getRoute() != null && !group.getRoute().isDeleted()) {
          route = group.getRoute();
          break;
        }
      }
    }
    if (route == null) {
      return null;
    }
    Point<G2D> start = route.getStart();
    ZoneId zoneId =
        timezoneService.getZoneId(start.getPosition().getLat(), start.getPosition().getLon());
    ZonedDateTime zonedDateTime = ride.getDateTime().atZone(zoneId);
    return zonedDateTime.toInstant();
  }

  private List<RouteWithDistance> getLatestRoutes(
      Set<Long> teamIds, Long userId, @Nullable Double lat, @Nullable Double lon) {

    RouteQuery query =
        RouteQuery.builder()
            .domainId(pedalonsContext.getDomainId())
            .pinnedTeamId(pedalonsContext.getPinnedTeamIdNullable())
            .teamIds(teamIds)
            .userId(userId)
            .page(0)
            .size(LATEST_ROUTES_PER_TEAM)
            .includeDeleted(false)
            .platformAdmin(false)
            .build();

    List<Route> routes = routeRepository.findAll(query);

    return routes.stream()
        .filter(r -> r.getStatus() == Status.PUBLISHED)
        .map(route -> toRouteWithDistance(route, lat, lon))
        .toList();
  }

  private RouteWithDistance toRouteWithDistance(
      Route route, @Nullable Double userLat, @Nullable Double userLon) {

    Point<G2D> start = route.getStart();
    G2D position = start.getPosition();
    double startLat = position.getLat();
    double startLon = position.getLon();

    Double distanceFromUser = null;
    if (userLat != null && userLon != null) {
      distanceFromUser = haversineDistance(userLat, userLon, startLat, startLon);
    }

    DeviceRouteDto dto =
        DeviceRouteDto.builder()
            .teamSlug(route.getTeam().getSlug())
            .routeSlug(route.getSlug())
            .routeName(route.getName())
            .distance(route.getDistance())
            .elevationGain(route.getElevationGain())
            .startLat(startLat)
            .startLon(startLon)
            .build();

    return new RouteWithDistance(dto, distanceFromUser);
  }

  /** Get FIT content for a specific route. */
  @Logged
  public InputStream getFitContent(String teamSlug, String routeSlug) {
    Route route = findRouteBySlug(teamSlug, routeSlug);
    return gpxProcessingService.getFitContent(route);
  }

  /** Get filtered GPX content for a specific route. */
  @Logged
  public InputStream getGpxContent(String teamSlug, String routeSlug) {
    Route route = findRouteBySlug(teamSlug, routeSlug);
    return gpxProcessingService.getFilteredGpxContent(route);
  }

  /** Find a route by team and route slugs, verifying user membership. */
  private Route findRouteBySlug(String teamSlug, String routeSlug) {
    Long userId = pedalonsContext.getUserId();

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
        .findByTeamAndSlug(domainId, team.getId(), userId, routeSlug, false, false)
        .orElseThrow(() -> new NotFoundException(EntityType.ROUTE, routeSlug));
  }

  /** Calculate Haversine distance between two points in meters. */
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
