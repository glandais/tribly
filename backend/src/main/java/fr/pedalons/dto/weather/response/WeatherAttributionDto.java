package fr.pedalons.dto.weather.response;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(
    description =
        "Who the forecast comes from. Its licence (CC BY 4.0) asks every display to credit it:"
            + " show the name, linked to the url.")
@ValidateSchema
public record WeatherAttributionDto(
    @Schema(description = "Name to display, e.g. \"Open-Meteo.com\"", required = true) String name,
    @Schema(description = "Link of the credit", required = true) String url) {}
