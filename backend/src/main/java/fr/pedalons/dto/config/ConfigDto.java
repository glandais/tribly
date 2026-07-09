package fr.pedalons.dto.config;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Application configuration")
@ValidateSchema
public record ConfigDto(
    @Schema(description = "WebAuthn Relying Party ID (effective host)", required = true)
        String webAuthnRpId,
    @Schema(description = "Application name", required = true) String appName,
    @Schema(description = "Single team mode - team creation disabled", required = true)
        boolean singleTeam,
    @Schema(
            description =
                "Slug of the team the site is pinned to (dedicated hostname / alias). Null on a"
                    + " regular multi-team domain. When set, the app roots on this team.")
        @Nullable String pinnedTeamSlug) {}
