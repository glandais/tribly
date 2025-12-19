package com.tribly.api.users;

import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "User profile update request")
public record UpdateUserRequest(
    @Nullable
        @Schema(description = "User display name", examples = "John Doe", nullable = true)
        @Size(min = 1, max = 255)
        String displayName,
    @Nullable
        @Schema(description = "User locale (e.g. 'en', 'fr')", examples = "en", nullable = true)
        @Size(max = 10)
        String locale,
    @Nullable
        @Schema(
            description = "User timezone (e.g. 'Europe/Paris')",
            examples = "Europe/Paris",
            nullable = true)
        @Size(max = 50)
        String timezone) {}
