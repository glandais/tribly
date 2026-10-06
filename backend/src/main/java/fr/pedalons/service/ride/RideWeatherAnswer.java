package fr.pedalons.service.ride;

import fr.pedalons.dto.weather.response.RideWeatherDto;
import fr.pedalons.enums.WeatherStatus;
import org.jspecify.annotations.Nullable;

/**
 * What {@code getRideWeather} answers: the body, and the validator the resource puts in {@code
 * ETag}.
 *
 * @param etag a digest of the body, so it changes with anything the body says — the ride, its
 *     groups, a new fetch, or the clock turning a forecast {@code STALE}; null when the body must
 *     not be cached at all ({@link WeatherStatus#UNAVAILABLE})
 */
public record RideWeatherAnswer(RideWeatherDto body, @Nullable String etag) {

  /** {@code UNAVAILABLE} offers a retry: no client may keep it, not even to revalidate. */
  public boolean cacheable() {
    return etag != null;
  }
}
