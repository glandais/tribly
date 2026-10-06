package fr.pedalons.service.weather;

import fr.pedalons.enums.WeatherCondition;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * The first moment the rain becomes likely ({@value RideWeatherCalculator#RAIN_ALERT_PROBABILITY}
 * % or more).
 *
 * @param distance metres from the start on a leg; null in a ride's summary, which only knows the
 *     departure point
 */
public record RainAlert(
    int probability, Instant time, @Nullable Double distance, WeatherCondition condition) {}
