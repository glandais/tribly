package fr.pedalons.dto.rides.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
import java.time.LocalTime;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/**
 * What a ride card shows of one group without opening the ride: its pace and how full it is.
 *
 * <p>Built from scalars for a whole page of rides in one query ({@code RideSummaryRepository}) — no
 * {@code RideGroup}, participation nor user entity is loaded. Deliberately carries no leader and no
 * participant list: those belong to the detail ({@link RideGroupDto}).
 */
@Schema(
    description =
        "One group of a ride, as a list row shows it: name, pace and fill. Same figures as the"
            + " matching entry of the detail's groups, without the participants nor the leader.")
@ValidateSchema
public record RideGroupSummaryDto(
    @Schema(description = "Group ID (TSID)", required = true) String id,
    @Schema(description = "Group name", required = true) String name,
    @Nullable @Schema(description = "Start time of the group, when it differs from the ride's")
        LocalTime time,
    @Schema(
            description =
                "When the group leaves, as RideGroupDto.startAt. Replaces time, kept for the"
                    + " clients that still read it.",
            required = true)
        Instant startAt,
    @Nullable @Schema(description = "Average speed in km/h") Float averageSpeed,
    @Schema(description = "Current number of participants", required = true) int countParticipants,
    @Nullable @Schema(description = "Maximum participants, null when the group is uncapped")
        Integer maxParticipants,
    @Schema(
            description =
                "Whether the group has reached maxParticipants. False when maxParticipants is not"
                    + " set.",
            required = true)
        boolean full,
    @Nullable @Schema(description = "Slug of the group route, if it has one") String routeSlug,
    @Nullable @Schema(description = "Distance in meters of the group route, if it has one")
        Float distance,
    @Nullable
        @Schema(description = "Total elevation gain in meters of the group route, if it has one")
        Float elevationGain,
    @Schema(description = "Sort order", required = true) int sortOrder) {

  /** The summary of a group the detail already mapped in full. */
  public static RideGroupSummaryDto from(RideGroupDto group) {
    return new RideGroupSummaryDto(
        group.id(),
        group.name(),
        group.time(),
        group.startAt(),
        group.averageSpeed(),
        group.countParticipants(),
        group.maxParticipants(),
        group.full(),
        group.routeSlug(),
        group.distance(),
        group.elevationGain(),
        group.sortOrder());
  }
}
