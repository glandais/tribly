package fr.pedalons.dto.rides.response;

import fr.pedalons.dto.users.response.PublicUserDto;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * What a ride list row needs to know about a ride's groups and participants, without loading a
 * single {@code RideGroup}, {@code RideParticipation} or {@code User} entity.
 *
 * <p>Built in bulk for a whole page by {@link
 * fr.pedalons.repository.ride.RideSummaryRepository#loadListSummaries}.
 *
 * @param full every group of the ride has a capacity and has reached it. A ride with no groups, or
 *     with at least one uncapped group, is never full.
 * @param maxParticipants the sum of the groups' capacities, or {@code null} when the ride has no
 *     group or at least one uncapped group — the same rides {@code full} can never be true for
 */
public record RideListSummary(
    int groupCount,
    int participantCount,
    boolean full,
    @Nullable Integer maxParticipants,
    List<PublicUserDto> topParticipants) {

  /** A ride with no groups at all. */
  public static final RideListSummary EMPTY = new RideListSummary(0, 0, false, null, List.of());
}
