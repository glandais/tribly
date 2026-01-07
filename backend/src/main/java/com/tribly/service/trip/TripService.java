package com.tribly.service.trip;

import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.place.Place;
import com.tribly.domain.place.repository.PlaceRepository;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.trip.Trip;
import com.tribly.domain.trip.TripParticipation;
import com.tribly.domain.trip.TripStage;
import com.tribly.domain.trip.repository.TripParticipationRepository;
import com.tribly.domain.trip.repository.TripRepository;
import com.tribly.domain.trip.repository.TripStageRepository;
import com.tribly.domain.user.User;
import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.trips.request.StageRequest;
import com.tribly.dto.trips.request.TripRequest;
import com.tribly.dto.trips.response.TripDto;
import com.tribly.dto.trips.response.TripListResponse;
import com.tribly.dto.trips.response.TripParticipationDto;
import com.tribly.enums.ActionType;
import com.tribly.enums.AllEntityType;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.*;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.common.TeamEntityService;
import com.tribly.service.route.RouteService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class TripService extends TeamEntityService<Trip, TripRepository, TripDto> {

  private static final Logger LOG = Logger.getLogger(TripService.class);

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
  protected boolean hasRights(
      ActionType action, Team team, @Nullable User user, @Nullable Trip entity) {
    if (!team.isEnableTrips()) {
      return false;
    }
    return switch (action) {
      case CREATE, UPDATE, DELETE -> securityService.getOrganizer(user, team) != null;
      case JOIN ->
          securityService.getMembership(user, team) != null
              && entity != null
              && entity.getStatus() == Status.PUBLISHED;
      // SQL
      case READ -> true;
    };
  }

  public TripListResponse listTrips(
      Team team,
      @Nullable User user,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    TriblyPage<Trip> trips =
        tripRepository.find(
            TeamEntityQueryBasic.builder()
                .userId(user == null ? null : user.getId())
                .teamIds(Set.of(team.getId()))
                .search(search)
                .from(from)
                .to(to)
                .page(page)
                .size(size)
                .build());
    List<TripDto> dtos =
        trips.items().stream().map(t -> TripDto.from(t, false, assetService)).toList();
    return new TripListResponse(dtos, trips.total(), page, size);
  }

  @Override
  public Trip getBySlug(Team team, String tripSlug, @Nullable User user) {
    TriblyPage<Trip> trips =
        tripRepository.find(
            TeamEntityQueryBasic.builder()
                .userId(user == null ? null : user.getId())
                .teamIds(Set.of(team.getId()))
                .slug(tripSlug)
                .page(0)
                .size(1)
                .build());
    if (trips.items().isEmpty()) {
      throw new NotFoundException(AllEntityType.TRIP, tripSlug);
    } else {
      return trips.items().getFirst();
    }
  }

  @Transactional
  public TripDto createTrip(Team team, TripRequest request, User creator) {
    checkRights(ActionType.CREATE, team, creator, null);

    validateVisibility(request, team);

    Visibility visibility = request.visibility();

    // Generate slug from name, ensure unique within team
    String slug = slugService.generateSlug(request.name(), team.getId(), tripRepository);

    Route route = getRoute(request.routeSlug(), team, visibility, creator);

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
      createTripStage(creator, trip, stageRequest, sortOrder);
      sortOrder++;
    }

    LOG.infov("Trip '{0}' created by user {1} for team {2}", trip.getName(), creator.getId(), team);
    return TripDto.from(trip, true, assetService);
  }

  private void createTripStage(User user, Trip trip, StageRequest stageRequest, int sortOrder) {
    TripStage stage = new TripStage(user, trip, stageRequest.name());
    setStageProperties(trip, stage, stageRequest, sortOrder, user);
    trip.addStage(stage);
    tripStageRepository.persistAndFlush(stage);
    updateMedia(stage, stageRequest.media());
    tripStageRepository.persist(stage);
  }

  private void setStageProperties(
      Trip trip, TripStage stage, StageRequest stageRequest, int sortOrder, User user) {
    stage.setTrip(trip);
    stage.setName(stageRequest.name());
    stage.setDateTime(stageRequest.dateTime());
    Route stageRoute =
        getRoute(stageRequest.routeSlug(), trip.getTeam(), trip.getVisibility(), user);
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
      @Nullable String routeSlug, Team team, Visibility visibility, User user) {
    Route route = null;
    if (routeSlug != null) {
      route = routeService.get(ActionType.READ, team, routeSlug, user);
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
        .orElseThrow(() -> new NotFoundException(AllEntityType.PLACE, placeId));
  }

  @Transactional
  public TripDto updateTrip(Team team, String tripSlug, TripRequest request, User user) {
    Trip trip = get(ActionType.UPDATE, team, tripSlug, user);

    // Validate visibility: private teams can only have team-only trips
    validateVisibility(request, team);
    trip.setVisibility(request.visibility());

    trip.setName(request.name());
    trip.setDateTime(request.dateTime());
    trip.setStatus(request.status());
    Route route = getRoute(request.routeSlug(), trip.getTeam(), trip.getVisibility(), user);
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
        createTripStage(user, trip, stageRequest, sortOrder);
      } else {
        TripStage existingStage = existingStages.remove(stageId);
        if (existingStage != null) {
          existingStage.setDeleted(false);
          setStageProperties(trip, existingStage, stageRequest, sortOrder, user);
          updateMedia(existingStage, stageRequest.media());
          // No persist needed - entity is already managed and will be updated on flush
        } else {
          throw new NotFoundException(AllEntityType.TRIP_STAGE, stageRequest.id());
        }
      }
      sortOrder++;
    }

    tripRepository.persist(trip);

    LOG.infov("Trip {0} updated by user {1}", tripSlug, user.getId());
    return TripDto.from(trip, true, assetService);
  }

  @Transactional
  public void deleteTrip(Team team, String tripSlug, User user) {
    Trip trip = get(ActionType.DELETE, team, tripSlug, user);

    trip.setDeleted(true);
    tripRepository.persist(trip);
    LOG.infov("Trip {0} deleted by user {1}", tripSlug, user);
  }

  @Transactional
  public TripParticipationDto joinTrip(Team team, String tripSlug, User user) {
    Trip trip = get(ActionType.JOIN, team, tripSlug, user);

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
      LOG.infov("User {0} joined trip {1}", user.getId(), trip.getId());
      return TripParticipationDto.from(tripParticipation);
    }

    TripParticipation participation = new TripParticipation(trip, user);

    trip.addParticipation(participation);
    participationRepository.persist(participation);

    LOG.infov("User {0} joined trip {1}", user.getId(), trip.getId());
    return TripParticipationDto.from(participation);
  }

  @Transactional
  public void leaveTrip(Team team, String tripSlug, User user) {
    Trip trip = get(ActionType.READ, team, tripSlug, user);

    TripParticipation participation =
        participationRepository
            .findByUserAndTrip(user.getId(), trip.getId())
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_REGISTERED));

    participation.setDeleted(true);
    participationRepository.persist(participation);

    LOG.infov("User {0} left trip {1}", user.getId(), tripSlug);
  }
}
