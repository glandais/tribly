package fr.pedalons.service.weather;

import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * The weather summaries of a page of rides, as {@code CommentCounts} is for comments: built once per
 * page by {@link RideWeatherLookup#forRides}, read per row.
 *
 * <p>A ride is <b>absent</b> unless its summary says something to show — {@code OK}, {@code STALE}
 * or {@code NOT_YET_AVAILABLE}. Absent makes {@code RideDto.weather} vanish from the JSON (Jackson
 * {@code NON_NULL}): a finished or cancelled ride, one without a place, or one whose forecast is
 * not in cache yet shows no weather line at all.
 *
 * <p>{@link TripWeatherLookup#forTrips} builds the same thing for the trips of a page, keyed by trip
 * id: the summary of each trip's next leg (docs/LEDGER_*.md API-82), with that leg's zone — the
 * zone its times read in, which is the next stage's and not the trip's (docs/LEDGER_*.md API-60).
 *
 * @param zoneById the IANA zone of each summary's leg, filled for trips only
 */
public record RideWeatherSummaries(
    Map<Long, RideWeatherSummary> byRideId, Map<Long, String> zoneById) {

  public static final RideWeatherSummaries NONE = new RideWeatherSummaries(Map.of());

  public RideWeatherSummaries(Map<Long, RideWeatherSummary> byRideId) {
    this(byRideId, Map.of());
  }

  /** This ride's summary, or null when there is nothing to show. */
  public @Nullable RideWeatherSummary forRide(@Nullable Long rideId) {
    return rideId == null ? null : byRideId.get(rideId);
  }

  /** This trip's summary, from {@link TripWeatherLookup}, or null when there is nothing to show. */
  public @Nullable RideWeatherSummary forTrip(@Nullable Long tripId) {
    return forRide(tripId);
  }

  /** The zone of this trip's summary — its next leg's — or null when there is no summary. */
  public @Nullable String zoneForTrip(@Nullable Long tripId) {
    return tripId == null ? null : zoneById.get(tripId);
  }
}
