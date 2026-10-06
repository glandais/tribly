package fr.pedalons.service.weather;

import fr.pedalons.domain.ride.Ride;
import fr.pedalons.repository.ride.RideRepository;
import fr.pedalons.repository.weather.WeatherCellRepository;
import fr.pedalons.service.weather.RideWeatherCalculator.LegInput;
import fr.pedalons.service.weather.RideWeatherPlans.RidePlan;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;

/**
 * Decides which cells the cache needs, never calling the provider.
 *
 * <p>Every five minutes, the published rides leaving between now and seven days ahead — the window
 * the read side shows ({@code RideWeatherService#isOutOfRange}: a ride already gone is {@code
 * OUT_OF_RANGE}), so no cell is fetched for a forecast nobody can see:
 * their departure cell and the cell of each forecast point of each group, with the passage nearest
 * to now. Each cell is upserted with that demand ({@code WeatherCellRepository#upsertDemand}); the
 * refresh interval follows from it ({@link WeatherRefreshPolicy}). Deterministic in the rides and
 * the clock, so a tick replayed — or run by both backends of a rolling deploy — writes the same
 * rows.
 *
 * <p>Across all domains: the cache is global (see {@code fr.pedalons.domain.weather}).
 */
@ApplicationScoped
public class WeatherPlanner {

  private static final Logger LOG = Logger.getLogger(WeatherPlanner.class);

  @Inject RideRepository rideRepository;
  @Inject RideWeatherPlans plans;
  @Inject WeatherCellRepository cellRepository;
  @Inject WeatherFetchWorker fetchWorker;

  @Scheduled(every = "5m", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
  void tick() {
    if (!fetchWorker.isEnabled()) {
      return;
    }
    try {
      int cells = plan(Instant.now());
      LOG.debugf("Weather planner: %d cell(s) in demand", cells);
    } catch (Exception e) {
      LOG.error("Weather planning failed", e);
    }
  }

  /** One planning pass. Public for the tests, which run with the scheduler off. */
  @Transactional
  public int plan(Instant now) {
    List<Ride> rides = rideRepository.findForWeather(now, now.plus(RideWeatherCalculator.HORIZON));
    Map<CellKey, Instant> demand = demand(plans.plans(rides), now);
    for (Map.Entry<CellKey, Instant> entry : demand.entrySet()) {
      CellKey key = entry.getKey();
      Instant need = entry.getValue();
      cellRepository.upsertDemand(
          key.latIdx(),
          key.lonIdx(),
          key.eleBand(),
          key.centerLat(),
          key.centerLon(),
          need,
          WeatherRefreshPolicy.ttl(need, now),
          now);
    }
    return demand.size();
  }

  /**
   * Cell → nearest passage. A passage already behind us (a group timed before the ride's own
   * departure) counts as {@code now}: its cells want the shortest interval.
   */
  static Map<CellKey, Instant> demand(List<RidePlan> plans, Instant now) {
    Map<CellKey, Instant> demand = new HashMap<>();
    for (RidePlan plan : plans) {
      if (plan.departure() != null) {
        need(demand, plan.departure().cell(), plan.departureTime(), now);
      }
      for (LegInput leg : plan.legs()) {
        if (leg.samples() == null) {
          continue;
        }
        for (RouteSamples.Sample sample : leg.samples().samples()) {
          Instant passage =
              RideWeatherCalculator.passage(leg.start(), sample.distance(), leg.speed().kmh());
          need(demand, sample.cell(), passage, now);
        }
      }
    }
    return demand;
  }

  private static void need(
      Map<CellKey, Instant> demand, CellKey cell, Instant passage, Instant now) {
    Instant need = passage.isBefore(now) ? now : passage;
    demand.merge(cell, need, (a, b) -> a.isBefore(b) ? a : b);
  }
}
