package com.tribly.api.rides;

import com.tribly.domain.common.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

// Request DTOs
@Schema(description = "Ride creation request")
public record CreateRideRequest(
    @Schema(description = "Ride title", examples = "Sunday Morning Ride", required = true)
        @NotBlank
        @Size(min = 3, max = 200)
        String title,
    @Nullable @Schema(description = "Ride description", nullable = true) @Size(max = 5000)
        String description,
    @Schema(description = "Ride date", examples = "2025-06-15", required = true) LocalDate date,
    @Nullable @Schema(description = "Start time", examples = "09:00", nullable = true)
        LocalTime startTime,
    @Nullable @Schema(description = "Route ID (TSID)", nullable = true) String routeId,
    @Nullable @Schema(description = "Meeting point ID (TSID)", nullable = true)
        String meetingPointId,
    @Nullable @Schema(description = "Visibility level", nullable = true) Visibility visibility,
    @Nullable
        @Schema(description = "Publication timestamp (for scheduled publishing)", nullable = true)
        Instant publishAt,
    @Nullable @Schema(description = "Ride groups to create", nullable = true)
        List<CreateGroupRequest> groups) {}
