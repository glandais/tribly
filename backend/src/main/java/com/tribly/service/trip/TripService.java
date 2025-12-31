package com.tribly.service.trip;

import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.place.Place;
import com.tribly.domain.place.repository.PlaceRepository;
import com.tribly.domain.route.Route;
import com.tribly.domain.route.repository.RouteRepository;
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
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.common.TeamEntityService;
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
public class TripService extends TeamEntityService {

  private static final Logger LOG = Logger.getLogger(TripService.class);

  @Inject TripRepository tripRepository;

  @Inject TripStageRepository tripStageRepository;

  @Inject TripParticipationRepository participationRepository;

  @Inject RouteRepository routeRepository;

  @Inject PlaceRepository placeRepository;

  public TripListResponse listTrips(
      String teamSlug,
      @Nullable Long userId,
      @Nullable String search,
      @Nullable Instant from,
      @Nullable Instant to,
      int page,
      int size) {
    TriblyPage<Trip> trips =
        tripRepository.find(
            TeamEntityQueryBasic.builder()
                .userId(userId)
                .teamSlugs(Set.of(teamSlug))
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

  protected Trip getTrip(String teamSlug, String tripSlug, @Nullable Long userId) {
    TriblyPage<Trip> trips =
        tripRepository.find(
            TeamEntityQueryBasic.builder()
                .userId(userId)
                .teamSlugs(Set.of(teamSlug))
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

  public TripDto getTripDetail(String teamSlug, String tripSlug, @Nullable Long userId) {
    return TripDto.from(getTrip(teamSlug, tripSlug, userId), true, assetService);
  }

  @Transactional
  public TripDto createTrip(String teamSlug, TripRequest request, Long creatorId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    User creator =
        userRepository
            .findActiveById(creatorId)
            .orElseThrow(() -> BusinessException.notFound("User", creatorId));

    // Security check: must be admin or organizer to create trips
    securityService.requireOrganizer(creatorId, team.getSlug());
    requireTripEnabled(team);

    // Validate visibility: private teams can only have team-only trips
    Visibility visibility = request.visibility();
    if (team.getVisibility() != Visibility.PUBLIC && visibility == Visibility.PUBLIC) {
      throw BusinessException.validation("Private teams can only have team-only trips");
    }

    // Generate slug from name, ensure unique within team
    String slug =
        slugService.generateSlug(
            request.name(), s -> tripRepository.existsByTeamAndSlug(team.getId(), s));

    Route route = getRoute(request.routeSlug(), team, visibility);

    Trip trip = new Trip(creator, team, request.dateTime(), request.name(), slug, visibility);
    trip.setRoute(route);
    trip.setStatus(request.status());
    trip.setPublishAt(request.publishAt());

    tripRepository.persistAndFlush(trip);

    updateMedia(trip, request.media());
    tripRepository.persist(trip);

    int sortOrder = 0;
    for (StageRequest stageRequest : request.stages()) {
      createTripStage(creator, trip, stageRequest, sortOrder);
      sortOrder++;
    }

    LOG.infov("Trip '{0}' created by user {1} for team {2}", trip.getName(), creatorId, teamSlug);
    return TripDto.from(trip, true, assetService);
  }

  private void requireTripEnabled(Team team) {
    if (!team.isEnableTrips()) {
      throw BusinessException.forbidden("Trips are disabled");
    }
  }

  private void createTripStage(User user, Trip trip, StageRequest stageRequest, int sortOrder) {
    TripStage stage = new TripStage(user, trip, stageRequest.name());
    setStageProperties(trip, stage, stageRequest, sortOrder);
    trip.addStage(stage);
    tripStageRepository.persistAndFlush(stage);
    updateMedia(stage, stageRequest.media());
    tripStageRepository.persist(stage);
  }

  private void setStageProperties(
      Trip trip, TripStage stage, StageRequest stageRequest, int sortOrder) {
    stage.setTrip(trip);
    stage.setName(stageRequest.name());
    stage.setDateTime(stageRequest.dateTime());
    Route stageRoute = getRoute(stageRequest.routeSlug(), trip.getTeam(), trip.getVisibility());
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

  private @Nullable Route getRoute(@Nullable String routeSlug, Team team, Visibility visibility) {
    Route route = null;
    if (routeSlug != null) {
      route =
          routeRepository
              .findByTeamAndSlug(team.getId(), routeSlug)
              .orElseThrow(() -> BusinessException.notFound("Route not found"));
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
  public TripDto updateTrip(String teamSlug, String tripSlug, TripRequest request, Long userId) {
    Trip trip = getTrip(teamSlug, tripSlug, userId);

    // Security check: must be admin or creator (if organizer) to edit
    securityService.requireOrganizer(userId, teamSlug);
    requireTripEnabled(trip.getTeam());

    User user =
        userRepository
            .findActiveById(userId)
            .orElseThrow(() -> BusinessException.notFound("User", userId));

    // Validate visibility: private teams can only have team-only trips
    Team team = trip.getTeam();
    if (team.getVisibility() != Visibility.PUBLIC && request.visibility() == Visibility.PUBLIC) {
      throw BusinessException.validation("Private teams can only have team-only trips");
    }
    trip.setVisibility(request.visibility());

    trip.setName(request.name());
    trip.setDateTime(request.dateTime());
    trip.setStatus(request.status());
    Route route = getRoute(request.routeSlug(), trip.getTeam(), trip.getVisibility());
    trip.setRoute(route);
    // publishAt can be explicitly set to null to remove scheduled publishing
    trip.setPublishAt(request.publishAt());

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
          setStageProperties(trip, existingStage, stageRequest, sortOrder);
          updateMedia(existingStage, stageRequest.media());
          // No persist needed - entity is already managed and will be updated on flush
        } else {
          throw BusinessException.notFound("Stage", stageRequest.id());
        }
      }
      sortOrder++;
    }

    tripRepository.persist(trip);

    LOG.infov("Trip {0} updated by user {1}", tripSlug, userId);
    return TripDto.from(trip, true, assetService);
  }

  @Transactional
  public void deleteTrip(String teamSlug, String tripSlug, Long userId) {
    Trip trip = getTrip(teamSlug, tripSlug, userId);

    // Security check: must be admin or creator (if organizer) to delete
    securityService.requireOrganizer(userId, teamSlug);
    requireTripEnabled(trip.getTeam());

    trip.setDeleted(true);
    tripRepository.persist(trip);
    LOG.infov("Trip {0} deleted by user {1}", tripSlug, userId);
  }

  @Transactional
  public TripParticipationDto joinTrip(String teamSlug, String tripSlug, Long userId) {
    Trip trip = getTrip(teamSlug, tripSlug, userId);

    if (trip.getStatus() != Status.PUBLISHED) {
      throw BusinessException.validation("Can only join published trips");
    }

    User user =
        userRepository
            .findActiveById(userId)
            .orElseThrow(() -> BusinessException.notFound("User", userId));

    // Security check: must be a team member to join trips
    securityService.requireMembership(userId, teamSlug);
    requireTripEnabled(trip.getTeam());

    Optional<TripParticipation> existingParticipation =
        participationRepository.findByUserAndTripIncludingDeleted(userId, trip.getId());

    if (existingParticipation.isPresent()) {
      TripParticipation tripParticipation = existingParticipation.get();
      if (!tripParticipation.isDeleted()) {
        throw BusinessException.conflict(
            "You are already registered for this trip", "ALREADY_REGISTERED");
      }
      // Restore soft-deleted participation
      tripParticipation.setDeleted(false);
      participationRepository.persist(tripParticipation);
      LOG.infov("User {0} joined trip {1}", userId, trip.getId());
      return TripParticipationDto.from(tripParticipation);
    }

    TripParticipation participation = new TripParticipation(trip, user);

    trip.addParticipation(participation);
    participationRepository.persist(participation);

    LOG.infov("User {0} joined trip {1}", userId, trip.getId());
    return TripParticipationDto.from(participation);
  }

  @Transactional
  public void leaveTrip(String teamSlug, String tripSlug, Long userId) {
    Trip trip = getTrip(teamSlug, tripSlug, userId);
    requireTripEnabled(trip.getTeam());

    TripParticipation participation =
        participationRepository
            .findByUserAndTrip(userId, trip.getId())
            .orElseThrow(() -> BusinessException.notFound("You are not registered for this trip"));

    participation.setDeleted(true);
    participationRepository.persist(participation);

    LOG.infov("User {0} left trip {1}", userId, tripSlug);
  }
}
