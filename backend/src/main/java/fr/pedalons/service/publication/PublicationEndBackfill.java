package fr.pedalons.service.publication;

import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripStage;
import io.quarkus.narayana.jta.QuarkusTransaction;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.jboss.logging.Logger;

/**
 * Fills {@code end_date_time} for the rides and trips that have none — every row written before
 * the column existed, and those an older backend writes during a start-first deploy
 * (docs/LEDGER_*.md API-85).
 *
 * <p>Idempotent and resumable: it only ever looks at rows whose end is null, by batches of {@value
 * #BATCH_SIZE} in id order, each batch in its own transaction. Each value is written by a
 * conditional update ({@code where end_date_time is null}) that leaves the version alone: the
 * previous release, running beside this one for a minute, never sees an optimistic-lock failure
 * because of it, and a row the application has filled in the meantime is not overwritten. Until a
 * row is filled the list queries read {@code coalesce(end_date_time, date_time + 3 h)}, so nothing
 * is misplaced while this runs.
 */
@ApplicationScoped
public class PublicationEndBackfill {

  private static final Logger LOG = Logger.getLogger(PublicationEndBackfill.class);

  static final int BATCH_SIZE = 200;

  @Inject EntityManager entityManager;

  @Inject PublicationEndCalculator calculator;

  void onStart(@Observes StartupEvent ev) {
    try {
      int filled = run();
      if (filled > 0) {
        LOG.infov("Filled the end of {0} ride(s) and trip(s)", filled);
      }
    } catch (Exception e) {
      // Never block the startup on it: the readers fall back on the default duration.
      LOG.error("Backfill of publication ends failed", e);
    }
  }

  /**
   * Runs the backfill to the end.
   *
   * @return how many rides and trips got an end
   */
  public int run() {
    long lastId = Long.MIN_VALUE;
    int filled = 0;
    while (true) {
      long after = lastId;
      Batch batch = QuarkusTransaction.requiringNew().call(() -> batch(after));
      filled += batch.filled();
      if (batch.lastId() == null) {
        return filled;
      }
      lastId = batch.lastId();
    }
  }

  private record Batch(Long lastId, int filled) {}

  private Batch batch(long afterId) {
    List<TeamEntity> rows =
        entityManager
            .createQuery(
                "select te from TeamEntity te where TYPE(te) in (Ride, Trip)"
                    + " and te.endDateTime is null and te.id > :afterId order by te.id",
                TeamEntity.class)
            .setParameter("afterId", afterId)
            .setMaxResults(BATCH_SIZE)
            .getResultList();
    if (rows.isEmpty()) {
      return new Batch(null, 0);
    }
    int filled = 0;
    for (TeamEntity row : rows) {
      switch (row) {
        case Ride ride -> filled += fill(ride.getId(), calculator.rideEnd(ride));
        case Trip trip -> {
          PublicationEndCalculator.TripEnds ends = calculator.tripEnds(trip);
          for (Map.Entry<TripStage, Instant> stage : ends.stages().entrySet()) {
            fill(stage.getKey().getId(), stage.getValue());
          }
          filled += fill(trip.getId(), ends.end());
        }
        default -> {}
      }
    }
    Long lastId = rows.getLast().getId();
    entityManager.clear();
    return new Batch(lastId, filled);
  }

  private int fill(Long id, Instant end) {
    return entityManager
        .createQuery(
            "update TeamEntity te set te.endDateTime = :end where te.id = :id"
                + " and te.endDateTime is null")
        .setParameter("end", end)
        .setParameter("id", id)
        .executeUpdate();
  }
}
