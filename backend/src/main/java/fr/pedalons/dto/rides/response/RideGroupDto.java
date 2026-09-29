package fr.pedalons.dto.rides.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.ride.RideParticipation;
import fr.pedalons.domain.route.Route;
import fr.pedalons.dto.users.response.PublicUserDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.service.asset.ThumbnailLookup.ThemedThumbnail;
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
        Float elevationGain,
    @Nullable
        @Schema(
            description =
                "The member who leads this group, when one is designated. Null means no leader was"
                    + " designated — render nothing rather than falling back on the ride's"
                    + " creator, who is the same person on every group of the ride.")
        PublicUserDto leader,
    @Nullable @Schema(description = "Thumbnail URL (light) of the group route, if it has one")
        String thumbnailLightUrl,
    @Nullable @Schema(description = "Thumbnail URL (dark) of the group route, if it has one")
        String thumbnailDarkUrl,
    @Nullable
        @Schema(
            description =
                "The one thumbnail of the group route to show when the client does not theme its"
                    + " cards: the light variant if there is one, else the dark one. Null when the"
                    + " group has no route or its route has no thumbnail — the ride's own"
                    + " thumbnail is then the one to fall back on.")
        String thumbnailUrl) {

  public static RideGroupDto from(RideGroup group) {
    return from(group, null, null);
  }

  /**
   * @param registeredGroupId the group of this ride the current user joined, or {@code null} — the
   *     caller resolved it once for the whole payload rather than once per group
   * @param routeThumbnail the thumbnail of the group's route, resolved by {@code ThumbnailLookup}
   *     for every group of the ride in one query — never by walking {@code route.getAssets()} here
   */
  public static RideGroupDto from(
      RideGroup group, @Nullable Long registeredGroupId, @Nullable ThemedThumbnail routeThumbnail) {
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
        route != null ? route.getElevationGain() : null,
        group.getLeader() != null ? PublicUserDto.from(group.getLeader()) : null,
        routeThumbnail != null ? routeThumbnail.light() : null,
        routeThumbnail != null ? routeThumbnail.dark() : null,
        routeThumbnail != null ? routeThumbnail.collapsed() : null);
  }
}
