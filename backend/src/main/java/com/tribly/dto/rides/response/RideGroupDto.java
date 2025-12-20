package com.tribly.dto.rides.response;

import com.tribly.domain.ride.RideGroup;
import com.tribly.infrastructure.id.TsidUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride group information")
public record RideGroupDto(
    @Schema(description = "Group ID (TSID)", required = true) String id,
    @Schema(description = "Group name", required = true) String name,
    @Nullable @Schema(description = "Group description", nullable = true) String description,
    @Nullable @Schema(description = "Average speed in km/h", nullable = true) Integer averageSpeed,
    @Nullable @Schema(description = "Maximum participants", nullable = true)
        Integer maxParticipants,
    @Schema(description = "Current number of participants", required = true)
        int currentParticipants,
    @Schema(description = "Sort order", required = true) int sortOrder) {
  public static RideGroupDto from(RideGroup group) {
    return new RideGroupDto(
        TsidUtils.toString(group.getId()),
        group.getName(),
        group.getDescription(),
        group.getAverageSpeed(),
        group.getMaxParticipants(),
        group.getCurrentParticipants(),
        group.getSortOrder());
  }
}
