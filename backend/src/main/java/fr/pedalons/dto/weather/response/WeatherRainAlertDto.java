package fr.pedalons.dto.weather.response;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.WeatherCondition;
import fr.pedalons.service.weather.RainAlert;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "The first moment rain becomes likely: a probability of 50 % or more")
@ValidateSchema
public record WeatherRainAlertDto(
    @Schema(description = "Probability of precipitation then, % (0–100)", required = true)
        int probability,
    @Schema(description = "When — the passage at the checkpoint, or the hour", required = true)
        Instant time,
    @Nullable
        @Schema(
            description =
                "Where, metres from the start of the leg. Absent in a ride's summary, which only"
                    + " knows the departure point.")
        Double distance,
    @Schema(description = "The condition forecast then", required = true)
        WeatherCondition condition) {

  public static @Nullable WeatherRainAlertDto fromNullable(@Nullable RainAlert alert) {
    return alert == null
        ? null
        : new WeatherRainAlertDto(
            alert.probability(), alert.time(), alert.distance(), alert.condition());
  }
}
