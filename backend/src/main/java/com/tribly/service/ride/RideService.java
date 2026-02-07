package com.tribly.service.ride;

import static com.tribly.dto.error.ErrorCode.ALREADY_REGISTERED;
import static com.tribly.dto.error.ErrorCode.NOT_REGISTERED;

import com.tribly.common.TsidUtils;
import com.tribly.common.exception.BusinessException;
import com.tribly.common.exception.ConflictException;
import com.tribly.domain.place.Place;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.ride.RideParticipation;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.rides.request.GroupRequest;
import com.tribly.dto.rides.request.RideRequest;
import com.tribly.dto.rides.response.*;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.NotFoundException;
import com.tribly.repository.place.PlaceRepository;
import com.tribly.repository.ride.RideGroupRepository;
import com.tribly.repository.ride.RideParticipationRepository;
import com.tribly.repository.ride.RideRepository;
import com.tribly.service.common.TeamEntityService;
import com.tribly.service.route.RouteService;
import com.tribly.service.security.annotation.CheckAccess;
import com.tribly.service.thumbnail.ThumbnailService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class RideService extends TeamEntityService<Ride, RideRepository, RideDto> {

  @Inject RideRepository rideRepository;

  @Inject RideGroupRepository rideGroupRepository;

  @Inject RideParticipationRepository participationRepository;

  @Inject RouteService routeService;

  @Inject PlaceRepository placeRepository;

  @Inject ThumbnailService thumbnailService;

  @Override
  protected RideRepository getRepository() {
    return rideRepository;
  }

  @Override
  protected RideDto toDto(Ride entity) {
    return RideDto.from(entity, true, assetService);
  }

  @Override
  public Ride findBySlug(Team team, String entitySlug) {
    return super.findBySlug(team, entitySlug);
  }

  @CheckAccess(entityType = EntityType.RIDE, action = ActionType.READ)
  public RideDto getDto(String teamSlug, String entitySlug) {
    Team team = teamService.getTeam(teamSlug);
    return super.getDto(team, entitySlug);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.RIDE, action = ActionType.CREATE)
  public RideDto createRide(String teamSlug, RideRequest request) {
    Team team = teamService.getTeam(teamSlug);
    User creator = triblyContext.getUser();
    validateVisibility(team, request);

    // Generate slug from name, ensure unique within team
    String slug = slugService.generateSlug(request.name(), team.getId(), rideRepository);

    Route route = getRoute(teamSlug, request.routeSlug(), request.visibility());
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
      createRideGroup(teamSlug, creator, ride, groupRequest, sortOrder);
      sortOrder++;
    }

    thumbnailService.generateRideThumbnails(ride);

    return RideDto.from(ride, true, assetService);
  }

  private void createRideGroup(
      String teamSlug, User user, Ride ride, GroupRequest groupRequest, int sortOrder) {
    RideGroup group = new RideGroup(user, ride, groupRequest.name());
    setProperties(teamSlug, ride, group, groupRequest, sortOrder, user);
    ride.addGroup(group);
    rideGroupRepository.persist(group);
  }

  private void setProperties(
      String teamSlug,
      Ride ride,
      RideGroup group,
      GroupRequest groupRequest,
      int sortOrder,
      User user) {
    group.setRide(ride);
    group.setName(groupRequest.name());
    group.setTime(groupRequest.time());
    Route groupRoute = getRoute(teamSlug, groupRequest.routeSlug(), ride.getVisibility());
    group.setAverageSpeed(groupRequest.averageSpeed());
    group.setMaxParticipants(groupRequest.maxParticipants());
    group.setSortOrder(sortOrder);
    group.setRoute(groupRoute);
  }

  private @Nullable Route getRoute(
      String teamSlug, @Nullable String routeSlug, Visibility visibility) {
    Route route = null;
    if (routeSlug != null) {
      route = routeService.get(teamSlug, routeSlug);
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
        .orElseThrow(() -> new NotFoundException(EntityType.PLACE, placeId));
  }

  @Transactional
  @CheckAccess(entityType = EntityType.RIDE, action = ActionType.UPDATE)
  public RideDto updateRide(String teamSlug, String rideSlug, RideRequest request) {
    Team team = teamService.getTeam(teamSlug);
    Ride ride = findBySlug(team, rideSlug);
    User user = triblyContext.getUser();

    validateVisibility(team, request);
    ride.setVisibility(request.visibility());

    ride.setName(request.name());
    ride.setDateTime(request.dateTime());
    ride.setStatus(request.status());
    Route route = getRoute(teamSlug, request.routeSlug(), request.visibility());
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
    ride.getGroups().clear();
    int sortOrder = 0;
    for (GroupRequest groupRequest : request.groups()) {
      Long groupId = TsidUtils.toLongNullable(groupRequest.id());
      if (groupId == null) {
        createRideGroup(teamSlug, user, ride, groupRequest, sortOrder);
      } else {
        RideGroup existingRideGroup = existingGroups.remove(groupId);
        if (existingRideGroup != null) {
          setProperties(teamSlug, ride, existingRideGroup, groupRequest, sortOrder, user);
          ride.getGroups().add(existingRideGroup);
        } else {
          throw new NotFoundException(EntityType.RIDE_GROUP, groupRequest.id());
        }
      }
      sortOrder++;
    }

    rideRepository.persist(ride);

    thumbnailService.generateRideThumbnails(ride);

    return RideDto.from(ride, true, assetService);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.RIDE, action = ActionType.UPDATE)
  public RideDto updateSlug(String teamSlug, String slug, String newSlug) {
    Team team = teamService.getTeam(teamSlug);
    return super.updateSlug(team, slug, newSlug);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.RIDE, action = ActionType.DELETE)
  public void deleteRide(String teamSlug, String rideSlug) {
    Team team = teamService.getTeam(teamSlug);
    Ride ride = findBySlug(team, rideSlug);

    ride.setDeleted(true);
    rideRepository.persist(ride);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.RIDE, action = ActionType.JOIN)
  public RideParticipationDto joinGroup(String teamSlug, String rideSlug, Long groupId) {
    Team team = teamService.getTeam(teamSlug);
    Ride ride = findBySlug(team, rideSlug);

    RideGroup group =
        rideGroupRepository
            .findByIdAndRide(groupId, ride.getId())
            .orElseThrow(() -> new NotFoundException(EntityType.RIDE_GROUP, groupId));

    Optional<RideParticipation> existingParticipation =
        participationRepository.findByUserAndRide(triblyContext.getUserId(), ride.getId());

    if (existingParticipation.isPresent()) {
      throw new ConflictException(ALREADY_REGISTERED);
    }

    checkCapacity(group);

    RideParticipation participation = new RideParticipation(group, triblyContext.getUser());

    group.addParticipation(participation);
    participationRepository.persist(participation);

    return RideParticipationDto.from(participation);
  }

  private void checkCapacity(RideGroup group) {
    if (!group.hasCapacity()) {
      throw new ConflictException(ErrorCode.GROUP_FULL);
    }
  }

  @Transactional
  @CheckAccess(entityType = EntityType.RIDE, action = ActionType.LEAVE)
  public void leaveGroup(String teamSlug, String rideSlug, Long groupId) {
    RideParticipation participation =
        participationRepository
            .findByUserAndGroup(triblyContext.getUserId(), groupId)
            .orElseThrow(() -> new BusinessException(NOT_REGISTERED));

    participationRepository.delete(participation);
  }
}
