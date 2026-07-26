package fr.pedalons.dto.rides.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.ride.RideParticipation;
import fr.pedalons.domain.route.Route;
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
    @Schema(description = "Sort order", required = true) int sortOrder,
    @Schema(
            description =
                "Whether the current user is registered in THIS group. False if anonymous.",
            required = true)
        boolean registered,
    @Schema(
            description =
                "Whether the group has reached maxParticipants. False when maxParticipants is not"
                    + " set.",
            required = true)
        boolean full,
    @Nullable @Schema(description = "Distance in meters of the group route, if it has one")
        Float distance,
    @Nullable
        @Schema(description = "Total elevation gain in meters of the group route, if it has one")
        Float elevationGain) {

  public static RideGroupDto from(RideGroup group) {
    return from(group, null);
  }

  /**
   * @param registeredGroupId the group of this ride the current user joined, or {@code null} — the
   *     caller resolved it once for the whole payload rather than once per group
   */
  public static RideGroupDto from(RideGroup group, @Nullable Long registeredGroupId) {
    List<PublicUserDto> participantDtos =
        group.getParticipations().stream()
            .map(RideParticipation::getUser)
            .map(PublicUserDto::from)
            .toList();
    // RideGroup.route is a to-one, already resolved (and batched across the ride's groups) by the
    // routeSlug read just below: the metrics cost no extra query.
    Route route = group.getRoute();
    return new RideGroupDto(
        TsidUtils.toString(group.getId()),
        group.getName(),
        group.getTime(),
        route != null ? route.getSlug() : null,
        group.getAverageSpeed(),
        group.getMaxParticipants(),
        group.getCurrentParticipants(),
        participantDtos,
        group.getSortOrder(),
        registeredGroupId != null && registeredGroupId.equals(group.getId()),
        !group.hasCapacity(),
        route != null ? route.getDistance() : null,
        route != null ? route.getElevationGain() : null);
  }
}
