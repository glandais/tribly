package com.tribly.service.ride;

import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.place.Place;
import com.tribly.domain.place.repository.PlaceRepository;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.ride.RideParticipation;
import com.tribly.domain.ride.repository.RideGroupRepository;
import com.tribly.domain.ride.repository.RideParticipationRepository;
import com.tribly.domain.ride.repository.RideRepository;
import com.tribly.domain.route.Route;
import com.tribly.domain.route.repository.RouteRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.rides.request.GroupRequest;
import com.tribly.dto.rides.request.RideRequest;
import com.tribly.dto.rides.response.*;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.common.TeamEntityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class RideService extends TeamEntityService {

  private static final Logger LOG = Logger.getLogger(RideService.class);

  @Inject RideRepository rideRepository;

  @Inject RideGroupRepository rideGroupRepository;

  @Inject RideParticipationRepository participationRepository;

  @Inject RouteRepository routeRepository;

  @Inject PlaceRepository placeRepository;

  public RideListResponse listRides(
      String teamSlug,
      @Nullable Long userId,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    TriblyPage<Ride> rides =
        rideRepository.find(
            TeamEntityQueryBasic.builder()
                .userId(userId)
                .teamSlugs(Set.of(teamSlug))
                .search(search)
                .from(from)
                .to(to)
                .page(page)
                .size(size)
                .build());
    List<RideDto> dtos =
        rides.items().stream().map(r -> RideDto.from(r, false, assetService)).toList();
    return new RideListResponse(dtos, rides.total(), page, size);
  }

  protected Ride getRide(String teamSlug, String rideSlug, @Nullable Long userId) {
    TriblyPage<Ride> rides =
        rideRepository.find(
            TeamEntityQueryBasic.builder()
                .userId(userId)
                .teamSlugs(Set.of(teamSlug))
                .slug(rideSlug)
                .page(0)
                .size(1)
                .build());
    if (rides.items().isEmpty()) {
      throw BusinessException.notFound("Ride", rideSlug);
    } else {
      return rides.items().getFirst();
    }
  }

  public RideDto getRideDetail(String teamSlug, String rideSlug, @Nullable Long userId) {
    return RideDto.from(getRide(teamSlug, rideSlug, userId), true, assetService);
  }

  @Transactional
  public RideDto createRide(String teamSlug, RideRequest request, Long creatorId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    User creator =
        userRepository
            .findActiveById(creatorId)
            .orElseThrow(() -> BusinessException.notFound("User", creatorId));

    // Security check: must be admin or organizer to create rides
    securityService.requireOrganizer(creatorId, team.getSlug());

    // Validate visibility: private teams can only have team-only rides
    Visibility visibility = request.visibility();
    if (team.getVisibility() != Visibility.PUBLIC && visibility == Visibility.PUBLIC) {
      throw BusinessException.validation("Private teams can only have team-only rides");
    }

    // Generate slug from name, ensure unique within team
    String slug =
        slugService.generateSlug(
            request.name(), s -> rideRepository.existsByTeamAndSlug(team.getId(), s));

    Route route = getRoute(request.routeSlug(), team, visibility);
    Place startPlace = getPlace(request.startPlaceId(), team);
    Place endPlace = getPlace(request.endPlaceId(), team);

    Ride ride = new Ride(creator, team, request.dateTime(), request.name(), slug, visibility);
    ride.setRoute(route);
    ride.setStart(startPlace);
    ride.setEnd(endPlace);
    ride.setStatus(request.status());
    ride.setPublishAt(request.publishAt());

    rideRepository.persistAndFlush(ride);

    updateMedia(ride, request.media());
    rideRepository.persist(ride);

    int sortOrder = 0;
    for (GroupRequest groupRequest : request.groups()) {
      createRideGroup(creator, ride, groupRequest, sortOrder);
      sortOrder++;
    }

    LOG.infov("Ride '{0}' created by user {1} for team {2}", ride.getName(), creatorId, teamSlug);
    return RideDto.from(ride, true, assetService);
  }

  private void createRideGroup(User user, Ride ride, GroupRequest groupRequest, int sortOrder) {
    RideGroup group = new RideGroup(user, ride, groupRequest.name());
    setProperties(ride, group, groupRequest, sortOrder);
    ride.addGroup(group);
    rideGroupRepository.persist(group);
  }

  private void setProperties(Ride ride, RideGroup group, GroupRequest groupRequest, int sortOrder) {
    group.setRide(ride);
    group.setName(groupRequest.name());
    group.setTime(groupRequest.time());
    Route groupRoute = getRoute(groupRequest.routeSlug(), ride.getTeam(), ride.getVisibility());
    group.setAverageSpeed(groupRequest.averageSpeed());
    group.setMaxParticipants(groupRequest.maxParticipants());
    group.setSortOrder(sortOrder);
    group.setRoute(groupRoute);
  }

  private @Nullable Route getRoute(@Nullable String routeSlug, Team team, Visibility visibility) {
    Route route = null;
    if (routeSlug != null) {
      route =
          routeRepository
              .findByTeamAndSlug(team.getId(), routeSlug)
              .orElseThrow(() -> BusinessException.notFound("Route not found"));
      if (visibility == Visibility.PUBLIC && route.getVisibility() != Visibility.PUBLIC) {
        throw BusinessException.businessRule(
            "Can't use private route on public ride", "PUBLIC_RIDE_PRIVATE_ROUTE");
      }
    }
    return route;
  }

  private @Nullable Place getPlace(@Nullable String placeId, Team team) {
    if (placeId == null) {
      return null;
    }
    Long id = TsidUtils.toLong(placeId);
    return placeRepository
        .findByIdAndTeam(id, team.getId())
        .orElseThrow(() -> BusinessException.notFound("Place", placeId));
  }

  @Transactional
  public RideDto updateRide(String teamSlug, String rideSlug, RideRequest request, Long userId) {
    Ride ride = getRide(teamSlug, rideSlug, userId);

    // Security check: must be admin or creator (if organizer) to edit
    securityService.requireOrganizer(userId, teamSlug);

    User user =
        userRepository
            .findActiveById(userId)
            .orElseThrow(() -> BusinessException.notFound("User", userId));

    // Validate visibility: private teams can only have team-only rides
    Team team = ride.getTeam();
    if (team.getVisibility() != Visibility.PUBLIC && request.visibility() == Visibility.PUBLIC) {
      throw BusinessException.validation("Private teams can only have team-only rides");
    }
    ride.setVisibility(request.visibility());

    ride.setName(request.name());
    ride.setDateTime(request.dateTime());
    ride.setStatus(request.status());
    Route route = getRoute(request.routeSlug(), ride.getTeam(), request.visibility());
    ride.setRoute(route);
    Place startPlace = getPlace(request.startPlaceId(), team);
    Place endPlace = getPlace(request.endPlaceId(), team);
    ride.setStart(startPlace);
    ride.setEnd(endPlace);
    // publishAt can be explicitly set to null to remove scheduled publishing
    ride.setPublishAt(request.publishAt());

    updateMedia(ride, request.media());

    Map<Long, RideGroup> existingGroups =
        ride.getGroups().stream().collect(Collectors.toMap(RideGroup::getId, Function.identity()));
    for (RideGroup group : ride.getGroups()) {
      group.setDeleted(true);
      group.setSortOrder(0);
    }
    int sortOrder = 0;
    for (GroupRequest groupRequest : request.groups()) {
      Long groupId = TsidUtils.toLongNullable(groupRequest.id());
      if (groupId == null) {
        createRideGroup(user, ride, groupRequest, sortOrder);
      } else {
        RideGroup existingRideGroup = existingGroups.remove(groupId);
        if (existingRideGroup != null) {
          existingRideGroup.setDeleted(false);
          setProperties(ride, existingRideGroup, groupRequest, sortOrder);
        } else {
          throw BusinessException.notFound("Group", groupRequest.id());
        }
      }
      sortOrder++;
    }

    rideRepository.persist(ride);

    LOG.infov("Ride {0} updated by user {1}", rideSlug, userId);
    return RideDto.from(ride, true, assetService);
  }

  @Transactional
  public void deleteRide(String teamSlug, String rideSlug, Long userId) {
    Ride ride = getRide(teamSlug, rideSlug, userId);

    // Security check: must be admin or creator (if organizer) to delete
    securityService.requireOrganizer(userId, teamSlug);

    ride.setDeleted(true);
    rideRepository.persist(ride);
    LOG.infov("Ride {0} deleted by user {1}", rideSlug, userId);
  }

  @Transactional
  public RideParticipationDto joinGroup(
      String teamSlug, String rideSlug, Long groupId, Long userId) {
    Ride ride = getRide(teamSlug, rideSlug, userId);

    if (ride.getStatus() != Status.PUBLISHED) {
      throw BusinessException.validation("Can only join published rides");
    }

    RideGroup group =
        rideGroupRepository
            .findByIdAndRide(groupId, ride.getId())
            .orElseThrow(() -> BusinessException.notFound("Group", groupId));

    User user =
        userRepository
            .findActiveById(userId)
            .orElseThrow(() -> BusinessException.notFound("User", userId));

    // Security check: must be a team member to join rides
    securityService.requireMembership(userId, teamSlug);

    Optional<RideParticipation> existingParticipation =
        participationRepository.findByUserAndRideIncludingDeleted(userId, ride.getId());

    if (existingParticipation.isPresent()) {
      RideParticipation rideParticipation = existingParticipation.get();
      if (!rideParticipation.isDeleted()) {
        throw BusinessException.conflict(
            "You are already registered for this ride", "ALREADY_REGISTERED");
      }
      checkCapacity(group);
      // Restore soft-deleted membership and update group if changed
      rideParticipation.setRideGroup(group);
      rideParticipation.setDeleted(false);
      participationRepository.persist(rideParticipation);
      LOG.infov("User {0} joined group {1} in ride {2}", userId, groupId, ride.getId());
      return RideParticipationDto.from(rideParticipation);
    }

    checkCapacity(group);

    RideParticipation participation = new RideParticipation(group, user);

    group.addParticipation(participation);
    participationRepository.persist(participation);

    LOG.infov("User {0} joined group {1} in ride {2}", userId, groupId, ride.getId());
    return RideParticipationDto.from(participation);
  }

  private void checkCapacity(RideGroup group) {
    if (!group.hasCapacity()) {
      throw BusinessException.conflict("This group is full", "GROUP_FULL");
    }
  }

  @Transactional
  public void leaveGroup(String teamSlug, String rideSlug, Long groupId, Long userId) {
    getRideDetail(teamSlug, rideSlug, userId);

    RideParticipation participation =
        participationRepository
            .findByUserAndGroup(userId, groupId)
            .orElseThrow(() -> BusinessException.notFound("You are not registered for this group"));

    participation.setDeleted(true);
    participationRepository.persist(participation);

    LOG.infov("User {0} left group {1} in ride {2}", userId, groupId, rideSlug);
  }
}
