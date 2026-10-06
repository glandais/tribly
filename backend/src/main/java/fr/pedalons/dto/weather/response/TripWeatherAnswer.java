package fr.pedalons.dto.weather.response;

import fr.pedalons.enums.WeatherStatus;
import org.jspecify.annotations.Nullable;

/**
 * What {@code getTripWeather} answers: the body, and the validator the resource puts in {@code
 * ETag} — as {@link RideWeatherAnswer} for a ride.
 *
 * @param etag a digest of the body; null when the body must not be cached at all ({@link
 *     WeatherStatus#UNAVAILABLE})
 */
public record TripWeatherAnswer(TripWeatherDto body, @Nullable String etag) {}
