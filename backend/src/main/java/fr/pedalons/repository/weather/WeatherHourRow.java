package fr.pedalons.repository.weather;

import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * One forecast hour as the read side consumes it: the {@code weather_hourly} row with the sunrise
 * and sunset of its local date already joined, so telling day from night costs no second query.
 *
 * @param ownerId the cell id on the detail path; the ride id on the list path, where one row serves
 *     one ride's summary
 * @param sunrise null when the provider has no daily row for that date, or in polar day or night
 */
public record WeatherHourRow(
    long ownerId,
    Instant time,
    double temperature,
    double apparentTemperature,
    @Nullable Integer precipitationProbability,
    double precipitation,
    int weatherCode,
    double windSpeed,
    double windDirection,
    @Nullable Double windGusts,
    @Nullable Instant sunrise,
    @Nullable Instant sunset) {}
