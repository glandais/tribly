package fr.pedalons.service.weather;

import fr.pedalons.repository.weather.WeatherCellRepository;
import fr.pedalons.repository.weather.WeatherDailyRepository;
import fr.pedalons.repository.weather.WeatherHourlyRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import org.jboss.logging.Logger;

/**
 * Nightly purge of the weather cache: the hours more than a day past, the days before those, and
 * the cells no ride has asked for in two days (with their rows). Idempotent: two backends of a
 * rolling deploy may both run it.
 */
@ApplicationScoped
public class WeatherHousekeeping {

  private static final Logger LOG = Logger.getLogger(WeatherHousekeeping.class);

  static final Duration HOURS_KEPT = Duration.ofDays(1);
  static final Duration CELLS_KEPT = Duration.ofDays(2);

  @Inject WeatherCellRepository cellRepository;
  @Inject WeatherHourlyRepository hourlyRepository;
  @Inject WeatherDailyRepository dailyRepository;

  @Scheduled(cron = "0 40 3 * * ?")
  void nightly() {
    try {
      purge(Instant.now());
    } catch (Exception e) {
      LOG.error("Weather cache purge failed", e);
    }
  }

  /** Public for the tests, which run with the scheduler off. */
  @Transactional
  public void purge(Instant now) {
    Instant hourCutoff = now.minus(HOURS_KEPT);
    long hours = hourlyRepository.deleteBefore(hourCutoff);
    // A date is local to its cell: a day's margin keeps every zone's "yesterday".
    long days =
        dailyRepository.deleteBefore(LocalDate.ofInstant(hourCutoff, ZoneOffset.UTC).minusDays(1));
    int cells = cellRepository.deleteUndemandedBefore(now.minus(CELLS_KEPT));
    if (hours + days + cells > 0) {
      LOG.infof(
          "Weather cache purge: %d hour(s), %d day(s), %d cell(s) deleted", hours, days, cells);
    }
  }
}
