package fr.pedalons.dto.social.response;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Strava OAuth authorization URL response")
@ValidateSchema
public record StravaAuthUrlResponse(
    @Schema(description = "URL to redirect the user to for Strava authorization", required = true)
        String authorizationUrl) {}
