package fr.pedalons.service.weather;

import fr.pedalons.domain.ride.Ride;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.WeatherStatus;
import fr.pedalons.service.weather.RideWeatherCalculator.CellSeries;
import fr.pedalons.service.weather.RideWeatherCalculator.LegInput;
import fr.pedalons.service.weather.RideWeatherPlans.RidePlan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A ride's weather for its detail page, read from the cache only.
 *
 * <p><b>Never calls the provider and never writes</b>, so the web SSR can prefetch it like any
 * read: a cell nobody fetched yet reads as {@code UNAVAILABLE}, and the planner will have asked for
 * it within five minutes of the ride being published.
 *
 * <p>Its query count does not depend on the number of groups: the track ids of all the ride's
 * routes (one query), the cells (one), their hours with sunrise and sunset (one) — plus, on a cold
 * route-sample cache only, the tracks of each route not cached yet.
 *
 * <p>Takes a {@link Ride} the caller has already been allowed to read: the access check is the
 * caller's ({@code @CheckAccess(RIDE, READ)} on the endpoint). Nothing it returns carries a
 * coordinate.
 */
@ApplicationScoped
public class RideWeatherService {

  @Inject RideWeatherPlans plans;
  @Inject WeatherSeriesLoader seriesLoader;

  public RideWeather forRide(Ride ride) {
    return forRide(ride, Instant.now());
  }

  RideWeather forRide(Ride ride, Instant now) {
    if (isOutOfRange(ride, now)) {
      return RideWeather.of(WeatherStatus.OUT_OF_RANGE);
    }
    RidePlan plan = plans.plan(ride);
    if (plan.departure() == null) {
      return RideWeather.of(WeatherStatus.NO_LOCATION);
    }
    if (RideWeatherCalculator.isBeyondHorizon(ride.getDateTime(), now)) {
      return new RideWeather(
          WeatherStatus.NOT_YET_AVAILABLE,
          RideWeatherCalculator.availableFrom(ride.getDateTime()),
          null,
          DepartureWeather.of(WeatherStatus.NOT_YET_AVAILABLE),
          List.of());
    }

    // Every cell the plan reads, then one query for those cells and one for their hours.
    Set<CellKey> keys = new LinkedHashSet<>();
    CellKey departureCell = plan.departure().cell();
    keys.add(departureCell);
    for (LegInput leg : plan.legs()) {
      if (leg.samples() != null) {
        for (RouteSamples.Sample sample : leg.samples().samples()) {
          keys.add(sample.cell());
        }
      }
    }
    Map<CellKey, CellSeries> cache = seriesLoader.load(keys, plan.earliest(), plan.lastArrival());

    DepartureWeather departure =
        RideWeatherCalculator.departure(
            cache.getOrDefault(departureCell, CellSeries.EMPTY), plan.departureTime(), now);
    List<WeatherLeg> legs = new ArrayList<>(plan.legs().size());
    for (LegInput leg : plan.legs()) {
      legs.add(
          RideWeatherCalculator.leg(leg, key -> cache.getOrDefault(key, CellSeries.EMPTY), now));
    }
    return RideWeatherCalculator.ride(departure, legs);
  }

  /**
   * Finished — it has left, as {@code RideDto.finished} says — or cancelled: no weather block. A
   * draft has none either.
   */
  static boolean isOutOfRange(Ride ride, Instant now) {
    return ride.getStatus() != Status.PUBLISHED || ride.getDateTime().isBefore(now);
  }
}
