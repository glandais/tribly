package fr.pedalons.repository.trip;

import fr.pedalons.dto.trips.response.TripListSummary;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * Bulk-loads the stage/participant counts a trip list row needs.
 *
 * <p>Same shape of problem {@link fr.pedalons.repository.ride.RideSummaryRepository} solves for
 * rides: {@code TripDto.from} produced {@code stageCount} and {@code participantCount} by calling
 * {@code stages.size()} and {@code participations.size()}, which loads both collections in full. A
 * page of 30 trips with 3 stages and 5 participants each hydrated ~270 entities to produce 60
 * integers. Batch fetching hid it — the query count stayed flat while the row count did not.
 *
 * <p>Stages and participants are counted in two separate queries rather than one join: joining both
 * collections off {@code Trip} multiplies them together (3 stages x 5 participants = 15 rows per
 * trip), and while {@code count(distinct ...)} would still give the right answer, the database would
 * be building a product it does not need.
 */
@ApplicationScoped
public class TripSummaryRepository {

  @Inject EntityManager entityManager;

  /**
   * Returns a summary per trip id. Trip ids with neither stages nor participants are absent from the
   * map — callers fall back to {@link TripListSummary#EMPTY}.
   */
  public Map<Long, TripListSummary> loadListSummaries(Collection<Long> tripIds) {
    if (tripIds.isEmpty()) {
      return Map.of();
    }
    // Deleted stages are excluded, matching Trip.getStageCount().
    Map<Long, StageAggregate> stageAggregates = loadStageAggregates(tripIds);
    Map<Long, Integer> participantCounts =
        countBy(
            "select p.trip.id, count(p.id) from TripParticipation p "
                + "where p.trip.id in (:tripIds) "
                + "group by p.trip.id",
            tripIds);

    Map<Long, TripListSummary> summaries = new HashMap<>();
    for (Long tripId : tripIds) {
      StageAggregate stages = stageAggregates.get(tripId);
      int participants = participantCounts.getOrDefault(tripId, 0);
      if (stages != null || participants > 0) {
        StageAggregate safe = stages != null ? stages : StageAggregate.EMPTY;
        summaries.put(
            tripId,
            new TripListSummary(
                safe.count(),
                participants,
                safe.totalDistance(),
                safe.totalElevationGain(),
                safe.endDate()));
      }
    }
    return summaries;
  }

  /**
   * Stage count, route totals and end date of every trip on the page, in one query.
   *
   * <p>The three figures a trip card shows besides its title — how far, how much climbing, until
   * when — all fold over the same rows, so they are one {@code group by} rather than three. The join
   * to the route is a left join: a trip whose stages carry no route still has a stage count and an
   * end date, it just has no distance.
   *
   * <p>The trip ids handed in came out of a {@code PedalonsQuery}, so they are already the ones this
   * caller may see; this query narrows that set, it never widens it.
   */
  private Map<Long, StageAggregate> loadStageAggregates(Collection<Long> tripIds) {
    List<Object[]> rows =
        entityManager
            .createQuery(
                "select s.trip.id, count(s.id), sum(r.distance), sum(r.elevationGain),"
                    + " max(s.dateTime) "
                    + "from TripStage s left join s.route r "
                    + "where s.trip.id in (:tripIds) and s.deleted = false "
                    + "group by s.trip.id",
                Object[].class)
            .setParameter("tripIds", tripIds)
            .getResultList();
    Map<Long, StageAggregate> aggregates = new HashMap<>();
    for (Object[] row : rows) {
      aggregates.put(
          (Long) row[0],
          new StageAggregate(
              ((Number) row[1]).intValue(),
              toFloat((Number) row[2]),
              toFloat((Number) row[3]),
              toInstant(row[4])));
    }
    return aggregates;
  }

  private static @Nullable Float toFloat(@Nullable Number value) {
    return value == null ? null : value.floatValue();
  }

  /**
   * {@code max()} over a timestamp comes back as an {@link Instant} on this dialect, but the JDBC
   * type of an aggregate is the driver's business, not ours — so a {@link java.util.Date} is
   * accepted too rather than blowing up at runtime on a dialect change.
   */
  private static @Nullable Instant toInstant(@Nullable Object value) {
    return switch (value) {
      case null -> null;
      case Instant instant -> instant;
      case java.sql.Timestamp timestamp -> timestamp.toInstant();
      case java.util.Date date -> date.toInstant();
      default ->
          throw new IllegalStateException(
              "Unexpected stage date type: " + value.getClass().getName());
    };
  }

  /** One trip's stage figures, before they are merged with its participant count. */
  private record StageAggregate(
      int count,
      @Nullable Float totalDistance,
      @Nullable Float totalElevationGain,
      @Nullable Instant endDate) {

    static final StageAggregate EMPTY = new StageAggregate(0, null, null, null);
  }

  private Map<Long, Integer> countBy(String hql, Collection<Long> tripIds) {
    List<Object[]> rows =
        entityManager
            .createQuery(hql, Object[].class)
            .setParameter("tripIds", tripIds)
            .getResultList();
    Map<Long, Integer> counts = new HashMap<>();
    for (Object[] row : rows) {
      counts.put((Long) row[0], ((Number) row[1]).intValue());
    }
    return counts;
  }
}
