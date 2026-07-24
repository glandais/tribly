package fr.pedalons.service.ride;

import static fr.pedalons.dto.error.ErrorCode.ALREADY_REGISTERED;
import static fr.pedalons.dto.error.ErrorCode.NOT_REGISTERED;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.ConflictException;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.ride.RideParticipation;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.rides.request.GroupRequest;
import fr.pedalons.dto.rides.request.RideRequest;
import fr.pedalons.dto.rides.response.*;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import fr.pedalons.infrastructure.exception.NotFoundException;
import fr.pedalons.repository.place.PlaceRepository;
import fr.pedalons.repository.ride.RideGroupRepository;
import fr.pedalons.repository.ride.RideParticipationRepository;
import fr.pedalons.repository.ride.RideRepository;
import fr.pedalons.service.common.TeamEntityService;
import fr.pedalons.service.route.RouteService;
import fr.pedalons.service.security.annotation.CheckAccess;
import fr.pedalons.service.thumbnail.ThumbnailService;
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
    User creator = pedalonsContext.getUser();
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
      if (visibility != Visibility.TEAM && route.getVisibility() == Visibility.TEAM) {
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
    User user = pedalonsContext.getUser();

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

    // Never empty a collection with orphanRemoval to refill it right after: setProperties looks a
    // route up, and any query makes Hibernate run its flush-time cascades — which would see the
    // emptied collection, declare every group an orphan and delete it. Putting them back afterwards
    // does not bring them back. So the groups stay in place and only the unclaimed ones leave,
    // once.
    Map<Long, RideGroup> orphanedGroups =
        ride.getGroups().stream().collect(Collectors.toMap(RideGroup::getId, Function.identity()));
    int sortOrder = 0;
    for (GroupRequest groupRequest : request.groups()) {
      Long groupId = TsidUtils.toLongNullable(groupRequest.id());
      if (groupId == null) {
        createRideGroup(teamSlug, user, ride, groupRequest, sortOrder);
      } else {
        RideGroup existingRideGroup = orphanedGroups.remove(groupId);
        if (existingRideGroup == null) {
          throw new NotFoundException(EntityType.RIDE_GROUP, groupRequest.id());
        }
        setProperties(teamSlug, ride, existingRideGroup, groupRequest, sortOrder, user);
      }
      sortOrder++;
    }
    ride.getGroups().removeAll(orphanedGroups.values());

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

  @CheckAccess(entityType = EntityType.RIDE, action = ActionType.DELETE)
  @Transactional
  public RideDto undeleteRide(String teamSlug, String rideSlug) {
    Team team = teamService.getTeam(teamSlug);
    Ride ride = findBySlugIncludeDeleted(team, rideSlug);
    ride.setDeleted(false);
    rideRepository.persist(ride);
    return RideDto.from(ride, true, assetService);
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
        participationRepository.findByUserAndRide(pedalonsContext.getUserId(), ride.getId());

    if (existingParticipation.isPresent()) {
      throw new ConflictException(ALREADY_REGISTERED);
    }

    checkCapacity(group);

    RideParticipation participation = new RideParticipation(group, pedalonsContext.getUser());

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
            .findByUserAndGroup(pedalonsContext.getUserId(), groupId)
            .orElseThrow(() -> new BusinessException(NOT_REGISTERED));

    participationRepository.delete(participation);
  }
}
