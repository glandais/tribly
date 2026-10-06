package fr.pedalons.service.weather;

import fr.pedalons.enums.RelativeWind;
import fr.pedalons.enums.WeatherCheckpointKind;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * A forecast point along a leg. <b>No coordinates</b>, deliberately: a PUBLIC ride may ride a TEAM
 * route, and the clients place the point by {@code distance} on the geometry they may read.
 *
 * @param distance metres from the start of the leg's route
 * @param elevation metres, from the track
 * @param time estimated passage
 * @param weather null when the cache has nothing for that place and hour
 * @param relativeWind on the stretch that starts here (for the finish, the stretch that ends here)
 * @param headwind km/h, signed: positive against the rider
 * @param relativeWindAngle degrees, clockwise, of the direction the wind blows <em>towards</em>,
 *     relative to the direction of travel: 0 = from behind, 180 = in the face
 */
public record WeatherCheckpoint(
    int index,
    WeatherCheckpointKind kind,
    double distance,
    @Nullable Double elevation,
    Instant time,
    @Nullable WeatherConditions weather,
    @Nullable RelativeWind relativeWind,
    @Nullable Double headwind,
    @Nullable Double relativeWindAngle) {}
