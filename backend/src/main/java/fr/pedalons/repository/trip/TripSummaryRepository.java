package fr.pedalons.repository.trip;

import fr.pedalons.dto.trips.response.TripListSummary;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    Map<Long, Integer> stageCounts =
        countBy(
            "select s.trip.id, count(s.id) from TripStage s "
                + "where s.trip.id in (:tripIds) and s.deleted = false "
                + "group by s.trip.id",
            tripIds);
    Map<Long, Integer> participantCounts =
        countBy(
            "select p.trip.id, count(p.id) from TripParticipation p "
                + "where p.trip.id in (:tripIds) "
                + "group by p.trip.id",
            tripIds);

    Map<Long, TripListSummary> summaries = new HashMap<>();
    for (Long tripId : tripIds) {
      int stages = stageCounts.getOrDefault(tripId, 0);
      int participants = participantCounts.getOrDefault(tripId, 0);
      if (stages > 0 || participants > 0) {
        summaries.put(tripId, new TripListSummary(stages, participants));
      }
    }
    return summaries;
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
