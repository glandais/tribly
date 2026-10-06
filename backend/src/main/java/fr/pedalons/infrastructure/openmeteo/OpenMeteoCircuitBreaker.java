package fr.pedalons.infrastructure.openmeteo;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayDeque;
import java.util.Deque;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

/**
 * Keeps the worker off a provider in trouble, and within the daily call budget.
 *
 * <p>In memory, per instance, on purpose: during a rolling deploy two backends each keep their own,
 * which at worst doubles a few probes for a minute — the backoff written on each cell is what
 * actually spaces the retries. Open for {@value #OPEN_MINUTES} minutes after {@value
 * #FAILURE_THRESHOLD} consecutive failures, or at once on a 429; one success closes it.
 *
 * <p>The budget counts <em>locations</em>, which is what Open-Meteo bills: a request for 100 places
 * with 10 variables over 8 days is 100 calls. Three budgets, as the provider has three limits: per
 * UTC day (reset at midnight, the provider's own day), and per sliding hour and minute — on the free
 * plan 5 000 an hour and 600 a minute, which a backlog drained in one tick would otherwise exceed,
 * and whose 429 would then stop every refresh until the next full hour.
 */
@ApplicationScoped
public class OpenMeteoCircuitBreaker {

  private static final Logger LOG = Logger.getLogger(OpenMeteoCircuitBreaker.class);

  static final int FAILURE_THRESHOLD = 3;
  static final int OPEN_MINUTES = 10;

  @ConfigProperty(name = "pedalons.weather.daily-call-budget", defaultValue = "5000")
  int dailyCallBudget;

  @ConfigProperty(name = "pedalons.weather.hourly-call-budget", defaultValue = "4500")
  int hourlyCallBudget;

  @ConfigProperty(name = "pedalons.weather.per-minute-call-budget", defaultValue = "500")
  int perMinuteCallBudget;

  private int consecutiveFailures;
  private @Nullable Instant openUntil;
  private @Nullable LocalDate budgetDay;
  private int callsToday;

  /** The requests of the last hour, oldest first: when, and how many locations. */
  private final Deque<Calls> recent = new ArrayDeque<>();

  private record Calls(Instant at, int locations) {}

  /** Whether a request may go out now. */
  public synchronized boolean isClosed(Instant now) {
    return openUntil == null || !now.isBefore(openUntil);
  }

  /**
   * How many more locations may be asked for now: the least of what is left of today's budget, of
   * the last hour's and of the last minute's.
   */
  public synchronized int remainingBudget(Instant now) {
    rollDay(now);
    forgetBefore(now.minus(Duration.ofHours(1)));
    int lastHour = 0;
    int lastMinute = 0;
    Instant minuteAgo = now.minus(Duration.ofMinutes(1));
    for (Calls calls : recent) {
      lastHour += calls.locations();
      if (calls.at().isAfter(minuteAgo)) {
        lastMinute += calls.locations();
      }
    }
    int left =
        Math.min(
            dailyCallBudget - callsToday,
            Math.min(hourlyCallBudget - lastHour, perMinuteCallBudget - lastMinute));
    return Math.max(0, left);
  }

  /** Whether today's budget is what is spent — the one that waits for midnight UTC. */
  public synchronized boolean isDailyBudgetSpent(Instant now) {
    rollDay(now);
    return callsToday >= dailyCallBudget;
  }

  /** Counts a request against the budget, before sending it: a timed-out call was billed too. */
  public synchronized void recordCalls(int locations, Instant now) {
    rollDay(now);
    callsToday += locations;
    forgetBefore(now.minus(Duration.ofHours(1)));
    recent.addLast(new Calls(now, locations));
  }

  public synchronized void recordSuccess() {
    consecutiveFailures = 0;
    openUntil = null;
  }

  public synchronized void recordFailure(Instant now) {
    consecutiveFailures++;
    if (consecutiveFailures >= FAILURE_THRESHOLD) {
      open(now, consecutiveFailures + " consecutive failures");
    }
  }

  public synchronized void recordRateLimited(Instant now) {
    consecutiveFailures++;
    open(now, "rate limited");
  }

  private void open(Instant now, String why) {
    openUntil = now.plus(Duration.ofMinutes(OPEN_MINUTES));
    LOG.warnf("Open-Meteo circuit open for %d min: %s", OPEN_MINUTES, why);
  }

  private void forgetBefore(Instant cutoff) {
    while (!recent.isEmpty() && !recent.peekFirst().at().isAfter(cutoff)) {
      recent.removeFirst();
    }
  }

  private void rollDay(Instant now) {
    LocalDate today = LocalDate.ofInstant(now, ZoneOffset.UTC);
    if (!today.equals(budgetDay)) {
      budgetDay = today;
      callsToday = 0;
    }
  }
}
