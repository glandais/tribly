package com.tribly.dto.ridetemplates.response;

import com.tribly.domain.ridetemplate.RideTemplateGroup;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.infrastructure.id.TsidUtils;
import java.time.LocalTime;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride template group information")
@ValidateSchema
public record RideTemplateGroupDto(
    @Schema(description = "Group ID (TSID)", required = true) String id,
    @Schema(description = "Group name", required = true) String name,
    @Nullable LocalTime time,
    @Nullable @Schema(description = "Average speed in km/h") Integer averageSpeed,
    @Nullable @Schema(description = "Maximum participants") Integer maxParticipants,
    @Schema(description = "Sort order", required = true) int sortOrder) {

  public static RideTemplateGroupDto from(RideTemplateGroup group) {
    return new RideTemplateGroupDto(
        TsidUtils.toString(group.getId()),
        group.getName(),
        group.getTime(),
        group.getAverageSpeed(),
        group.getMaxParticipants(),
        group.getSortOrder());
  }
}
