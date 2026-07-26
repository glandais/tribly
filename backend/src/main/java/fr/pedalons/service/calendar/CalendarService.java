package fr.pedalons.service.calendar;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.ForbiddenException;
import fr.pedalons.domain.calendar.CalendarToken;
import fr.pedalons.domain.common.Publication;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.team.UserTeam;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.calendar.request.AuthMode;
import fr.pedalons.dto.calendar.response.CalendarEventDto;
import fr.pedalons.dto.calendar.response.CalendarEventType;
import fr.pedalons.dto.calendar.response.CalendarEventsResponse;
import fr.pedalons.dto.calendar.response.CalendarTokenDto;
import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.dto.publications.response.UserParticipations;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.repository.calendar.CalendarTokenRepository;
import fr.pedalons.repository.common.AllPublicationRepository;
import fr.pedalons.repository.common.PublicationQuery;
import fr.pedalons.repository.ride.RideGroupRepository;
import fr.pedalons.repository.team.UserTeamRepository;
import fr.pedalons.service.asset.ThumbnailLookup;
import fr.pedalons.service.common.ParticipationLookup;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.CheckAccess;
import fr.pedalons.service.team.TeamService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class CalendarService {

  private static final int TOKEN_BYTES = 32; // 256 bits = 64 hex chars

  @Inject CalendarTokenRepository calendarTokenRepository;

  @Inject PedalonsQueryContext pedalonsQueryContext;

  @Inject TeamService teamService;

  @Inject AllPublicationRepository allPublicationRepository;

  @Inject IcsGenerationService icsGenerationService;

  @Inject UserTeamRepository userTeamRepository;

  @Inject DomainResolver domainResolver;

  @Inject ParticipationLookup participationLookup;

  @Inject RideGroupRepository rideGroupRepository;

  @Inject ThumbnailLookup thumbnailLookup;

  @CheckAccess(entityType = EntityType.CALENDAR, action = ActionType.LIST_ALL_TEAMS)
  public CalendarEventsResponse getEventsForUser(
      AuthMode authMode, @Nullable Instant from, @Nullable Instant to) {
    User user = pedalonsQueryContext.getUser();
    return getEventsForUserInternal(user, resolveFrom(from), resolveTo(to));
  }

  @CheckAccess(entityType = EntityType.CALENDAR, action = ActionType.LIST_ALL_TEAMS)
  public String generateIcs(AuthMode authMode, String token) {
    User user = validateToken(token);
    CalendarEventsResponse events =
        getEventsForUserInternal(user, getDefaultFrom(), getDefaultTo());
    return icsGenerationService.generateIcs(events.events(), "Pedalons - Mes sorties");
  }

  @CheckAccess(entityType = EntityType.CALENDAR, action = ActionType.LIST)
  public CalendarEventsResponse getEventsForTeam(
      AuthMode authMode, String teamSlug, @Nullable Instant from, @Nullable Instant to) {
    Team team = teamService.getTeam(teamSlug);
    User user = pedalonsQueryContext.getUser();
    return getTeamEvents(team, user, resolveFrom(from), resolveTo(to));
  }

  @CheckAccess(entityType = EntityType.CALENDAR, action = ActionType.LIST)
  public String generateIcsForTeam(AuthMode authMode, String token, String teamSlug) {
    User user = validateToken(token);
    Team team = teamService.getTeam(teamSlug);
    CalendarEventsResponse events = getTeamEvents(team, user, getDefaultFrom(), getDefaultTo());
    return icsGenerationService.generateIcs(events.events(), "Pedalons - " + team.getName());
  }

  private Instant resolveFrom(@Nullable Instant from) {
    return from != null ? from : getDefaultFrom();
  }

  private Instant resolveTo(@Nullable Instant to) {
    return to != null ? to : getDefaultTo();
  }

  @CheckAccess(entityType = EntityType.CALENDAR, action = ActionType.READ)
  public CalendarTokenDto getOrCreateToken() {
    User user = pedalonsQueryContext.getUser();
    CalendarToken token =
        calendarTokenRepository.findByUserId(user.getId()).orElseGet(() -> createToken(user));

    return buildTokenDto(token);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.CALENDAR, action = ActionType.UPDATE)
  public CalendarTokenDto regenerateToken() {
    User user = pedalonsQueryContext.getUser();

    Optional<CalendarToken> optionalCalendarToken =
        calendarTokenRepository.findByUserId(user.getId());
    if (optionalCalendarToken.isPresent()) {
      calendarTokenRepository.delete(optionalCalendarToken.get());
      calendarTokenRepository.flush();
    }

    CalendarToken newToken = createToken(user);
    return buildTokenDto(newToken);
  }

  User validateToken(String token) {
    return calendarTokenRepository
        .findByToken(token)
        .map(CalendarToken::getUser)
        .orElseThrow(ForbiddenException::new);
  }

  @Transactional
  CalendarToken createToken(User user) {
    String tokenValue = generateSecureToken();
    CalendarToken token = new CalendarToken(user, tokenValue);
    calendarTokenRepository.persist(token);
    return token;
  }

  private String generateSecureToken() {
    byte[] bytes = new byte[TOKEN_BYTES];
    new SecureRandom().nextBytes(bytes);
    return HexFormat.of().formatHex(bytes);
  }

  private CalendarTokenDto buildTokenDto(CalendarToken token) {
    String baseUrl = domainResolver.getEffectiveBaseUrl();
    String globalFeedUrl = baseUrl + "/api/calendar/ics?token=" + token.getToken();
    String teamFeedUrlTemplate =
        baseUrl + "/api/teams/{teamSlug}/calendar/ics?token=" + token.getToken();
    return new CalendarTokenDto(token.getToken(), globalFeedUrl, teamFeedUrlTemplate);
  }

  @Transactional
  protected CalendarEventsResponse getTeamEvents(Team team, User user, Instant from, Instant to) {
    return buildEvents(Set.of(team.getId()), user, from, to);
  }

  private CalendarEventsResponse getEventsForUserInternal(User user, Instant from, Instant to) {
    Set<Long> teamIds =
        userTeamRepository.findByUserId(user.getId()).stream()
            .map(UserTeam::getTeam)
            .map(Team::getId)
            .collect(Collectors.toSet());

    // An empty teamIds set means "no team filter" downstream, which would turn a personal calendar
    // into a domain-wide one.
    if (teamIds.isEmpty()) {
      return new CalendarEventsResponse(List.of());
    }

    return buildEvents(teamIds, user, from, to);
  }

  /**
   * Builds the whole window, then decorates it — deliberately in that order.
   *
   * <p>The default window is 210 days wide and has no pagination, so anything resolved per event is
   * resolved a few hundred times. The three per-user or per-asset pieces the agenda needs
   * (registrations, joined group names, thumbnails) are therefore loaded once for the entire window,
   * before any DTO is built: three statements, whatever the window returns. Route metrics and start
   * places cost nothing extra — {@code Ride.route}, {@code Ride.start} and the stage equivalents are
   * eager to-ones already loaded with the publication.
   */
  private CalendarEventsResponse buildEvents(
      Set<Long> teamIds, User user, Instant from, Instant to) {
    List<Ride> rides =
        getPublications(teamIds, user, from, to, PublicationType.RIDE).stream()
            .map(Ride.class::cast)
            .toList();
    List<Trip> trips =
        getPublications(teamIds, user, from, to, PublicationType.TRIP).stream()
            .map(Trip.class::cast)
            .toList();
    List<TripStage> stages = trips.stream().flatMap(trip -> trip.getStages().stream()).toList();

    UserParticipations participations =
        participationLookup.forPublications(user.getId(), ids(rides), ids(trips));
    Map<Long, String> groupNames =
        rideGroupRepository.findNamesByIds(participations.registeredGroupIdByRideId().values());
    Map<Long, String> thumbnails = thumbnailLookup.forTeamEntities(thumbnailIds(rides, stages));

    List<CalendarEventDto> events = new ArrayList<>(rides.size() + stages.size());
    for (Ride ride : rides) {
      events.add(toCalendarEvent(ride, participations, groupNames, thumbnails));
    }
    for (TripStage stage : stages) {
      events.add(toCalendarEvent(stage, participations, thumbnails));
    }
    return new CalendarEventsResponse(events);
  }

  private static List<Long> ids(List<? extends Publication> publications) {
    return publications.stream().map(Publication::getId).toList();
  }

  /**
   * Every id that may carry the picture of an event: the ride or stage itself, and the route it
   * points at, which is where the thumbnail lives when the outing has none of its own.
   */
  private static List<Long> thumbnailIds(List<Ride> rides, List<TripStage> stages) {
    List<Long> ids = new ArrayList<>();
    for (Ride ride : rides) {
      ids.add(ride.getId());
      if (ride.getRoute() != null) {
        ids.add(ride.getRoute().getId());
      }
    }
    for (TripStage stage : stages) {
      ids.add(stage.getId());
      if (stage.getRoute() != null) {
        ids.add(stage.getRoute().getId());
      }
    }
    return ids;
  }

  private static @Nullable String thumbnailUrl(
      Map<Long, String> thumbnails, Long entityId, @Nullable Route route) {
    String own = thumbnails.get(entityId);
    if (own != null) {
      return own;
    }
    return route != null ? thumbnails.get(route.getId()) : null;
  }

  /**
   * The ICS feeds authenticate through a calendar token rather than a session, so god mode is read
   * off the resolved {@code user} — {@link
   * fr.pedalons.service.security.PedalonsQueryContext#isPlatformAdmin()} would see no user there.
   */
  private List<Publication> getPublications(
      @Nullable Set<Long> teamIds,
      User user,
      Instant from,
      Instant to,
      PublicationType publicationType) {
    return allPublicationRepository.findAll(
        PublicationQuery.builder()
            .domainId(pedalonsQueryContext.getDomainId())
            .pinnedTeamId(pedalonsQueryContext.getPinnedTeamIdNullable())
            .userId(user.getId())
            .type(publicationType)
            .teamIds(teamIds)
            .from(from)
            .to(to)
            .includeDeleted(false)
            .platformAdmin(user.isPlatformAdmin())
            .build());
  }

  private CalendarEventDto toCalendarEvent(
      Ride ride,
      UserParticipations participations,
      Map<Long, String> groupNames,
      Map<Long, String> thumbnails) {
    Team team = ride.getTeam();
    Route route = ride.getRoute();
    Place start = ride.getStart();
    Long registeredGroupId = participations.registeredGroupId(ride.getId());
    return new CalendarEventDto(
        TsidUtils.toString(ride.getId()),
        ride.getName(),
        ride.getDateTime(),
        null,
        false,
        CalendarEventType.RIDE,
        team.getSlug(),
        team.getName(),
        ride.getSlug(),
        null,
        start != null ? start.getName() : null,
        route != null ? route.getDistance() : null,
        route != null ? route.getElevationGain() : null,
        thumbnailUrl(thumbnails, ride.getId(), route),
        registeredGroupId != null,
        registeredGroupId != null ? groupNames.get(registeredGroupId) : null,
        ride.getStatus());
  }

  private CalendarEventDto toCalendarEvent(
      TripStage stage, UserParticipations participations, Map<Long, String> thumbnails) {
    Team team = stage.getTeam();
    Trip trip = stage.getTrip();
    Route route = stage.getRoute();
    Place start = stage.getStartPlace();
    return new CalendarEventDto(
        TsidUtils.toString(stage.getId()),
        stage.getName(),
        stage.getDateTime(),
        null,
        true,
        CalendarEventType.TRIP_STAGE,
        team.getSlug(),
        team.getName(),
        stage.getSlug(),
        trip.getSlug(),
        start != null ? start.getName() : null,
        route != null ? route.getDistance() : null,
        route != null ? route.getElevationGain() : null,
        thumbnailUrl(thumbnails, stage.getId(), route),
        // A stage is not joined on its own: the registration lives on the parent trip, and trips
        // have no groups, hence no group name to report.
        participations.isRegisteredToTrip(trip.getId()),
        null,
        stage.getStatus());
  }

  protected Instant getDefaultFrom() {
    return Instant.now().minus(30, ChronoUnit.DAYS);
  }

  protected Instant getDefaultTo() {
    return Instant.now().plus(180, ChronoUnit.DAYS);
  }
}
