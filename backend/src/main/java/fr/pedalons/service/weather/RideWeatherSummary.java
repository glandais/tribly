package fr.pedalons.service.weather;

import fr.pedalons.enums.WeatherCondition;
import fr.pedalons.enums.WeatherStatus;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * A ride's weather on a card: the departure point, over the window from the start to the estimated
 * arrival of the last group. Only built for {@code OK}, {@code STALE} and {@code NOT_YET_AVAILABLE};
 * everything but {@code status} and {@code availableFrom} is null for the last one.
 *
 * @param temperature at the departure hour, °C
 * @param temperatureMin over the window
 * @param maxPrecipitationProbability over the window, when the model gives one
 * @param wind at the departure hour
 * @param rainAlert the first hour of the window with rain likely; its {@code distance} is null
 */
public record RideWeatherSummary(
    WeatherStatus status,
    @Nullable Instant availableFrom,
    @Nullable Integer weatherCode,
    @Nullable WeatherCondition condition,
    @Nullable Boolean daylight,
    @Nullable Double temperature,
    @Nullable Double temperatureMin,
    @Nullable Double temperatureMax,
    @Nullable Integer maxPrecipitationProbability,
    @Nullable Wind wind,
    @Nullable RainAlert rainAlert) {

  public static RideWeatherSummary notYetAvailable(Instant availableFrom) {
    return new RideWeatherSummary(
        WeatherStatus.NOT_YET_AVAILABLE,
        availableFrom,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null);
  }
}
