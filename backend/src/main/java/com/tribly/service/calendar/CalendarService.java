package com.tribly.service.calendar;

import com.tribly.common.TsidUtils;
import com.tribly.common.exception.ForbiddenException;
import com.tribly.domain.calendar.CalendarToken;
import com.tribly.domain.common.Publication;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.trip.Trip;
import com.tribly.domain.trip.TripStage;
import com.tribly.domain.user.User;
import com.tribly.dto.calendar.request.AuthMode;
import com.tribly.dto.calendar.response.CalendarEventDto;
import com.tribly.dto.calendar.response.CalendarEventType;
import com.tribly.dto.calendar.response.CalendarEventsResponse;
import com.tribly.dto.calendar.response.CalendarTokenDto;
import com.tribly.dto.publications.response.PublicationType;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.repository.calendar.CalendarTokenRepository;
import com.tribly.repository.common.AllPublicationRepository;
import com.tribly.repository.common.PublicationQuery;
import com.tribly.repository.team.UserTeamRepository;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.security.annotation.CheckAccess;
import com.tribly.service.team.TeamService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class CalendarService {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private static final int TOKEN_BYTES = 32; // 256 bits = 64 hex chars

  @Inject CalendarTokenRepository calendarTokenRepository;

  @Inject TriblyQueryContext triblyQueryContext;

  @Inject TeamService teamService;

  @Inject AllPublicationRepository allPublicationRepository;

  @Inject IcsGenerationService icsGenerationService;

  @Inject UserTeamRepository userTeamRepository;

  @ConfigProperty(name = "tribly.base-url", defaultValue = "http://localhost:8080")
  String baseUrl;

  @CheckAccess(entityType = EntityType.CALENDAR, action = ActionType.LIST_ALL_TEAMS)
  public CalendarEventsResponse getEventsForUser(
      AuthMode authMode, @Nullable Instant from, @Nullable Instant to) {
    if (from == null) {
      from = getDefaultFrom();
    }
    if (to == null) {
      to = getDefaultTo();
    }
    User user = triblyQueryContext.getUserNullable();
    return getEventsForUserInternal(user == null ? null : user.getId(), from, to);
  }

  @CheckAccess(entityType = EntityType.CALENDAR, action = ActionType.LIST_ALL_TEAMS)
  public String generateIcs(AuthMode authMode, String token) {
    User user = validateToken(token);

    Instant from = getDefaultFrom();
    Instant to = getDefaultTo();

    CalendarEventsResponse events = getEventsForUserInternal(user.getId(), from, to);
    return icsGenerationService.generateIcs(events.events(), "Tribly - Mes sorties");
  }

  @CheckAccess(entityType = EntityType.CALENDAR, action = ActionType.LIST)
  public CalendarEventsResponse getEventsForTeam(
      AuthMode authMode, String teamSlug, @Nullable Instant from, @Nullable Instant to) {
    if (from == null) {
      from = getDefaultFrom();
    }
    if (to == null) {
      to = getDefaultTo();
    }
    Team team = teamService.getTeam(teamSlug);
    User user = triblyQueryContext.getUser();
    return getTeamEvents(team, user, from, to);
  }

  @CheckAccess(entityType = EntityType.CALENDAR, action = ActionType.LIST)
  public String generateIcsForTeam(AuthMode authMode, String token, String teamSlug) {
    User user = validateToken(token);
    Team team = teamService.getTeam(teamSlug);

    Instant from = getDefaultFrom();
    Instant to = getDefaultTo();

    CalendarEventsResponse events = getTeamEvents(team, user, from, to);
    String calendarName = "Tribly - " + team.getName();
    return icsGenerationService.generateIcs(events.events(), calendarName);
  }

  @CheckAccess(entityType = EntityType.CALENDAR, action = ActionType.READ)
  public CalendarTokenDto getOrCreateToken() {
    User user = triblyQueryContext.getUser();
    CalendarToken token =
        calendarTokenRepository.findByUserId(user.getId()).orElseGet(() -> createToken(user));

    return buildTokenDto(token);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.CALENDAR, action = ActionType.UPDATE)
  public CalendarTokenDto regenerateToken() {
    User user = triblyQueryContext.getUser();

    calendarTokenRepository
        .findByUserId(user.getId())
        .ifPresent(
            existing -> {
              existing.setDeleted(true);
              calendarTokenRepository.persist(existing);
            });

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
    SECURE_RANDOM.nextBytes(bytes);
    return HexFormat.of().formatHex(bytes);
  }

  private CalendarTokenDto buildTokenDto(CalendarToken token) {
    String globalFeedUrl = baseUrl + "/api/calendar/ics?token=" + token.getToken();
    String teamFeedUrlTemplate =
        baseUrl + "/api/teams/{teamSlug}/calendar/ics?token=" + token.getToken();
    return new CalendarTokenDto(token.getToken(), globalFeedUrl, teamFeedUrlTemplate);
  }

  private CalendarEventsResponse getTeamEvents(Team team, User user, Instant from, Instant to) {
    Set<Long> teamIds = Set.of(team.getId());

    List<CalendarEventDto> events = new ArrayList<>();
    events.addAll(queryRides(teamIds, user.getId(), from, to));
    events.addAll(queryTripStages(teamIds, user.getId(), from, to));

    return new CalendarEventsResponse(events);
  }

  private CalendarEventsResponse getEventsForUserInternal(
      @Nullable Long userId, Instant from, Instant to) {
    Set<Long> teamIds = null;
    if (userId != null) {
      teamIds =
          userTeamRepository.findByUserId(userId).stream()
              .map(UserTeam::getTeam)
              .map(Team::getId)
              .collect(Collectors.toSet());
    }

    List<CalendarEventDto> events = new ArrayList<>();
    events.addAll(queryRides(teamIds, userId, from, to));
    events.addAll(queryTripStages(teamIds, userId, from, to));

    return new CalendarEventsResponse(events);
  }

  private List<CalendarEventDto> queryRides(
      @Nullable Set<Long> teamIds, @Nullable Long userId, Instant from, Instant to) {

    List<Publication> publications =
        getPublications(teamIds, userId, from, to, PublicationType.RIDE);
    return publications.stream().map(p -> (Ride) p).map(this::toCalendarEvent).toList();
  }

  private List<CalendarEventDto> queryTripStages(
      @Nullable Set<Long> teamIds, @Nullable Long userId, Instant from, Instant to) {
    List<Publication> publications =
        getPublications(teamIds, userId, from, to, PublicationType.TRIP);
    return publications.stream()
        .map(p -> (Trip) p)
        .flatMap(t -> t.getStages().stream())
        .map(this::toCalendarEvent)
        .toList();
  }

  private List<Publication> getPublications(
      @Nullable Set<Long> teamIds,
      @Nullable Long userId,
      Instant from,
      Instant to,
      PublicationType publicationType) {
    return allPublicationRepository
        .find(
            PublicationQuery.builder()
                .userId(userId)
                .type(publicationType)
                .teamIds(teamIds)
                .from(from)
                .to(to)
                .page(0)
                .size(1000)
                .build())
        .items();
  }

  private CalendarEventDto toCalendarEvent(Ride ride) {
    Team team = ride.getTeam();
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
        null);
  }

  private CalendarEventDto toCalendarEvent(TripStage stage) {
    Team team = stage.getTeam();
    Trip trip = stage.getTrip();
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
        null);
  }

  protected Instant getDefaultFrom() {
    return Instant.now().minus(30, ChronoUnit.DAYS);
  }

  protected Instant getDefaultTo() {
    return Instant.now().plus(180, ChronoUnit.DAYS);
  }
}
