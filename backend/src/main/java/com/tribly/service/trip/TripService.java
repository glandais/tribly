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
import com.tribly.dto.trips.request.StageRequest;
import com.tribly.dto.trips.request.TripRequest;
import com.tribly.dto.trips.response.TripDto;
import com.tribly.dto.trips.response.TripListResponse;
import com.tribly.dto.trips.response.TripParticipationDto;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class TripService extends TeamEntityService<Trip> {

  private static final Logger LOG = Logger.getLogger(TripService.class);

  @Inject TripRepository tripRepository;

  @Inject TripStageRepository tripStageRepository;

  @Inject TripParticipationRepository participationRepository;

  @Inject RouteService routeService;

  @Inject PlaceRepository placeRepository;

  @Override
  protected EntityType getEntityType() {
    return EntityType.TRIP;
  }

  @Override
  protected Optional<Trip> findByIdOptional(Long entityId) {
    return tripRepository.findByIdOptional(entityId);
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
      throw BusinessException.notFound("Trip", tripSlug);
    } else {
      return trips.items().getFirst();
    }
  }

  public TripDto getTripDetail(Team team, String tripSlug, @Nullable User user) {
    return TripDto.from(get(team, tripSlug, user), true, assetService);
  }

  @Transactional
  public TripDto createTrip(Team team, TripRequest request, User creator) {
    // Security check: must be admin or organizer to create trips
    securityService.requireOrganizer(creator, team);
    requireTripEnabled(team);

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

  private void requireTripEnabled(Team team) {
    if (!team.isEnableTrips()) {
      throw BusinessException.forbidden("Trips are disabled");
    }
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
      route = routeService.get(team, routeSlug, user);
      if (visibility == Visibility.PUBLIC && route.getVisibility() != Visibility.PUBLIC) {
        throw BusinessException.businessRule(
            "Can't use private route on public trip", "PUBLIC_TRIP_PRIVATE_ROUTE");
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
  public TripDto updateTrip(Team team, String tripSlug, TripRequest request, User user) {
    Trip trip = get(team, tripSlug, user);

    // Security check: must be admin or creator (if organizer) to edit
    securityService.requireOrganizer(user, team);
    requireTripEnabled(trip.getTeam());

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
          throw BusinessException.notFound("Stage", stageRequest.id());
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
    Trip trip = get(team, tripSlug, user);

    // Security check: must be admin or creator (if organizer) to delete
    securityService.requireOrganizer(user, team);
    requireTripEnabled(trip.getTeam());

    trip.setDeleted(true);
    tripRepository.persist(trip);
    LOG.infov("Trip {0} deleted by user {1}", tripSlug, user);
  }

  @Transactional
  public TripParticipationDto joinTrip(Team team, String tripSlug, User user) {
    Trip trip = get(team, tripSlug, user);

    if (trip.getStatus() != Status.PUBLISHED) {
      throw BusinessException.validation("Can only join published trips");
    }

    // Security check: must be a team member to join trips
    securityService.requireMembership(user, team);
    requireTripEnabled(trip.getTeam());

    Optional<TripParticipation> existingParticipation =
        participationRepository.findByUserAndTripIncludingDeleted(user.getId(), trip.getId());

    if (existingParticipation.isPresent()) {
      TripParticipation tripParticipation = existingParticipation.get();
      if (!tripParticipation.isDeleted()) {
        throw BusinessException.conflict(
            "You are already registered for this trip", "ALREADY_REGISTERED");
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
    Trip trip = get(team, tripSlug, user);
    requireTripEnabled(trip.getTeam());

    TripParticipation participation =
        participationRepository
            .findByUserAndTrip(user.getId(), trip.getId())
            .orElseThrow(() -> BusinessException.notFound("You are not registered for this trip"));

    participation.setDeleted(true);
    participationRepository.persist(participation);

    LOG.infov("User {0} left trip {1}", user.getId(), tripSlug);
  }

  @Transactional
  public TripDto updateSlug(Team team, String slugParam, String newSlug, User user) {
    Trip trip = get(team, slugParam, user);
    String currentSlug = trip.getSlug();

    securityService.requireOrganizer(user, team);

    if (!slugService.isValidSlug(newSlug)) {
      throw BusinessException.validation("Invalid slug format");
    }

    if (currentSlug.equals(newSlug)) {
      return TripDto.from(trip, true, assetService);
    }

    if (tripRepository.existsByTeamAndSlug(trip.getTeam().getId(), newSlug)) {
      throw BusinessException.conflict("Slug already in use", "SLUG_TAKEN");
    }

    slugService.clearEntityRedirect(trip.getTeam().getId(), EntityType.TRIP, newSlug);
    slugService.createEntityRedirect(trip, currentSlug);

    trip.setSlug(newSlug);
    tripRepository.persist(trip);

    LOG.infov("Trip slug changed from {0} to {1} by user {2}", currentSlug, newSlug, user.getId());
    return TripDto.from(trip, true, assetService);
  }
}
