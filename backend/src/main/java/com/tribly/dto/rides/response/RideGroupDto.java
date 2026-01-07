package com.tribly.dto.rides.response;

import com.tribly.common.TsidUtils;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.ride.RideParticipation;
import com.tribly.dto.users.response.PublicUserDto;
import com.tribly.dto.validation.ValidateSchema;
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
    @Nullable @Schema(description = "Average speed in km/h") Integer averageSpeed,
    @Nullable @Schema(description = "Maximum participants") Integer maxParticipants,
    @Schema(description = "Current number of participants", required = true) int countParticipants,
    @Schema(description = "Participants, empty if not access", required = true)
        List<PublicUserDto> participants,
    @Schema(description = "Sort order", required = true) int sortOrder) {
  public static RideGroupDto from(RideGroup group) {
    List<PublicUserDto> participantDtos =
        group.getParticipations().stream()
            .filter(p -> !p.isDeleted())
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
