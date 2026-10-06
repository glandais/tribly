package fr.pedalons.service.weather;

import fr.pedalons.domain.trip.Trip;
import fr.pedalons.dto.trips.response.TripListSummary;
import fr.pedalons.enums.Status;
import fr.pedalons.repository.weather.WeatherHourRow;
import fr.pedalons.repository.weather.WeatherHourlyRepository;
import fr.pedalons.repository.weather.WeatherHourlyRepository.TripLegHour;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * The weather line of a page of trip cards, in bulk — what {@link RideWeatherLookup} is for rides
 * (docs/LEDGER_*.md API-82). The line is that of the trip's <b>next leg</b>: the first live stage
 * still to leave, else the trip itself when it has no stage.
 *
 * <p><b>It costs a page, not a row</b>: no query at all when no trip of the page has a leg left to
 * leave (draft, cancelled, last stage gone — told by the page's {@link TripListSummary}, already
 * loaded), <b>one</b> otherwise, whatever the page size. The stages are never loaded: the next leg,
 * its departure cell and its window are worked out in SQL ({@code
 * WeatherHourlyRepository#findTripNextLegHours}). Reads the cache only, like the detail.
 *
 * <p>The departure cell is the one the detail reads for the stage's first checkpoint (first track
 * point, with its elevation band), so the card and the stage's own line agree on the start. The
 * extremes are over that one cell, as a ride's card: the rain alert carries no {@code distance}.
 *
 * <p>Takes trips the caller may already read; the result is about those trips only.
 */
@ApplicationScoped
public class TripWeatherLookup {

  @Inject WeatherHourlyRepository hourlyRepository;

  /**
   * @param summaries the list summary of each trip of the page, for its stage count and the date
   *     of its last stage
   */
  public RideWeatherSummaries forTrips(
      Collection<Trip> trips, Function<Long, TripListSummary> summaries) {
    return forTrips(trips, summaries, Instant.now());
  }

  RideWeatherSummaries forTrips(
      Collection<Trip> trips, Function<Long, TripListSummary> summaries, Instant now) {
    List<Long> candidates = new ArrayList<>();
    for (Trip trip : trips) {
      if (trip.getStatus() != Status.PUBLISHED) {
        continue;
      }
      TripListSummary summary = summaries.apply(trip.getId());
      Instant lastDeparture = summary.stageCount() > 0 ? summary.endDate() : trip.getDateTime();
      if (lastDeparture == null || lastDeparture.isBefore(now)) {
        continue;
      }
      candidates.add(trip.getId());
    }
    if (candidates.isEmpty()) {
      return RideWeatherSummaries.NONE;
    }
    Map<Long, List<TripLegHour>> byTrip = new LinkedHashMap<>();
    for (TripLegHour hour :
        hourlyRepository.findTripNextLegHours(
            candidates,
            now,
            now.plus(RideWeatherCalculator.HORIZON),
            CellKey.SQL_LAT_IDX,
            CellKey.SQL_LON_IDX,
            CellKey.ELEVATION_BAND,
            RideWeatherCalculator.DEFAULT_SPEED_KMH,
            RideWeatherLookup.SLACK)) {
      byTrip.computeIfAbsent(hour.tripId(), k -> new ArrayList<>()).add(hour);
    }
    Map<Long, RideWeatherSummary> result = new HashMap<>();
    for (Map.Entry<Long, List<TripLegHour>> entry : byTrip.entrySet()) {
      TripLegHour first = entry.getValue().getFirst();
      RideWeatherSummary summary;
      if (RideWeatherCalculator.isBeyondHorizon(first.departure(), now)) {
        summary =
            RideWeatherSummary.notYetAvailable(
                RideWeatherCalculator.availableFrom(first.departure()));
      } else {
        List<WeatherHourRow> hours =
            entry.getValue().stream().map(TripLegHour::hour).filter(Objects::nonNull).toList();
        summary =
            hours.isEmpty()
                ? null
                : RideWeatherCalculator.summary(
                    first.departure(), first.arrival(), first.fetchedAt(), hours, now);
      }
      if (summary != null) {
        result.put(entry.getKey(), summary);
      }
    }
    return result.isEmpty() ? RideWeatherSummaries.NONE : new RideWeatherSummaries(result);
  }
}
