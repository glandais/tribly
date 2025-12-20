package com.tribly.dto.rides.request;

import com.tribly.enums.RideStatus;
import com.tribly.enums.Visibility;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride update request")
public record UpdateRideRequest(
    @Nullable @Schema(description = "Ride title", nullable = true) @Size(min = 3, max = 200)
        String title,
    @Nullable @Schema(description = "Ride description", nullable = true) @Size(max = 5000)
        String description,
    @Nullable @Schema(description = "Ride date", nullable = true) LocalDate date,
    @Nullable @Schema(description = "Start time", nullable = true) LocalTime startTime,
    @Nullable @Schema(description = "Ride status", nullable = true) RideStatus status,
    @Nullable @Schema(description = "Visibility level", nullable = true) Visibility visibility,
    @Nullable @Schema(description = "Route ID (TSID)", nullable = true) String routeId,
    @Nullable @Schema(description = "Meeting point ID (TSID)", nullable = true)
        String meetingPointId,
    @Nullable @Schema(description = "Publication timestamp", nullable = true) Instant publishAt) {}
