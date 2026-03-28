package fr.pedalons.repository.calendar;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.calendar.CalendarToken;
import fr.pedalons.domain.user.User;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CalendarTokenRepositoryTest extends AbstractBaseTest {

  @Inject CalendarTokenRepository calendarTokenRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private User user1;
  private User user2;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user1 = dataService.createUser("user1@example.com", "User One");
    user2 = dataService.createUser("user2@example.com", "User Two");
  }

  @Test
  void findByToken_shouldReturnTokenWhenExists() {
    CalendarToken token = dataService.createCalendarToken(user1, "test-token-123");

    Optional<CalendarToken> result = calendarTokenRepository.findByToken("test-token-123");

    assertTrue(result.isPresent());
    assertEquals(token.getId(), result.get().getId());
    assertEquals(user1.getId(), result.get().getUser().getId());
  }

  @Test
  void findByToken_shouldReturnEmptyWhenNotExists() {
    Optional<CalendarToken> result = calendarTokenRepository.findByToken("nonexistent-token");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByToken_shouldIgnoreDeletedTokens() {
    CalendarToken token = dataService.createCalendarToken(user1, "deleted-token");
    dataService.deleteCalendarToken(token);

    Optional<CalendarToken> result = calendarTokenRepository.findByToken("deleted-token");

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserId_shouldReturnTokenWhenExists() {
    CalendarToken token = dataService.createCalendarToken(user1, "user-token-456");

    Optional<CalendarToken> result = calendarTokenRepository.findByUserId(user1.getId());

    assertTrue(result.isPresent());
    assertEquals(token.getId(), result.get().getId());
    assertEquals("user-token-456", result.get().getToken());
  }

  @Test
  void findByUserId_shouldReturnEmptyWhenUserHasNoToken() {
    Optional<CalendarToken> result = calendarTokenRepository.findByUserId(user1.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserId_shouldIgnoreDeletedTokens() {
    CalendarToken token = dataService.createCalendarToken(user1, "to-be-deleted");
    dataService.deleteCalendarToken(token);

    Optional<CalendarToken> result = calendarTokenRepository.findByUserId(user1.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void findByUserId_shouldReturnCorrectTokenForUser() {
    dataService.createCalendarToken(user1, "token-for-user1");
    dataService.createCalendarToken(user2, "token-for-user2");

    Optional<CalendarToken> result1 = calendarTokenRepository.findByUserId(user1.getId());
    Optional<CalendarToken> result2 = calendarTokenRepository.findByUserId(user2.getId());

    assertTrue(result1.isPresent());
    assertEquals("token-for-user1", result1.get().getToken());

    assertTrue(result2.isPresent());
    assertEquals("token-for-user2", result2.get().getToken());
  }
}
