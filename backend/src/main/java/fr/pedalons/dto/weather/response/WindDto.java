package fr.pedalons.dto.weather.response;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.CompassPoint;
import fr.pedalons.service.weather.Wind;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Wind at a place and an hour, 10 m above ground")
@ValidateSchema
public record WindDto(
    @Schema(description = "Mean wind speed, km/h", required = true) double speed,
    @Nullable @Schema(description = "Gusts, km/h. Absent when the model gives none.") Double gusts,
    @Schema(
            description =
                "Direction the wind comes FROM, degrees clockwise from north (0 = from the north,"
                    + " 90 = from the east)",
            required = true)
        double direction,
    @Schema(
            description =
                "direction on the eight-point rose, still the direction the wind comes FROM",
            required = true)
        CompassPoint compass) {

  public static WindDto from(Wind wind) {
    return new WindDto(wind.speed(), wind.gusts(), wind.direction(), wind.compass());
  }

  public static @Nullable WindDto fromNullable(@Nullable Wind wind) {
    return wind == null ? null : from(wind);
  }
}
