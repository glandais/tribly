package fr.pedalons.dto.rides.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.ride.RideParticipation;
import fr.pedalons.dto.users.response.PublicUserDto;
import fr.pedalons.dto.validation.ValidateSchema;
import java.time.LocalTime;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride group information")
@ValidateSchema
public record RideGroupDto(
    @Schema(description = "Group ID (TSID)", required = true) String id,
    @Schema(description = "Group name", required = true) String name,
    @Nullable LocalTime time,
    @Nullable @Schema(description = "Route slug") String routeSlug,
    @Nullable @Schema(description = "Average speed in km/h") Float averageSpeed,
    @Nullable @Schema(description = "Maximum participants") Integer maxParticipants,
    @Schema(description = "Current number of participants", required = true) int countParticipants,
    @Schema(description = "Participants, empty if not access", required = true)
        List<PublicUserDto> participants,
    @Schema(description = "Sort order", required = true) int sortOrder) {
  public static RideGroupDto from(RideGroup group) {
    List<PublicUserDto> participantDtos =
        group.getParticipations().stream()
            .map(RideParticipation::getUser)
            .map(PublicUserDto::from)
            .toList();
    return new RideGroupDto(
        TsidUtils.toString(group.getId()),
        group.getName(),
        group.getTime(),
        group.getRoute() != null ? group.getRoute().getSlug() : null,
        group.getAverageSpeed(),
        group.getMaxParticipants(),
        group.getCurrentParticipants(),
        participantDtos,
        group.getSortOrder());
  }
}
