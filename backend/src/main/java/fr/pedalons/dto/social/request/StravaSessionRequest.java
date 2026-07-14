package fr.pedalons.dto.social.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Exchange a one-time Strava login code for a session")
@ValidateSchema
public record StravaSessionRequest(
    @NotBlank @Schema(description = "One-time login code from the Strava callback", required = true)
        String code) {}
