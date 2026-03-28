package fr.pedalons.service.calendar;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.exception.ForbiddenException;
import fr.pedalons.domain.calendar.CalendarToken;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.calendar.request.AuthMode;
import fr.pedalons.dto.calendar.response.CalendarEventType;
import fr.pedalons.dto.calendar.response.CalendarEventsResponse;
import fr.pedalons.dto.calendar.response.CalendarTokenDto;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CalendarServiceTest extends AbstractBaseTest {

  @Inject CalendarService calendarService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject PedalonsQueryContext queryContext;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private User user1;
  private User user2;
  private Team team1;
  private Team team2;
  private Instant now;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    user1 = dataService.createUser("user1@example.com", "User One");
    user2 = dataService.createUser("user2@example.com", "User Two");
    team1 = dataService.createTeam(user1, "Team One", "team-one", Visibility.PUBLIC);
    team2 = dataService.createTeam(user2, "Team Two", "team-two", Visibility.PUBLIC);
    now = Instant.now();
  }

  // ==================== getEventsForUser ====================

  @Test
  void getEventsForUser_shouldReturnRidesFromUserTeams() {
    Ride ride = dataService.createRide(team1, user1, "Morning Ride", "morning-ride", now);

    queryContext.setUserForTest(user1);
    CalendarEventsResponse response = calendarService.getEventsForUser(AuthMode.WEB, null, null);

    assertEquals(1, response.events().size());
    assertEquals(ride.getName(), response.events().getFirst().title());
    assertEquals(CalendarEventType.RIDE, response.events().getFirst().type());
  }

  @Test
  void getEventsForUser_shouldReturnTripStagesFromUserTeams() {
    Trip trip = dataService.createTrip(team1, user1, "Summer Trip", now);
    TripStage stage = dataService.createTripStage(user1, trip, "Stage 1", now);

    queryContext.setUserForTest(user1);
    CalendarEventsResponse response = calendarService.getEventsForUser(AuthMode.WEB, null, null);

    assertEquals(1, response.events().size());
    assertEquals(stage.getName(), response.events().getFirst().title());
    assertEquals(CalendarEventType.TRIP_STAGE, response.events().getFirst().type());
  }

  @Test
  void getEventsForUser_shouldNotReturnEventsFromNonMemberTeams() {
    dataService.createRide(team2, user2, "Other Ride", "other-ride", now);

    queryContext.setUserForTest(user1);
    CalendarEventsResponse response = calendarService.getEventsForUser(AuthMode.WEB, null, null);

    assertTrue(response.events().isEmpty());
  }

  @Test
  void getEventsForUser_shouldFilterByDateRange() {
    Instant pastDate = now.minus(60, ChronoUnit.DAYS);
    Instant futureDate = now.plus(10, ChronoUnit.DAYS);

    dataService.createRide(team1, user1, "Past Ride", "past-ride", pastDate);
    Ride futureRide =
        dataService.createRide(team1, user1, "Future Ride", "future-ride", futureDate);

    queryContext.setUserForTest(user1);
    CalendarEventsResponse response =
        calendarService.getEventsForUser(
            AuthMode.WEB, now.minus(1, ChronoUnit.DAYS), now.plus(30, ChronoUnit.DAYS));

    assertEquals(1, response.events().size());
    assertEquals(futureRide.getName(), response.events().getFirst().title());
  }

  @Test
  void getEventsForUser_shouldReturnEventsFromMultipleTeams() {
    dataService.addUserToTeam(user1, team2, TeamRole.MEMBER);

    dataService.createRide(team1, user1, "Team1 Ride", "team1-ride", now);
    dataService.createRide(team2, user2, "Team2 Ride", "team2-ride", now.plus(1, ChronoUnit.HOURS));

    queryContext.setUserForTest(user1);
    CalendarEventsResponse response = calendarService.getEventsForUser(AuthMode.WEB, null, null);

    assertEquals(2, response.events().size());
  }

  @Test
  void getEventsForUser_shouldNotReturnDraftRides() {
    dataService.createRide(team1, user1, "Draft Ride", "draft-ride", now, Status.DRAFT);

    queryContext.setUserForTest(user2);
    CalendarEventsResponse response = calendarService.getEventsForUser(AuthMode.WEB, null, null);

    assertTrue(response.events().isEmpty());
  }

  // ==================== getEventsForTeam ====================

  @Test
  void getEventsForTeam_shouldReturnTeamRides() {
    Ride ride = dataService.createRide(team1, user1, "Team Ride", "team-ride", now);

    queryContext.setUserForTest(user1);
    CalendarEventsResponse response =
        calendarService.getEventsForTeam(AuthMode.WEB, team1.getSlug(), null, null);

    assertEquals(1, response.events().size());
    assertEquals(ride.getName(), response.events().getFirst().title());
  }

  @Test
  void getEventsForTeam_shouldReturnTeamTripStages() {
    Trip trip = dataService.createTrip(team1, user1, "Team Trip", now);
    TripStage stage = dataService.createTripStage(user1, trip, "Stage 1", now);

    queryContext.setUserForTest(user1);
    CalendarEventsResponse response =
        calendarService.getEventsForTeam(AuthMode.WEB, team1.getSlug(), null, null);

    assertEquals(1, response.events().size());
    assertEquals(stage.getName(), response.events().getFirst().title());
  }

  @Test
  void getEventsForTeam_shouldFilterByDateRange() {
    Instant pastDate = now.minus(60, ChronoUnit.DAYS);
    Instant futureDate = now.plus(10, ChronoUnit.DAYS);

    dataService.createRide(team1, user1, "Past Ride", "past-ride", pastDate);
    Ride futureRide =
        dataService.createRide(team1, user1, "Future Ride", "future-ride", futureDate);

    queryContext.setUserForTest(user1);
    CalendarEventsResponse response =
        calendarService.getEventsForTeam(
            AuthMode.WEB,
            team1.getSlug(),
            now.minus(1, ChronoUnit.DAYS),
            now.plus(30, ChronoUnit.DAYS));

    assertEquals(1, response.events().size());
    assertEquals(futureRide.getName(), response.events().getFirst().title());
  }

  // ==================== Token Management ====================

  @Test
  void getOrCreateToken_shouldCreateNewTokenForUser() {
    queryContext.setUserForTest(user1);
    CalendarTokenDto token = calendarService.getOrCreateToken();

    assertNotNull(token);
    assertNotNull(token.token());
    assertEquals(64, token.token().length()); // 32 bytes = 64 hex chars
    assertTrue(token.globalFeedUrl().contains(token.token()));
    assertTrue(token.teamFeedUrlTemplate().contains(token.token()));
  }

  @Test
  void getOrCreateToken_shouldReturnExistingToken() {
    CalendarToken existingToken = dataService.createCalendarToken(user1, "existing-token-123");

    queryContext.setUserForTest(user1);
    CalendarTokenDto token = calendarService.getOrCreateToken();

    assertEquals(existingToken.getToken(), token.token());
  }

  @Test
  void regenerateToken_shouldInvalidateOldToken() {
    CalendarToken oldToken = dataService.createCalendarToken(user1, "old-token-123");

    queryContext.setUserForTest(user1);
    CalendarTokenDto newToken = calendarService.regenerateToken();

    assertNotEquals(oldToken.getToken(), newToken.token());
  }

  @Test
  void regenerateToken_shouldCreateNewValidToken() {
    dataService.createCalendarToken(user1, "old-token-123");

    queryContext.setUserForTest(user1);
    CalendarTokenDto newToken = calendarService.regenerateToken();

    // Token should be valid - can be used to generate ICS
    User validated = calendarService.validateToken(newToken.token());
    assertEquals(user1.getId(), validated.getId());
  }

  // ==================== ICS Generation ====================

  @Test
  void generateIcs_shouldReturnIcsContentWithToken() {
    dataService.createRide(team1, user1, "Test Ride", "test-ride", now);
    CalendarToken token = dataService.createCalendarToken(user1, "valid-token-123");

    queryContext.setUserForTest(user1);
    String ics = calendarService.generateIcs(AuthMode.TOKEN, token.getToken());

    assertTrue(ics.contains("BEGIN:VCALENDAR"));
    assertTrue(ics.contains("Test Ride"));
    assertTrue(ics.contains("END:VCALENDAR"));
  }

  @Test
  void generateIcs_shouldThrowForInvalidToken() {
    queryContext.setUserForTest(user1);

    assertThrows(
        ForbiddenException.class,
        () -> calendarService.generateIcs(AuthMode.TOKEN, "invalid-token"));
  }

  @Test
  void generateIcsForTeam_shouldReturnTeamIcsContent() {
    dataService.createRide(team1, user1, "Team Ride", "team-ride", now);
    CalendarToken token = dataService.createCalendarToken(user1, "team-token-123");

    queryContext.setUserForTest(user1);
    String ics =
        calendarService.generateIcsForTeam(AuthMode.TOKEN, token.getToken(), team1.getSlug());

    assertTrue(ics.contains("BEGIN:VCALENDAR"));
    assertTrue(ics.contains("Team Ride"));
    assertTrue(ics.contains("Pedalons - Team One"));
  }

  @Test
  void generateIcsForTeam_shouldThrowForInvalidToken() {
    queryContext.setUserForTest(user1);

    assertThrows(
        ForbiddenException.class,
        () -> calendarService.generateIcsForTeam(AuthMode.TOKEN, "invalid-token", team1.getSlug()));
  }

  // ==================== Token Validation ====================

  @Test
  void validateToken_shouldReturnUserForValidToken() {
    CalendarToken token = dataService.createCalendarToken(user1, "valid-token-456");

    User validated = calendarService.validateToken(token.getToken());

    assertEquals(user1.getId(), validated.getId());
  }

  @Test
  void validateToken_shouldThrowForDeletedToken() {
    CalendarToken token = dataService.createCalendarToken(user1, "deleted-token");
    dataService.deleteCalendarToken(token);

    assertThrows(ForbiddenException.class, () -> calendarService.validateToken("deleted-token"));
  }

  @Test
  void validateToken_shouldThrowForNonexistentToken() {
    assertThrows(
        ForbiddenException.class, () -> calendarService.validateToken("nonexistent-token"));
  }
}
