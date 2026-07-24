package fr.pedalons.dto.config;

import fr.pedalons.dto.validation.ValidateSchema;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Server API contract version and build information")
@ValidateSchema
public record VersionDto(
    @Schema(
            description =
                "API contract version (semver). Bumped whenever the OpenAPI contract changes, and"
                    + " also exposed as info.version in the contract itself.",
            required = true)
        String apiVersion,
    @Schema(description = "Short git commit the server was built from, null when unavailable")
        @Nullable String commit,
    @Schema(description = "Build timestamp (ISO-8601, UTC), null when unavailable")
        @Nullable String buildTime) {}
