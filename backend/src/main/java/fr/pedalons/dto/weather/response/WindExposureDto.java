package fr.pedalons.dto.weather.response;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.service.weather.WindExposure;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
    description =
        "How much of a leg rides against, across and with the wind, metres. All zero when no"
            + " stretch has weather.")
@ValidateSchema
public record WindExposureDto(
    @Schema(description = "Metres with a HEAD wind", required = true) double head,
    @Schema(description = "Metres with a CROSS wind", required = true) double cross,
    @Schema(description = "Metres with a TAIL wind", required = true) double tail) {

  public static WindExposureDto from(WindExposure exposure) {
    return new WindExposureDto(exposure.head(), exposure.cross(), exposure.tail());
  }
}
