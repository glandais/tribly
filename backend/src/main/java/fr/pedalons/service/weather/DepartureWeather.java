package fr.pedalons.service.weather;

import fr.pedalons.enums.WeatherStatus;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * The weather at the meeting point when the ride leaves.
 *
 * @param conditions present for {@code OK} and {@code STALE} only
 * @param sunrise of the departure's local date, when known
 * @param fetchedAt when the departure cell was last fetched
 */
public record DepartureWeather(
    WeatherStatus status,
    @Nullable WeatherConditions conditions,
    @Nullable Instant sunrise,
    @Nullable Instant sunset,
    @Nullable Instant fetchedAt) {

  public static DepartureWeather of(WeatherStatus status) {
    return new DepartureWeather(status, null, null, null, null);
  }
}
