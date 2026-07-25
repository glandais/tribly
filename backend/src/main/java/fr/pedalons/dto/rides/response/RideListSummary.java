package fr.pedalons.dto.rides.response;

import fr.pedalons.dto.users.response.PublicUserDto;
import java.util.List;

/**
 * The three things a ride list row needs to know about a ride's groups and participants, without
 * loading a single {@code RideGroup}, {@code RideParticipation} or {@code User} entity.
 *
 * <p>Built in bulk for a whole page by {@link
 * fr.pedalons.repository.ride.RideSummaryRepository#loadListSummaries}.
 */
public record RideListSummary(
    int groupCount, int participantCount, List<PublicUserDto> topParticipants) {

  /** A ride with no groups at all. */
  public static final RideListSummary EMPTY = new RideListSummary(0, 0, List.of());
}
