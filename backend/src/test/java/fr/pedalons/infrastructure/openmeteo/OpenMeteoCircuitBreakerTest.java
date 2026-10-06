package fr.pedalons.infrastructure.openmeteo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** When the worker stops asking Open-Meteo, and for how long. */
class OpenMeteoCircuitBreakerTest {

  private static final Instant NOW = Instant.parse("2026-10-05T12:00:00Z");

  private OpenMeteoCircuitBreaker breaker;

  @BeforeEach
  void setUp() {
    breaker = new OpenMeteoCircuitBreaker();
    breaker.dailyCallBudget = 500;
    breaker.hourlyCallBudget = 500;
    breaker.perMinuteCallBudget = 500;
  }

  @Test
  void threeFailuresInARow_shouldOpenItForTenMinutes() {
    breaker.recordFailure(NOW);
    breaker.recordFailure(NOW);
    assertTrue(breaker.isClosed(NOW));

    breaker.recordFailure(NOW);

    assertFalse(breaker.isClosed(NOW));
    assertFalse(breaker.isClosed(NOW.plus(Duration.ofMinutes(9))));
    assertTrue(breaker.isClosed(NOW.plus(Duration.ofMinutes(10))));
  }

  @Test
  void aSuccess_shouldResetTheFailureCount() {
    breaker.recordFailure(NOW);
    breaker.recordFailure(NOW);
    breaker.recordSuccess();
    breaker.recordFailure(NOW);

    assertTrue(breaker.isClosed(NOW));
  }

  @Test
  void aRateLimit_shouldOpenItAtOnce() {
    breaker.recordRateLimited(NOW);

    assertFalse(breaker.isClosed(NOW));
    assertTrue(breaker.isClosed(NOW.plus(Duration.ofMinutes(10))));
  }

  @Test
  void budget_shouldCountLocationsAndResetAtMidnightUtc() {
    assertEquals(500, breaker.remainingBudget(NOW));

    breaker.recordCalls(120, NOW);
    breaker.recordCalls(400, NOW);

    // Never negative: a request already counted may have overshot.
    assertEquals(0, breaker.remainingBudget(NOW));
    assertEquals(0, breaker.remainingBudget(Instant.parse("2026-10-05T23:59:59Z")));
    assertEquals(500, breaker.remainingBudget(Instant.parse("2026-10-06T00:00:00Z")));
    assertFalse(breaker.isDailyBudgetSpent(Instant.parse("2026-10-06T00:00:00Z")));
  }

  @Test
  void dailyBudgetSpent_shouldTellTheDayFromTheMinute() {
    breaker.recordCalls(500, NOW);

    assertTrue(breaker.isDailyBudgetSpent(NOW));
  }

  @Test
  void perMinuteBudget_shouldCapABacklogDrainedInOneTick_andSlideWithTheClock() {
    breaker.dailyCallBudget = 5_000;
    breaker.hourlyCallBudget = 5_000;
    breaker.perMinuteCallBudget = 500;

    // Five rounds of 100 in the same tick: the sixth would cross Open-Meteo's 600 a minute.
    for (int round = 0; round < 5; round++) {
      assertEquals(500 - round * 100, breaker.remainingBudget(NOW.plusSeconds(round)));
      breaker.recordCalls(100, NOW.plusSeconds(round));
    }
    assertEquals(0, breaker.remainingBudget(NOW.plusSeconds(5)));
    assertFalse(breaker.isDailyBudgetSpent(NOW.plusSeconds(5)), "the minute, not the day");

    // A sliding minute: the first round is forgotten a minute after it went, not at :00.
    assertEquals(100, breaker.remainingBudget(NOW.plusSeconds(60)));
    assertEquals(500, breaker.remainingBudget(NOW.plusSeconds(65)));
  }

  @Test
  void hourlyBudget_shouldCapTheCallsOfTheLastHour() {
    breaker.dailyCallBudget = 20_000;
    breaker.hourlyCallBudget = 1_000;
    breaker.perMinuteCallBudget = 1_000;

    breaker.recordCalls(500, NOW);
    breaker.recordCalls(400, NOW.plus(Duration.ofMinutes(10)));

    assertEquals(100, breaker.remainingBudget(NOW.plus(Duration.ofMinutes(20))));
    assertEquals(600, breaker.remainingBudget(NOW.plus(Duration.ofMinutes(60))));
    assertEquals(1_000, breaker.remainingBudget(NOW.plus(Duration.ofMinutes(71))));
  }
}
