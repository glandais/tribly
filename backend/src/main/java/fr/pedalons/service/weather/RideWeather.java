package fr.pedalons.service.weather;

import fr.pedalons.enums.WeatherStatus;
import java.time.Instant;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * A ride's weather, for its detail page. Built by {@link RideWeatherService#forRide} from the cache
 * alone.
 *
 * @param availableFrom for {@code NOT_YET_AVAILABLE}: seven days before the departure
 * @param fetchedAt the oldest fetch among the cells read
 * @param legs one per group, in group order; one without {@code groupId} for a ride without groups;
 *     empty for {@code OUT_OF_RANGE}, {@code NO_LOCATION} and {@code NOT_YET_AVAILABLE}
 */
public record RideWeather(
    WeatherStatus status,
    @Nullable Instant availableFrom,
    @Nullable Instant fetchedAt,
    DepartureWeather departure,
    List<WeatherLeg> legs) {

  /** A ride with nothing to compute: finished, cancelled, or without any place. */
  public static RideWeather of(WeatherStatus status) {
    return new RideWeather(status, null, null, DepartureWeather.of(status), List.of());
  }
}
