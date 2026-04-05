package fr.pedalons.repository.gps;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.gps.GpsOAuthState;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.GpsServiceType;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class GpsOAuthStateRepositoryTest extends AbstractBaseTest {

  @Inject GpsOAuthStateRepository gpsOAuthStateRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    user = dataService.createUser("test@example.com", "Test User");
  }

  @Test
  void findValidByState_shouldReturnValidState() {
    Instant expiresAt = Instant.now().plus(10, ChronoUnit.MINUTES);
    dataService.createGpsOAuthState(user, "state-abc", GpsServiceType.HAMMERHEAD, expiresAt);

    Optional<GpsOAuthState> result = gpsOAuthStateRepository.findValidByState("state-abc");

    assertTrue(result.isPresent());
    assertEquals("state-abc", result.get().getState());
    assertEquals(GpsServiceType.HAMMERHEAD, result.get().getServiceType());
  }

  @Test
  void findValidByState_shouldReturnEmptyForNonexistent() {
    Optional<GpsOAuthState> result = gpsOAuthStateRepository.findValidByState("nonexistent");

    assertTrue(result.isEmpty());
  }

  @Test
  void findValidByState_shouldIgnoreExpiredStates() {
    dataService.createExpiredGpsOAuthState(user, "expired-state", GpsServiceType.GARMIN);

    Optional<GpsOAuthState> result = gpsOAuthStateRepository.findValidByState("expired-state");

    assertTrue(result.isEmpty());
  }

  @Test
  @Transactional
  void deleteExpiredStates_shouldDeleteExpiredAndLeaveValid() {
    Instant futureExpiry = Instant.now().plus(10, ChronoUnit.MINUTES);
    dataService.createGpsOAuthState(user, "valid-state", GpsServiceType.HAMMERHEAD, futureExpiry);
    dataService.createExpiredGpsOAuthState(user, "expired-1", GpsServiceType.HAMMERHEAD);
    dataService.createExpiredGpsOAuthState(user, "expired-2", GpsServiceType.GARMIN);

    long deletedCount = gpsOAuthStateRepository.deleteExpiredStates();

    assertEquals(2, deletedCount);
    assertTrue(gpsOAuthStateRepository.findValidByState("valid-state").isPresent());
  }
}
