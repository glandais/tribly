package fr.pedalons.service.weather;

import fr.pedalons.enums.WeatherStatus;
import java.time.Instant;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * The weather along one ridden route: a group of a ride today, a stage of a trip tomorrow.
 *
 * @param groupId null for a ride without groups (its own route)
 * @param status {@code NO_LOCATION} when the leg has no route to sample
 * @param averageSpeed km/h used for the passages
 * @param speedIsDefault {@code averageSpeed} is the {@value RideWeatherCalculator#DEFAULT_SPEED_KMH}
 *     km/h default, to be said on screen
 * @param distance metres of the route
 * @param fetchedAt the oldest fetch among the cells read
 */
public record WeatherLeg(
    @Nullable Long groupId,
    WeatherStatus status,
    Instant startTime,
    double averageSpeed,
    boolean speedIsDefault,
    double distance,
    Instant arrivalTime,
    @Nullable Instant fetchedAt,
    List<WeatherCheckpoint> checkpoints,
    List<WindSegment> segments,
    WindExposure windExposure,
    @Nullable Wind prevailingWind,
    @Nullable RainAlert rainAlert) {}
