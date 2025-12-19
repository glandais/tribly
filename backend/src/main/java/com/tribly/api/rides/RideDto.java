package com.tribly.api.rides;

import com.tribly.domain.common.Visibility;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideStatus;
import com.tribly.infrastructure.id.TsidUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

// Response DTOs
@Schema(description = "Ride summary data")
public record RideDto(
    @Schema(description = "Ride ID (TSID)", required = true) String id,
    @Schema(description = "Ride URL slug", required = true) String slug,
    @Schema(description = "Ride title", required = true) String title,
    @Nullable @Schema(description = "Ride description", nullable = true) String description,
    @Schema(description = "Ride date", required = true) LocalDate date,
    @Nullable @Schema(description = "Start time", nullable = true) LocalTime startTime,
    @Schema(description = "Ride status", required = true) RideStatus status,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Schema(description = "Number of participants", required = true) int participantCount,
    @Schema(description = "Number of groups", required = true) int groupCount,
    @Nullable @Schema(description = "Publication timestamp", nullable = true) String publishAt,
    @Nullable @Schema(description = "Creation timestamp", nullable = true) String createdAt) {
  public static RideDto from(Ride ride) {
    return new RideDto(
        TsidUtils.toString(ride.getId()),
        ride.getSlug(),
        ride.getTitle(),
        ride.getDescription(),
        ride.getDate(),
        ride.getStartTime(),
        ride.getStatus(),
        ride.getVisibility(),
        ride.getParticipantCount(),
        ride.getGroupCount(),
        ride.getPublishAt() != null ? ride.getPublishAt().toString() : null,
        ride.getCreatedAt().toString());
  }
}
