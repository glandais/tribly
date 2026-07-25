package fr.pedalons.dto.publications.response;

import fr.pedalons.dto.rides.response.RideListSummary;
import fr.pedalons.dto.trips.response.TripListSummary;
import java.util.Map;

/**
 * Per-publication aggregates for one page of a publication list, loaded in bulk before any row is
 * mapped.
 *
 * <p>A publication list mixes rides, trips and posts. Rides and trips both need counts over
 * associations ({@code groups}/{@code participations}, {@code stages}/{@code participations}) that a
 * list row never renders; computing them by walking the associations pulls every row of every
 * association into the persistence context. Loading them here, once per page, keeps a list's cost
 * proportional to the number of rows rather than to how popular those rows are.
 *
 * @param rides keyed by ride id; a ride absent from the map has no groups
 * @param trips keyed by trip id; a trip absent from the map has no stages and no participants
 */
public record PublicationListSummaries(
    Map<Long, RideListSummary> rides, Map<Long, TripListSummary> trips) {

  /** No summaries loaded — every publication falls back to its EMPTY summary. */
  public static final PublicationListSummaries EMPTY =
      new PublicationListSummaries(Map.of(), Map.of());

  public RideListSummary ride(Long rideId) {
    return rides.getOrDefault(rideId, RideListSummary.EMPTY);
  }

  public TripListSummary trip(Long tripId) {
    return trips.getOrDefault(tripId, TripListSummary.EMPTY);
  }
}
