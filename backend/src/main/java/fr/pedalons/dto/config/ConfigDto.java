package fr.pedalons.dto.config;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Application configuration")
@ValidateSchema
public record ConfigDto(
    @Schema(description = "WebAuthn Relying Party ID (domain)", required = true)
        String webAuthnRpId,
    @Schema(description = "Application name", required = true) String appName,
    @Schema(description = "Single team mode - team creation disabled", required = true)
        boolean singleTeam) {}
