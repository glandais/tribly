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
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.rides.request.GroupRequest;
import com.tribly.dto.rides.request.RideRequest;
import com.tribly.dto.rides.response.*;
import com.tribly.enums.EntityType;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.common.TeamEntityService;
import com.tribly.service.route.RouteService;
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
public class RideService extends TeamEntityService<Ride> {

  private static final Logger LOG = Logger.getLogger(RideService.class);

  @Inject RideRepository rideRepository;

  @Inject RideGroupRepository rideGroupRepository;

  @Inject RideParticipationRepository participationRepository;

  @Inject RouteService routeService;

  @Inject PlaceRepository placeRepository;

  @Override
  protected EntityType getEntityType() {
    return EntityType.RIDE;
  }

  @Override
  protected Optional<Ride> findByIdOptional(Long entityId) {
    return rideRepository.findByIdOptional(entityId);
  }

  @Override
  public Ride getBySlug(Team team, String rideSlug, @Nullable User user) {
    TriblyPage<Ride> rides =
        rideRepository.find(
            TeamEntityQueryBasic.builder()
                .userId(user == null ? null : user.getId())
                .teamIds(Set.of(team.getId()))
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

  public RideListResponse listRides(
      Team team,
      @Nullable User user,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    TriblyPage<Ride> rides =
        rideRepository.find(
            TeamEntityQueryBasic.builder()
                .userId(user == null ? null : user.getId())
                .teamIds(Set.of(team.getId()))
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

  public RideDto getRideDetail(Team team, String rideSlug, @Nullable User user) {
    return RideDto.from(get(team, rideSlug, user), true, assetService);
  }

  @Transactional
  public RideDto createRide(Team team, RideRequest request, User creator) {
    // Security check: must be admin or organizer to create rides
    securityService.requireOrganizer(creator, team);

    validateVisibility(request, team);

    // Generate slug from name, ensure unique within team
    String slug = slugService.generateSlug(request.name(), team.getId(), rideRepository);

    Route route = getRoute(request.routeSlug(), team, request.visibility(), creator);
    Place startPlace = getPlace(request.startPlaceId(), team);
    Place endPlace = getPlace(request.endPlaceId(), team);

    Ride ride =
        new Ride(creator, team, request.dateTime(), request.name(), slug, request.visibility());
    ride.setRoute(route);
    ride.setStart(startPlace);
    ride.setEnd(endPlace);
    ride.setStatus(request.status());
    if (request.status() == Status.DRAFT) {
      ride.setPublishAt(request.publishAt());
    } else {
      ride.setPublishAt(null);
    }
    rideRepository.persistAndFlush(ride);

    updateMedia(ride, request.media());
    rideRepository.persist(ride);

    int sortOrder = 0;
    for (GroupRequest groupRequest : request.groups()) {
      createRideGroup(creator, ride, groupRequest, sortOrder);
      sortOrder++;
    }

    LOG.infov(
        "Ride '{0}' created by user {1} for team {2}",
        ride.getName(), creator.getId(), team.getSlug());
    return RideDto.from(ride, true, assetService);
  }

  private void createRideGroup(User user, Ride ride, GroupRequest groupRequest, int sortOrder) {
    RideGroup group = new RideGroup(user, ride, groupRequest.name());
    setProperties(ride, group, groupRequest, sortOrder, user);
    ride.addGroup(group);
    rideGroupRepository.persist(group);
  }

  private void setProperties(
      Ride ride, RideGroup group, GroupRequest groupRequest, int sortOrder, User user) {
    group.setRide(ride);
    group.setName(groupRequest.name());
    group.setTime(groupRequest.time());
    Route groupRoute =
        getRoute(groupRequest.routeSlug(), ride.getTeam(), ride.getVisibility(), user);
    group.setAverageSpeed(groupRequest.averageSpeed());
    group.setMaxParticipants(groupRequest.maxParticipants());
    group.setSortOrder(sortOrder);
    group.setRoute(groupRoute);
  }

  private @Nullable Route getRoute(
      @Nullable String routeSlug, Team team, Visibility visibility, User user) {
    Route route = null;
    if (routeSlug != null) {
      route = routeService.get(team, routeSlug, user);
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
  public RideDto updateRide(Team team, String rideSlug, RideRequest request, User user) {
    Ride ride = get(team, rideSlug, user);

    // Security check: must be admin or creator (if organizer) to edit
    securityService.requireOrganizer(user, team);

    validateVisibility(request, team);
    ride.setVisibility(request.visibility());

    ride.setName(request.name());
    ride.setDateTime(request.dateTime());
    ride.setStatus(request.status());
    Route route = getRoute(request.routeSlug(), ride.getTeam(), request.visibility(), user);
    ride.setRoute(route);
    Place startPlace = getPlace(request.startPlaceId(), team);
    Place endPlace = getPlace(request.endPlaceId(), team);
    ride.setStart(startPlace);
    ride.setEnd(endPlace);
    if (request.status() == Status.DRAFT) {
      ride.setPublishAt(request.publishAt());
    } else {
      ride.setPublishAt(null);
    }
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
          setProperties(ride, existingRideGroup, groupRequest, sortOrder, user);
        } else {
          throw BusinessException.notFound("Group", groupRequest.id());
        }
      }
      sortOrder++;
    }

    rideRepository.persist(ride);

    LOG.infov("Ride {0} updated by user {1}", rideSlug, user.getId());
    return RideDto.from(ride, true, assetService);
  }

  @Transactional
  public void deleteRide(Team team, String rideSlug, User user) {
    Ride ride = get(team, rideSlug, user);

    // Security check: must be admin or creator (if organizer) to delete
    securityService.requireOrganizer(user, team);

    ride.setDeleted(true);
    rideRepository.persist(ride);
    LOG.infov("Ride {0} deleted by user {1}", rideSlug, user.getId());
  }

  @Transactional
  public RideParticipationDto joinGroup(Team team, String rideSlug, Long groupId, User user) {
    Ride ride = get(team, rideSlug, user);

    if (ride.getStatus() != Status.PUBLISHED) {
      throw BusinessException.validation("Can only join published rides");
    }

    RideGroup group =
        rideGroupRepository
            .findByIdAndRide(groupId, ride.getId())
            .orElseThrow(() -> BusinessException.notFound("Group", groupId));

    // Security check: must be a team member to join rides
    securityService.requireMembership(user, team);

    Optional<RideParticipation> existingParticipation =
        participationRepository.findByUserAndRideIncludingDeleted(user.getId(), ride.getId());

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
      LOG.infov("User {0} joined group {1} in ride {2}", user.getId(), groupId, ride.getId());
      return RideParticipationDto.from(rideParticipation);
    }

    checkCapacity(group);

    RideParticipation participation = new RideParticipation(group, user);

    group.addParticipation(participation);
    participationRepository.persist(participation);

    LOG.infov("User {0} joined group {1} in ride {2}", user.getId(), groupId, ride.getId());
    return RideParticipationDto.from(participation);
  }

  private void checkCapacity(RideGroup group) {
    if (!group.hasCapacity()) {
      throw BusinessException.conflict("This group is full", "GROUP_FULL");
    }
  }

  @Transactional
  public void leaveGroup(Team team, String rideSlug, Long groupId, User user) {
    getRideDetail(team, rideSlug, user);

    RideParticipation participation =
        participationRepository
            .findByUserAndGroup(user.getId(), groupId)
            .orElseThrow(() -> BusinessException.notFound("You are not registered for this group"));

    participation.setDeleted(true);
    participationRepository.persist(participation);

    LOG.infov("User {0} left group {1} in ride {2}", user.getId(), groupId, rideSlug);
  }

  @Transactional
  public RideDto updateSlug(Team team, String slugParam, String newSlug, User user) {
    Ride ride = get(team, slugParam, user);
    String currentSlug = ride.getSlug();

    securityService.requireOrganizer(user, team);

    // Validate new slug format
    if (!slugService.isValidSlug(newSlug)) {
      throw BusinessException.validation("Invalid slug format");
    }

    // No change needed
    if (currentSlug.equals(newSlug)) {
      return RideDto.from(ride, true, assetService);
    }

    // Check if new slug is already taken (by a non-deleted ride in this team)
    if (rideRepository.existsByTeamAndSlug(ride.getTeam().getId(), newSlug)) {
      throw BusinessException.conflict("Slug already in use", "SLUG_TAKEN");
    }

    // Clear any existing redirect TO this new slug (reuse scenario)
    slugService.clearEntityRedirect(ride.getTeam().getId(), EntityType.RIDE, newSlug);

    // Create redirect from old slug to this ride
    slugService.createEntityRedirect(ride, currentSlug);

    // Update the slug
    ride.setSlug(newSlug);
    rideRepository.persist(ride);

    LOG.infov("Ride slug changed from {0} to {1} by user {2}", currentSlug, newSlug, user.getId());
    return RideDto.from(ride, true, assetService);
  }
}
