package fr.pedalons.dto.trips.response;

/**
 * The two numbers a trip list row needs about its stages and participants, without loading a single
 * {@code TripStage} or {@code TripParticipation} entity.
 *
 * <p>Built in bulk for a whole page by {@link
 * fr.pedalons.repository.trip.TripSummaryRepository#loadListSummaries}. A list row shows no stage or
 * participant details, only these counts — {@code TripDto.from} used to load both collections in
 * full to produce them.
 */
public record TripListSummary(int stageCount, int participantCount) {

  /** A trip with no stages and no participants. */
  public static final TripListSummary EMPTY = new TripListSummary(0, 0);
}
