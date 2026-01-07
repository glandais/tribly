package com.tribly.service.ride;

import static com.tribly.dto.error.ErrorCode.ALREADY_REGISTERED;
import static com.tribly.dto.error.ErrorCode.NOT_REGISTERED;

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
import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.rides.request.GroupRequest;
import com.tribly.dto.rides.request.RideRequest;
import com.tribly.dto.rides.response.*;
import com.tribly.enums.ActionType;
import com.tribly.enums.AllEntityType;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.exception.ConflictException;
import com.tribly.infrastructure.exception.NotFoundException;
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
public class RideService extends TeamEntityService<Ride, RideRepository, RideDto> {

  private static final Logger LOG = Logger.getLogger(RideService.class);

  @Inject RideRepository rideRepository;

  @Inject RideGroupRepository rideGroupRepository;

  @Inject RideParticipationRepository participationRepository;

  @Inject RouteService routeService;

  @Inject PlaceRepository placeRepository;

  @Override
  protected RideRepository getRepository() {
    return rideRepository;
  }

  @Override
  protected RideDto toDto(Ride entity) {
    return RideDto.from(entity, true, assetService);
  }

  @Override
  protected boolean hasRights(
      ActionType action, Team team, @Nullable User user, @Nullable Ride entity) {
    return switch (action) {
      case CREATE, UPDATE, DELETE -> securityService.getOrganizer(user, team) != null;
      // SQL
      case READ -> true;
      case JOIN ->
          securityService.getMembership(user, team) != null
              && entity != null
              && entity.getStatus() == Status.PUBLISHED;
    };
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
      throw new NotFoundException(AllEntityType.RIDE, rideSlug);
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

  @Transactional
  public RideDto createRide(Team team, RideRequest request, User creator) {
    checkRights(ActionType.CREATE, team, creator, null);

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
      route = routeService.get(ActionType.READ, team, routeSlug, user);
      if (visibility == Visibility.PUBLIC && route.getVisibility() != Visibility.PUBLIC) {
        throw new BusinessException(ErrorCode.PUBLIC_RIDE_PRIVATE_ROUTE);
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
        .orElseThrow(() -> new NotFoundException(AllEntityType.PLACE, placeId));
  }

  @Transactional
  public RideDto updateRide(Team team, String rideSlug, RideRequest request, User user) {
    Ride ride = get(ActionType.UPDATE, team, rideSlug, user);

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
          throw new NotFoundException(AllEntityType.RIDE_GROUP, groupRequest.id());
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
    Ride ride = get(ActionType.DELETE, team, rideSlug, user);

    ride.setDeleted(true);
    rideRepository.persist(ride);
    LOG.infov("Ride {0} deleted by user {1}", rideSlug, user.getId());
  }

  @Transactional
  public RideParticipationDto joinGroup(Team team, String rideSlug, Long groupId, User user) {
    Ride ride = get(ActionType.JOIN, team, rideSlug, user);

    RideGroup group =
        rideGroupRepository
            .findByIdAndRide(groupId, ride.getId())
            .orElseThrow(() -> new NotFoundException(AllEntityType.RIDE_GROUP, groupId));

    Optional<RideParticipation> existingParticipation =
        participationRepository.findByUserAndRideIncludingDeleted(user.getId(), ride.getId());

    if (existingParticipation.isPresent()) {
      RideParticipation rideParticipation = existingParticipation.get();
      if (!rideParticipation.isDeleted()) {
        throw new ConflictException(ALREADY_REGISTERED);
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
      throw new ConflictException(ErrorCode.GROUP_FULL);
    }
  }

  @Transactional
  public void leaveGroup(Team team, String rideSlug, Long groupId, User user) {
    get(ActionType.READ, team, rideSlug, user);

    RideParticipation participation =
        participationRepository
            .findByUserAndGroup(user.getId(), groupId)
            .orElseThrow(() -> new BusinessException(NOT_REGISTERED));

    participation.setDeleted(true);
    participationRepository.persist(participation);

    LOG.infov("User {0} left group {1} in ride {2}", user.getId(), groupId, rideSlug);
  }
}
