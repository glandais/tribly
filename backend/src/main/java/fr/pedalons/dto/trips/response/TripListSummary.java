package fr.pedalons.dto.trips.response;

import java.time.Instant;
import org.jspecify.annotations.Nullable;

/**
 * What a trip list row needs to know about its stages and participants, without loading a single
 * {@code TripStage} or {@code TripParticipation} entity.
 *
 * <p>Built in bulk for a whole page by {@link
 * fr.pedalons.repository.trip.TripSummaryRepository#loadListSummaries}. A list row shows no stage or
 * participant details — only these counts and totals — and {@code TripDto.from} used to load both
 * collections in full to produce them.
 *
 * @param stageCount stages that are not deleted
 * @param participantCount registered participants
 * @param totalDistance metres over every stage that has a route; null when no stage has one
 * @param totalElevationGain metres climbed over every stage that has a route; null likewise
 * @param endDate the date of the last stage; null for a trip with no stage, in which case the trip
 *     lasts a day and its {@code dateTime} is both ends
 */
public record TripListSummary(
    int stageCount,
    int participantCount,
    @Nullable Float totalDistance,
    @Nullable Float totalElevationGain,
    @Nullable Instant endDate) {

  /** A trip with no stages and no participants. */
  public static final TripListSummary EMPTY = new TripListSummary(0, 0, null, null, null);
}
