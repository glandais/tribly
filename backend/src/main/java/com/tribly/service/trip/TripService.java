package com.tribly.service.trip;

import com.tribly.common.TsidUtils;
import com.tribly.common.exception.BusinessException;
import com.tribly.common.exception.ConflictException;
import com.tribly.domain.place.Place;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.trip.Trip;
import com.tribly.domain.trip.TripParticipation;
import com.tribly.domain.trip.TripStage;
import com.tribly.domain.user.User;
import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.trips.request.StageRequest;
import com.tribly.dto.trips.request.TripRequest;
import com.tribly.dto.trips.response.TripDto;
import com.tribly.dto.trips.response.TripParticipationDto;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.*;
import com.tribly.repository.place.PlaceRepository;
import com.tribly.repository.trip.TripParticipationRepository;
import com.tribly.repository.trip.TripRepository;
import com.tribly.repository.trip.TripStageRepository;
import com.tribly.service.common.TeamEntityService;
import com.tribly.service.route.RouteService;
import com.tribly.service.security.annotation.CheckAccess;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class TripService extends TeamEntityService<Trip, TripRepository, TripDto> {

  @Inject TripRepository tripRepository;

  @Inject TripStageRepository tripStageRepository;

  @Inject TripParticipationRepository participationRepository;

  @Inject RouteService routeService;

  @Inject PlaceRepository placeRepository;

  @Override
  protected TripRepository getRepository() {
    return tripRepository;
  }

  @Override
  protected TripDto toDto(Trip entity) {
    return TripDto.from(entity, true, assetService);
  }

  @Override
  public Trip findBySlug(Team team, String entitySlug) {
    return super.findBySlug(team, entitySlug);
  }

  @CheckAccess(entityType = EntityType.TRIP, action = ActionType.READ)
  public TripDto getDto(String teamSlug, String entitySlug) {
    Team team = teamService.getTeam(teamSlug);
    return super.getDto(team, entitySlug);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TRIP, action = ActionType.CREATE)
  public TripDto createTrip(String teamSlug, TripRequest request) {
    Team team = teamService.getTeam(teamSlug);
    User creator = triblyContext.getUser();
    validateVisibility(team, request);

    Visibility visibility = request.visibility();

    // Generate slug from name, ensure unique within team
    String slug = slugService.generateSlug(request.name(), team.getId(), tripRepository);

    Route route = getRoute(teamSlug, request.routeSlug(), visibility);

    Trip trip = new Trip(creator, team, request.dateTime(), request.name(), slug, visibility);
    trip.setRoute(route);
    trip.setStatus(request.status());
    if (request.status() == Status.DRAFT) {
      trip.setPublishAt(request.publishAt());
    } else {
      trip.setPublishAt(null);
    }

    tripRepository.persistAndFlush(trip);

    updateMedia(trip, request.media());
    tripRepository.persist(trip);

    int sortOrder = 0;
    for (StageRequest stageRequest : request.stages()) {
      createTripStage(teamSlug, creator, trip, stageRequest, sortOrder);
      sortOrder++;
    }

    return TripDto.from(trip, true, assetService);
  }

  private void createTripStage(
      String teamSlug, User user, Trip trip, StageRequest stageRequest, int sortOrder) {
    TripStage stage = new TripStage(user, trip, stageRequest.name());
    setStageProperties(teamSlug, trip, stage, stageRequest, sortOrder, user);
    trip.addStage(stage);
    tripStageRepository.persistAndFlush(stage);
    updateMedia(stage, stageRequest.media());
    tripStageRepository.persist(stage);
  }

  private void setStageProperties(
      String teamSlug,
      Trip trip,
      TripStage stage,
      StageRequest stageRequest,
      int sortOrder,
      User user) {
    stage.setTrip(trip);
    stage.setName(stageRequest.name());
    stage.setDateTime(stageRequest.dateTime());
    Route stageRoute = getRoute(teamSlug, stageRequest.routeSlug(), trip.getVisibility());
    stage.setRoute(stageRoute);
    Place startPlace = getPlace(stageRequest.startPlaceId(), trip.getTeam());
    Place endPlace = getPlace(stageRequest.endPlaceId(), trip.getTeam());
    stage.setStartPlace(startPlace);
    stage.setEndPlace(endPlace);
    stage.setSortOrder(sortOrder);
    // Inherit visibility from trip
    stage.setVisibility(trip.getVisibility());
    stage.setStatus(trip.getStatus());
  }

  private @Nullable Route getRoute(
      String teamSlug, @Nullable String routeSlug, Visibility visibility) {
    Route route = null;
    if (routeSlug != null) {
      route = routeService.get(teamSlug, routeSlug);
      if (visibility == Visibility.PUBLIC && route.getVisibility() != Visibility.PUBLIC) {
        throw new BusinessException(ErrorCode.PUBLIC_TRIP_PRIVATE_ROUTE);
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
  @CheckAccess(entityType = EntityType.TRIP, action = ActionType.UPDATE)
  public TripDto updateTrip(String teamSlug, String tripSlug, TripRequest request) {
    Team team = teamService.getTeam(teamSlug);
    Trip trip = findBySlug(team, tripSlug);
    User user = triblyContext.getUser();

    // Validate visibility: private teams can only have team-only trips
    validateVisibility(team, request);
    trip.setVisibility(request.visibility());

    trip.setName(request.name());
    trip.setDateTime(request.dateTime());
    trip.setStatus(request.status());
    Route route = getRoute(teamSlug, request.routeSlug(), trip.getVisibility());
    trip.setRoute(route);
    if (request.status() == Status.DRAFT) {
      trip.setPublishAt(request.publishAt());
    } else {
      trip.setPublishAt(null);
    }

    updateMedia(trip, request.media());

    Map<Long, TripStage> existingStages =
        trip.getStages().stream().collect(Collectors.toMap(TripStage::getId, Function.identity()));
    for (TripStage stage : trip.getStages()) {
      stage.setDeleted(true);
      stage.setSortOrder(0);
    }
    int sortOrder = 0;
    for (StageRequest stageRequest : request.stages()) {
      Long stageId = TsidUtils.toLongNullable(stageRequest.id());
      if (stageId == null) {
        createTripStage(teamSlug, user, trip, stageRequest, sortOrder);
      } else {
        TripStage existingStage = existingStages.remove(stageId);
        if (existingStage != null) {
          existingStage.setDeleted(false);
          setStageProperties(teamSlug, trip, existingStage, stageRequest, sortOrder, user);
          updateMedia(existingStage, stageRequest.media());
          // No persist needed - entity is already managed and will be updated on flush
        } else {
          throw new NotFoundException(EntityType.TRIP_STAGE, stageRequest.id());
        }
      }
      sortOrder++;
    }

    tripRepository.persist(trip);

    return TripDto.from(trip, true, assetService);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TRIP, action = ActionType.UPDATE)
  public TripDto updateSlug(String teamSlug, String slug, String newSlug) {
    Team team = teamService.getTeam(teamSlug);
    return super.updateSlug(team, slug, newSlug);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TRIP, action = ActionType.DELETE)
  public void deleteTrip(String teamSlug, String tripSlug) {
    Team team = teamService.getTeam(teamSlug);
    Trip trip = findBySlug(team, tripSlug);

    trip.setDeleted(true);
    tripRepository.persist(trip);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TRIP, action = ActionType.JOIN)
  public TripParticipationDto joinTrip(String teamSlug, String tripSlug) {
    Team team = teamService.getTeam(teamSlug);
    Trip trip = findBySlug(team, tripSlug);
    User user = triblyContext.getUser();

    Optional<TripParticipation> existingParticipation =
        participationRepository.findByUserAndTripIncludingDeleted(user.getId(), trip.getId());

    if (existingParticipation.isPresent()) {
      TripParticipation tripParticipation = existingParticipation.get();
      if (!tripParticipation.isDeleted()) {
        throw new ConflictException(ErrorCode.ALREADY_REGISTERED);
      }
      // Restore soft-deleted participation
      tripParticipation.setDeleted(false);
      participationRepository.persist(tripParticipation);
      return TripParticipationDto.from(tripParticipation);
    }

    TripParticipation participation = new TripParticipation(trip, user);

    trip.addParticipation(participation);
    participationRepository.persist(participation);

    return TripParticipationDto.from(participation);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.TRIP, action = ActionType.LEAVE)
  public void leaveTrip(String teamSlug, String tripSlug) {
    Team team = teamService.getTeam(teamSlug);
    Trip trip = findBySlug(team, tripSlug);

    TripParticipation participation =
        participationRepository
            .findByUserAndTrip(triblyContext.getUserId(), trip.getId())
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_REGISTERED));

    participation.setDeleted(true);
    participationRepository.persist(participation);
  }
}
