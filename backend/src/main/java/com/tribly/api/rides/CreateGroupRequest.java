package com.tribly.api.rides;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride group creation request")
public record CreateGroupRequest(
    @Schema(description = "Group name", examples = "Fast Group", required = true)
        @NotBlank
        @Size(min = 1, max = 100)
        String name,
    @Nullable @Schema(description = "Group description", nullable = true) String description,
    @Nullable @Schema(description = "Average speed in km/h", examples = "25", nullable = true)
        Integer averageSpeed,
    @Nullable @Schema(description = "Maximum participants", nullable = true)
        Integer maxParticipants,
    @Nullable @Schema(description = "Route ID (TSID) for this group", nullable = true)
        String routeId) {}
