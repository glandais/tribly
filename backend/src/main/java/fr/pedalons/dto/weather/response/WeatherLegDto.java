package fr.pedalons.dto.weather.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.WeatherStatus;
import fr.pedalons.service.weather.WeatherLeg;
import java.time.Instant;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(
    description =
        "The weather along one ridden route: a group of a ride (a stage of a trip, later). Passages"
            + " are estimated from startTime at averageSpeed.")
@ValidateSchema
public record WeatherLegDto(
    @Nullable
        @Schema(
            description =
                "The ride group (TSID). Absent for a ride without groups: the leg rides the ride's"
                    + " own route.")
        String groupId,
    @Schema(
            description =
                "State of this leg's forecast. NO_LOCATION when the leg has no route to sample:"
                    + " then no checkpoint, no segment.",
            required = true)
        WeatherStatus status,
    @Schema(description = "When the leg leaves", required = true) Instant startTime,
    @Schema(description = "Speed used for the passages, km/h", required = true) double averageSpeed,
    @Schema(
            description =
                "Whether averageSpeed is the 25 km/h default, the group having none — to be said"
                    + " on screen",
            required = true)
        boolean speedIsDefault,
    @Schema(description = "Length of the leg's route, metres", required = true) double distance,
    @Schema(description = "Estimated arrival", required = true) Instant arrivalTime,
    @Nullable @Schema(description = "The oldest fetch among the forecasts this leg reads")
        Instant fetchedAt,
    @Schema(description = "Forecast points, start to finish", required = true)
        List<WeatherCheckpointDto> checkpoints,
    @Schema(
            description = "The wind stretch by stretch, from one checkpoint to the next",
            required = true)
        List<WindSegmentDto> segments,
    @Schema(description = "Distance ridden against, across and with the wind", required = true)
        WindExposureDto windExposure,
    @Nullable
        @Schema(
            description =
                "The leg's dominant wind: circular mean of the directions, mean speed, highest"
                    + " gust")
        WindDto prevailingWind,
    @Nullable @Schema(description = "The first checkpoint where rain becomes likely, if any")
        WeatherRainAlertDto rainAlert) {

  public static WeatherLegDto from(WeatherLeg leg) {
    return new WeatherLegDto(
        leg.groupId() != null ? TsidUtils.toString(leg.groupId()) : null,
        leg.status(),
        leg.startTime(),
        leg.averageSpeed(),
        leg.speedIsDefault(),
        leg.distance(),
        leg.arrivalTime(),
        leg.fetchedAt(),
        leg.checkpoints().stream().map(WeatherCheckpointDto::from).toList(),
        leg.segments().stream().map(WindSegmentDto::from).toList(),
        WindExposureDto.from(leg.windExposure()),
        WindDto.fromNullable(leg.prevailingWind()),
        WeatherRainAlertDto.fromNullable(leg.rainAlert()));
  }
}
