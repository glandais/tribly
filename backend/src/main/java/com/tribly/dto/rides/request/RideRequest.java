package com.tribly.dto.rides.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.RideStatus;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride request")
@ValidateSchema
public record RideRequest(
    @Schema(description = "Ride title", examples = "Sunday Morning Ride", required = true)
        @NotBlank
        @Size(min = 3, max = 200)
        String title,
    @Nullable @Schema(description = "Ride description", nullable = true) @Size(max = 5000)
        String description,
    @Schema(description = "Ride date", examples = "2025-06-15", required = true) LocalDate date,
    @Nullable @Schema(description = "Start time", examples = "09:00", nullable = true)
        LocalTime startTime,
    @Schema(description = "Ride status", required = true) RideStatus status,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Nullable @Schema(description = "Route ID (TSID)", nullable = true) String routeId,
    @Nullable @Schema(description = "Meeting point ID (TSID)", nullable = true)
        String meetingPointId,
    @Nullable
        @Schema(description = "Publication timestamp (for scheduled publishing)", nullable = true)
        Instant publishAt,
    @Schema(description = "Ride groups to create", required = true)
        List<@Valid GroupRequest> groups) {

  @JsonIgnore
  @Nullable
  public Long getRouteIdLong() {
    return TsidUtils.toLongNullable(routeId);
  }

  @JsonIgnore
  @Nullable
  public Long getMeetingPointIdLong() {
    return TsidUtils.toLongNullable(meetingPointId);
  }
}
