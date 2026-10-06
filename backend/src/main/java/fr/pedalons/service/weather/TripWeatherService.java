package fr.pedalons.service.weather;

import fr.pedalons.domain.trip.Trip;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.WeatherStatus;
import fr.pedalons.service.weather.RideWeatherCalculator.CellSeries;
import fr.pedalons.service.weather.RideWeatherCalculator.LegInput;
import fr.pedalons.service.weather.TripWeatherPlans.StagePlan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A trip's weather, stage by stage, read from the cache only — the trip counterpart of {@link
 * RideWeatherService}, under the same rules: <b>never calls the provider and never writes</b>, takes
 * a {@link Trip} the caller has already been allowed to read, returns no coordinate.
 *
 * <p>Each stage has its own state: a stage already gone is {@code OUT_OF_RANGE}, one beyond the
 * seven-day horizon {@code NOT_YET_AVAILABLE}, one without a route {@code NO_LOCATION}. Its query
 * count does not depend on the number of stages: the track ids (one), the cells (one), their hours
 * over the window of the stages within range (one) — plus, on a cold route-sample cache only, the
 * tracks of each route not cached yet.
 */
@ApplicationScoped
public class TripWeatherService {

  @Inject TripWeatherPlans plans;
  @Inject WeatherSeriesLoader seriesLoader;

  public TripWeather forTrip(Trip trip) {
    return forTrip(trip, Instant.now());
  }

  TripWeather forTrip(Trip trip, Instant now) {
    // A draft or a cancelled trip has no weather block; a finished one falls out below, every
    // stage being gone.
    if (trip.getStatus() != Status.PUBLISHED) {
      return TripWeather.of(WeatherStatus.OUT_OF_RANGE);
    }
    List<StagePlan> stagePlans = plans.plan(trip);

    // The cells of the stages within range, and the window that covers them all.
    Set<CellKey> keys = new LinkedHashSet<>();
    Instant from = null;
    Instant to = null;
    for (StagePlan plan : stagePlans) {
      LegInput leg = plan.leg();
      if (isGone(leg, now)
          || leg.samples() == null
          || RideWeatherCalculator.isBeyondHorizon(leg.start(), now)) {
        continue;
      }
      for (RouteSamples.Sample sample : leg.samples().samples()) {
        keys.add(sample.cell());
      }
      from = from == null || leg.start().isBefore(from) ? leg.start() : from;
      to = to == null || leg.arrival().isAfter(to) ? leg.arrival() : to;
    }
    Map<CellKey, CellSeries> cache = from == null ? Map.of() : seriesLoader.load(keys, from, to);

    List<TripWeather.StageLeg> stages = new ArrayList<>(stagePlans.size());
    List<WeatherLeg> upcoming = new ArrayList<>(stagePlans.size());
    for (StagePlan plan : stagePlans) {
      LegInput leg = plan.leg();
      WeatherLeg weather;
      if (isGone(leg, now)) {
        weather = gone(leg);
      } else {
        weather =
            RideWeatherCalculator.leg(leg, key -> cache.getOrDefault(key, CellSeries.EMPTY), now);
        upcoming.add(weather);
      }
      stages.add(new TripWeather.StageLeg(plan.stageId(), weather));
    }
    return RideWeatherCalculator.trip(stages, upcoming);
  }

  /** A stage that has left: its forecast is no longer shown, as for a ride gone. */
  static boolean isGone(LegInput leg, Instant now) {
    return leg.start().isBefore(now);
  }

  private static WeatherLeg gone(LegInput leg) {
    return new WeatherLeg(
        null,
        WeatherStatus.OUT_OF_RANGE,
        null,
        leg.start(),
        leg.speed().kmh(),
        leg.speed().isDefault(),
        RideWeatherCalculator.round0(leg.distance()),
        leg.arrival(),
        null,
        List.of(),
        List.of(),
        WindExposure.NONE,
        null,
        null);
  }
}
