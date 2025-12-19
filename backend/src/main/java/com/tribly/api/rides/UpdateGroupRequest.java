package com.tribly.api.rides;

import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride group update request")
public record UpdateGroupRequest(
    @Nullable @Schema(description = "Group name", nullable = true) @Size(min = 1, max = 100)
        String name,
    @Nullable @Schema(description = "Group description", nullable = true) String description,
    @Nullable @Schema(description = "Average speed in km/h", nullable = true) Integer averageSpeed,
    @Nullable @Schema(description = "Maximum participants", nullable = true)
        Integer maxParticipants,
    @Nullable @Schema(description = "Route ID (TSID)", nullable = true) String routeId) {}
