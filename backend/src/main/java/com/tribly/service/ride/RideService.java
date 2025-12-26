package com.tribly.service.ride;

import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.ride.RideParticipation;
import com.tribly.domain.ride.repository.RideGroupRepository;
import com.tribly.domain.ride.repository.RideParticipationRepository;
import com.tribly.domain.ride.repository.RideRepository;
import com.tribly.domain.route.Route;
import com.tribly.domain.route.repository.RouteRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.rides.request.GroupRequest;
import com.tribly.dto.rides.request.RideRequest;
import com.tribly.dto.rides.response.*;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.common.BasicQuery;
import com.tribly.service.common.SlugService;
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
public class RideService extends TeamEntityService<Ride, BasicQuery, TeamEntityQueryBasic> {

  private static final Logger LOG = Logger.getLogger(RideService.class);

  @Inject RideRepository rideRepository;

  @Inject RideGroupRepository rideGroupRepository;

  @Inject RideParticipationRepository participationRepository;

  @Inject TeamRepository teamRepository;

  @Inject UserRepository userRepository;

  @Inject RouteRepository routeRepository;

  @Inject SlugService slugService;

  public RideRepository getRepository() {
    return rideRepository;
  }

  @Override
  protected TeamEntityQueryBasic getQuery(
      BasicQuery query, Set<Long> memberTeamIds, Set<Long> organizerTeamIds) {
    return query.getTeamEntityQueryBasic(memberTeamIds, organizerTeamIds);
  }

  public RideListResponse listRides(
      String teamSlug,
      @Nullable Long userId,
      @Nullable Instant from,
      @Nullable Instant to,
      @Nullable Status status,
      int page,
      int size) {
    TriblyPage<Ride> rides =
        list(new BasicQuery(null, Set.of(teamSlug), userId, status, null, from, to, page, size));
    List<RideDto> dtos = rides.items().stream().map(r -> RideDto.from(r, false)).toList();
    return new RideListResponse(dtos, rides.total(), page, size);
  }

  protected Ride getRide(String teamSlug, String rideSlug, @Nullable Long userId) {
    TriblyPage<Ride> rides =
        list(new BasicQuery(rideSlug, Set.of(teamSlug), userId, null, null, null, null, 0, 1));
    if (rides.items().isEmpty()) {
      throw BusinessException.notFound("Ride", rideSlug);
    } else {
      return rides.items().getFirst();
    }
  }

  public RideDto getRideDetail(String teamSlug, String rideSlug, @Nullable Long userId) {
    return RideDto.from(getRide(teamSlug, rideSlug, userId), true);
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

    Route route = getRoute(request.routeSlug(), team);

    Ride ride = new Ride(team, creator, request.name(), slug, request.dateTime());
    ride.setDescription(request.description());
    ride.setVisibility(visibility);
    ride.setRoute(route);
    ride.setStatus(request.status());
    ride.setPublishAt(request.publishAt());

    rideRepository.persist(ride);

    int sortOrder = 0;
    for (GroupRequest groupRequest : request.groups()) {
      createRideGroup(ride, groupRequest, sortOrder);
      sortOrder++;
    }

    LOG.infov("Ride '{0}' created by user {1} for team {2}", ride.getName(), creatorId, teamSlug);
    return RideDto.from(ride, true);
  }

  private void createRideGroup(Ride ride, GroupRequest groupRequest, int sortOrder) {
    RideGroup group = new RideGroup();
    setProperties(ride, group, groupRequest, sortOrder);
    ride.addGroup(group);
    rideGroupRepository.persist(group);
  }

  private void setProperties(Ride ride, RideGroup group, GroupRequest groupRequest, int sortOrder) {
    group.setRide(ride);
    group.setName(groupRequest.name());
    Route groupRoute = getRoute(groupRequest.routeSlug(), ride.getTeam());
    group.setDescription(groupRequest.description());
    group.setAverageSpeed(groupRequest.averageSpeed());
    group.setMaxParticipants(groupRequest.maxParticipants());
    group.setSortOrder(sortOrder);
    group.setRoute(groupRoute);
  }

  private @Nullable Route getRoute(@Nullable String routeSlug, Team team) {
    Route route = null;
    if (routeSlug != null) {
      route =
          routeRepository
              .findByTeamAndSlug(team.getId(), routeSlug)
              .orElseThrow(() -> BusinessException.notFound("Route not found"));
      if (!route.getTeam().getId().equals(team.getId())) {
        throw BusinessException.businessRule(
            "Route team is not ride team", "ROUTE_TEAM_RIDE_TEAM_DIFFERENT");
      }
      if (team.getVisibility() == Visibility.PUBLIC && route.getVisibility() != Visibility.PUBLIC) {
        throw BusinessException.businessRule(
            "Can't use private route on public ride", "PUBLIC_RIDE_PRIVATE_ROUTE");
      }
    }
    return route;
  }

  @Transactional
  public RideDto updateRide(String teamSlug, String rideSlug, RideRequest request, Long userId) {
    Ride ride = getRide(teamSlug, rideSlug, userId);

    // Security check: must be admin or creator (if organizer) to edit
    securityService.requireOrganizer(userId, teamSlug);

    // Validate visibility: private teams can only have team-only rides
    Team team = ride.getTeam();
    if (team.getVisibility() != Visibility.PUBLIC && request.visibility() == Visibility.PUBLIC) {
      throw BusinessException.validation("Private teams can only have team-only rides");
    }
    ride.setVisibility(request.visibility());

    ride.setName(request.name());
    ride.setDescription(request.description());
    ride.setDateTime(request.dateTime());
    ride.setStatus(request.status());
    Route route = getRoute(request.routeSlug(), ride.getTeam());
    ride.setRoute(route);
    // publishAt can be explicitly set to null to remove scheduled publishing
    ride.setPublishAt(request.publishAt());

    rideRepository.persist(ride);

    Map<Long, RideGroup> existingGroups =
        ride.getGroups().stream().collect(Collectors.toMap(RideGroup::getId, Function.identity()));
    ride.getGroups().clear();
    int sortOrder = 0;
    for (GroupRequest groupRequest : request.groups()) {
      Long groupId = TsidUtils.toLongNullable(groupRequest.id());
      if (groupId == null) {
        createRideGroup(ride, groupRequest, sortOrder);
      } else {
        RideGroup existingRideGroup = existingGroups.remove(groupId);
        if (existingRideGroup != null) {
          setProperties(ride, existingRideGroup, groupRequest, sortOrder);
          ride.addGroup(existingRideGroup);
          rideGroupRepository.persist(existingRideGroup);
        } else {
          createRideGroup(ride, groupRequest, sortOrder);
        }
      }
      sortOrder++;
    }
    for (RideGroup rideGroup : existingGroups.values()) {
      rideGroup.setRide(ride);
      rideGroup.setDeleted(true);
      rideGroup.setSortOrder(sortOrder);
      ride.addGroup(rideGroup);
      rideGroupRepository.persist(rideGroup);
      sortOrder++;
    }

    LOG.infov("Ride {0} updated by user {1}", rideSlug, userId);
    return RideDto.from(ride, true);
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

  public RideGroupListResponse listGroups(String teamSlug, String rideSlug, @Nullable Long userId) {
    Ride ride = getRide(teamSlug, rideSlug, userId);
    List<RideGroup> groups = rideGroupRepository.findByRide(ride.getId());
    List<RideGroupDto> dtos = groups.stream().map(RideGroupDto::from).toList();
    return new RideGroupListResponse(dtos);
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
