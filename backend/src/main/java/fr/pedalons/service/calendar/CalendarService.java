package fr.pedalons.service.calendar;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.ForbiddenException;
import fr.pedalons.domain.calendar.CalendarToken;
import fr.pedalons.domain.common.Publication;
import fr.pedalons.domain.ride.Ride;
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
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.repository.calendar.CalendarTokenRepository;
import fr.pedalons.repository.common.AllPublicationRepository;
import fr.pedalons.repository.common.PublicationQuery;
import fr.pedalons.repository.team.UserTeamRepository;
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

  @CheckAccess(entityType = EntityType.CALENDAR, action = ActionType.LIST_ALL_TEAMS)
  public CalendarEventsResponse getEventsForUser(
      AuthMode authMode, @Nullable Instant from, @Nullable Instant to) {
    User user = pedalonsQueryContext.getUser();
    return getEventsForUserInternal(user.getId(), resolveFrom(from), resolveTo(to));
  }

  @CheckAccess(entityType = EntityType.CALENDAR, action = ActionType.LIST_ALL_TEAMS)
  public String generateIcs(AuthMode authMode, String token) {
    User user = validateToken(token);
    CalendarEventsResponse events =
        getEventsForUserInternal(user.getId(), getDefaultFrom(), getDefaultTo());
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
    Set<Long> teamIds = Set.of(team.getId());

    List<CalendarEventDto> events = new ArrayList<>();
    events.addAll(queryRides(teamIds, user.getId(), from, to));
    events.addAll(queryTripStages(teamIds, user.getId(), from, to));

    return new CalendarEventsResponse(events);
  }

  private CalendarEventsResponse getEventsForUserInternal(Long userId, Instant from, Instant to) {
    Set<Long> teamIds =
        userTeamRepository.findByUserId(userId).stream()
            .map(UserTeam::getTeam)
            .map(Team::getId)
            .collect(Collectors.toSet());

    List<CalendarEventDto> events = new ArrayList<>();
    events.addAll(queryRides(teamIds, userId, from, to));
    events.addAll(queryTripStages(teamIds, userId, from, to));

    return new CalendarEventsResponse(events);
  }

  private List<CalendarEventDto> queryRides(
      Set<Long> teamIds, Long userId, Instant from, Instant to) {

    return getPublications(teamIds, userId, from, to, PublicationType.RIDE).stream()
        .map(p -> (Ride) p)
        .map(this::toCalendarEvent)
        .toList();
  }

  private List<CalendarEventDto> queryTripStages(
      Set<Long> teamIds, Long userId, Instant from, Instant to) {
    return getPublications(teamIds, userId, from, to, PublicationType.TRIP).stream()
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
    return allPublicationRepository.findAll(
        PublicationQuery.builder()
            .domainId(pedalonsQueryContext.getDomainId())
            .userId(userId)
            .type(publicationType)
            .teamIds(teamIds)
            .from(from)
            .to(to)
            .includeDeleted(false)
            .platformAdmin(false)
            .build());
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
        trip.getSlug());
  }

  protected Instant getDefaultFrom() {
    return Instant.now().minus(30, ChronoUnit.DAYS);
  }

  protected Instant getDefaultTo() {
    return Instant.now().plus(180, ChronoUnit.DAYS);
  }
}
