package com.tribly.dto.teams.request;

import com.tribly.enums.Visibility;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Team update request")
public record UpdateTeamRequest(
    @Nullable @Schema(description = "Team name", nullable = true) @Size(min = 2, max = 255)
        String name,
    @Nullable @Schema(description = "Team description", nullable = true) @Size(max = 2000)
        String description,
    @Nullable @Schema(description = "Whether the team is publicly visible", nullable = true)
        Visibility visibility,
    @Nullable @Schema(description = "Logo image URL", nullable = true) String logoUrl,
    @Nullable @Schema(description = "Cover image URL", nullable = true) String coverImageUrl,
    @Nullable @Schema(description = "Maximum number of members (null = unlimited)", nullable = true)
        Integer maxMembers) {}
