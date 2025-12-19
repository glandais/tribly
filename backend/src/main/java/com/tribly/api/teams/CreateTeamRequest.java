package com.tribly.api.teams;

import com.tribly.domain.common.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Team creation request")
public record CreateTeamRequest(
    @Schema(description = "Team name", examples = "Awesome Cycling Team", required = true)
        @NotBlank
        @Size(min = 2, max = 255)
        String name,
    @Nullable
        @Schema(
            description = "Team description",
            examples = "A team for weekend warriors",
            nullable = true)
        @Size(max = 2000)
        String description,
    @Schema(
            description = "Whether the team is publicly visible",
            examples = "true",
            required = true)
        Visibility visibility,
    @Nullable
        @Schema(
            description = "Maximum number of members (null = unlimited)",
            examples = "50",
            nullable = true)
        Integer maxMembers) {}
