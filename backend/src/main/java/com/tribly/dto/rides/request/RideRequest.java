package com.tribly.dto.rides.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.id.TsidUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride request")
@ValidateSchema
public record RideRequest(
    @Schema(description = "Ride name", examples = "Sunday Morning Ride", required = true)
        @NotBlank
        @Size(min = 3, max = 200)
        String name,
    @Nullable @Schema(description = "Ride description") @Size(max = 5000) String description,
    @Schema(description = "Ride date/time", examples = "2025-06-15", required = true)
        Instant dateTime,
    @Schema(description = "Ride status", required = true) Status status,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Nullable @Schema(description = "Route ID (TSID)") String routeId,
    @Nullable @Schema(description = "Publication timestamp (for scheduled publishing)")
        Instant publishAt,
    @Schema(description = "Ride groups to create", required = true)
        List<@Valid GroupRequest> groups) {

  @JsonIgnore
  @Nullable
  public Long getRouteIdLong() {
    return TsidUtils.toLongNullable(routeId);
  }
}
