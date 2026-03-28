package fr.pedalons.dto.ridetemplates.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ridetemplate.RideTemplateGroup;
import fr.pedalons.dto.validation.ValidateSchema;
import java.time.LocalTime;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride template group information")
@ValidateSchema
public record RideTemplateGroupDto(
    @Schema(description = "Group ID (TSID)", required = true) String id,
    @Schema(description = "Group name", required = true) String name,
    @Nullable LocalTime time,
    @Nullable @Schema(description = "Average speed in km/h") Float averageSpeed,
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
