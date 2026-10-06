package fr.pedalons.service.timezone;

import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripStage;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.LongFunction;
import org.jboss.logging.Logger;

/**
 * Fills {@code team_entities.timezone} and {@code ride_groups.start_at} where they are null: the
 * rows the previous release writes during a start-first deploy, and the groups V64 left to it: those
 * on a ride's DST-transition day, where PostgreSQL resolves an ambiguous wall time to the later
 * offset and {@code legStart} to the earlier one. V64 filled every other older row in SQL.
 * docs/LEDGER_*.md API-60, plan §8 step 4.
 *
 * <p>The shape of {@code PublicationEndBackfill}: idempotent and resumable, by batches of {@value
 * #BATCH_SIZE} in id order, each in its own transaction, each value written by a conditional update
 * ({@code where … is null}) that leaves the version alone — the previous release never sees an
 * optimistic-lock failure because of it, and a row the application filled meanwhile is kept.
 *
 * <p>A ride, a trip or a stage gets the zone its chain resolves, at constant instant (the instant
 * is the only truth an old row has); everything else the team's. The zones first, so that the
 * groups' starts are read in their ride's. A moved {@code start_at} changes no end in this version:
 * {@code PublicationEndCalculator} still reads {@code legStart} until lot 4.
 *
 * <p>The residual risk is the one docs/LEDGER_*.md API-89 accepted for the end: a row
 * <em>modified</em> by the previous release during the switch keeps a stale value until its next
 * save.
 */
@ApplicationScoped
public class EventTimezoneBackfill {

  private static final Logger LOG = Logger.getLogger(EventTimezoneBackfill.class);

  static final int BATCH_SIZE = 200;

  @Inject EntityManager entityManager;

  @Inject EventTimezoneResolver resolver;

  void onStart(@Observes StartupEvent ev) {
    try {
      int zones = runZones();
      int starts = runGroupStarts();
      if (zones > 0 || starts > 0) {
        LOG.infov("Filled {0} entity zone(s) and {1} group start(s)", zones, starts);
      }
    } catch (Exception e) {
      // Never block the startup on it: readers fall back on the team's zone and on group times.
      LOG.error("Backfill of event timezones failed", e);
    }
  }

  /**
   * Fills the zone of every team entity without one.
   *
   * @return how many rows got a zone
   */
  public int runZones() {
    return loop(this::zoneBatch);
  }

  /**
   * Fills the start of every ride group without one.
   *
   * @return how many groups got a start
   */
  public int runGroupStarts() {
    return loop(this::groupStartBatch);
  }

  private record Batch(Long lastId, int filled) {}

  private static int loop(LongFunction<Batch> batch) {
    long lastId = Long.MIN_VALUE;
    int filled = 0;
    while (true) {
      long after = lastId;
      Batch result = QuarkusTransaction.requiringNew().call(() -> batch.apply(after));
      filled += result.filled();
      if (result.lastId() == null) {
        return filled;
      }
      lastId = result.lastId();
    }
  }

  private Batch zoneBatch(long afterId) {
    List<TeamEntity> rows =
        entityManager
            .createQuery(
                "select te from TeamEntity te where te.timezone is null and te.id > :afterId"
                    + " order by te.id",
                TeamEntity.class)
            .setParameter("afterId", afterId)
            .setMaxResults(BATCH_SIZE)
            .getResultList();
    if (rows.isEmpty()) {
      return new Batch(null, 0);
    }
    int filled = 0;
    for (TeamEntity row : rows) {
      filled += fillZone(row.getId(), zoneOf(row));
    }
    Long lastId = rows.getLast().getId();
    entityManager.clear();
    return new Batch(lastId, filled);
  }

  private ZoneId zoneOf(TeamEntity row) {
    ZoneId teamZone = EventTimezoneResolver.teamZone(row.getTeam());
    return switch (row) {
      case Ride ride -> resolver.ride(ride);
      case Trip trip -> {
        Map<TripStage, Optional<ZoneId>> stages = resolver.locateStages(trip);
        yield resolver.locateTrip(List.copyOf(stages.values()), trip.getRoute()).orElse(teamZone);
      }
      case TripStage stage -> {
        Optional<ZoneId> zone = resolver.locateStages(stage.getTrip()).get(stage);
        // A deleted stage is out of the chain: its own points, else the team's.
        yield zone != null
            ? zone.orElse(teamZone)
            : resolver.ride(stage.getTeam(), stage.getStartPlace(), stage.getRoute());
      }
      default -> teamZone;
    };
  }

  private int fillZone(Long id, ZoneId zone) {
    return entityManager
        .createQuery(
            "update TeamEntity te set te.timezone = :zone where te.id = :id"
                + " and te.timezone is null")
        .setParameter("zone", zone.getId())
        .setParameter("id", id)
        .executeUpdate();
  }

  private Batch groupStartBatch(long afterId) {
    List<RideGroup> rows =
        entityManager
            .createQuery(
                "select g from RideGroup g join fetch g.ride where g.startAt is null"
                    + " and g.id > :afterId order by g.id",
                RideGroup.class)
            .setParameter("afterId", afterId)
            .setMaxResults(BATCH_SIZE)
            .getResultList();
    if (rows.isEmpty()) {
      return new Batch(null, 0);
    }
    int filled = 0;
    for (RideGroup group : rows) {
      Ride ride = group.getRide();
      Instant start = EventTimezoneResolver.groupStart(ride.getDateTime(), group, ride.zone());
      filled +=
          entityManager
              .createQuery(
                  "update RideGroup g set g.startAt = :start where g.id = :id"
                      + " and g.startAt is null")
              .setParameter("start", start)
              .setParameter("id", group.getId())
              .executeUpdate();
    }
    Long lastId = rows.getLast().getId();
    entityManager.clear();
    return new Batch(lastId, filled);
  }
}
