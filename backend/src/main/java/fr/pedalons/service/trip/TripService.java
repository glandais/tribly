package fr.pedalons.service.trip;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.ConflictException;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripParticipation;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.trips.request.StageRequest;
import fr.pedalons.dto.trips.request.TripRequest;
import fr.pedalons.dto.trips.response.TripDto;
import fr.pedalons.dto.trips.response.TripParticipationDto;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import fr.pedalons.infrastructure.exception.*;
import fr.pedalons.repository.place.PlaceRepository;
import fr.pedalons.repository.trip.TripParticipationRepository;
import fr.pedalons.repository.trip.TripRepository;
import fr.pedalons.repository.trip.TripStageRepository;
import fr.pedalons.service.common.TeamEntityService;
import fr.pedalons.service.route.RouteService;
import fr.pedalons.service.security.annotation.CheckAccess;
import fr.pedalons.service.thumbnail.ThumbnailService;
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

  @Inject ThumbnailService thumbnailService;

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
    User creator = pedalonsContext.getUser();
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

    thumbnailService.generateTripThumbnails(trip);

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
    User user = pedalonsContext.getUser();

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

    thumbnailService.generateTripThumbnails(trip);

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
    User user = pedalonsContext.getUser();

    Optional<TripParticipation> existingParticipation =
        participationRepository.findByUserAndTrip(user.getId(), trip.getId());

    if (existingParticipation.isPresent()) {
      throw new ConflictException(ErrorCode.ALREADY_REGISTERED);
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
            .findByUserAndTrip(pedalonsContext.getUserId(), trip.getId())
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_REGISTERED));

    participationRepository.delete(participation);
  }
}
