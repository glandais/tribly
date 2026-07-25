package fr.pedalons.repository.ride;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.dto.rides.response.RideListSummary;
import fr.pedalons.dto.users.response.PublicUserDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Bulk-loads the group/participant summary a ride list row needs.
 *
 * <p>Rendering a page of rides used to walk {@code ride.getGroups() -> group.getParticipations() ->
 * participation.getUser()} for every row, purely to produce a participant count and a five-avatar
 * preview. Batch fetching kept the <em>query</em> count flat, but the <em>row</em> count was not: a
 * page of 30 rides with 50 participants each hydrated ~1500 {@code RideParticipation} entities plus
 * their users into the persistence context, only to throw away all but 150 of them.
 *
 * <p>These two queries return scalars instead. Nothing enters the persistence context, so the cost
 * of a list page stops tracking the popularity of the rides on it.
 */
@ApplicationScoped
public class RideSummaryRepository {

  /** How many participant avatars a list row previews. */
  private static final int TOP_PARTICIPANTS = 5;

  @Inject EntityManager entityManager;

  /**
   * Returns a summary per ride id. Ride ids with no groups are absent from the map — callers fall
   * back to {@link RideListSummary#EMPTY}.
   */
  public Map<Long, RideListSummary> loadListSummaries(Collection<Long> rideIds) {
    if (rideIds.isEmpty()) {
      return Map.of();
    }
    Map<Long, List<PublicUserDto>> topParticipants = loadTopParticipants(rideIds);

    Map<Long, RideListSummary> summaries = new HashMap<>();
    for (Object[] row : loadCounts(rideIds)) {
      Long rideId = (Long) row[0];
      summaries.put(
          rideId,
          new RideListSummary(
              ((Number) row[1]).intValue(),
              ((Number) row[2]).intValue(),
              topParticipants.getOrDefault(rideId, List.of())));
    }
    return summaries;
  }

  /**
   * {@code (rideId, groupCount, participantCount)}. The left join keeps groups with no participants,
   * and {@code count(distinct g.id)} counts each group once even when it has many participations.
   */
  private List<Object[]> loadCounts(Collection<Long> rideIds) {
    return entityManager
        .createQuery(
            "select g.ride.id, count(distinct g.id), count(p.id) "
                + "from RideGroup g left join g.participations p "
                + "where g.ride.id in (:rideIds) "
                + "group by g.ride.id",
            Object[].class)
        .setParameter("rideIds", rideIds)
        .getResultList();
  }

  /**
   * The first {@link #TOP_PARTICIPANTS} distinct users to register for each ride, ordered by
   * registration time across all of the ride's groups — the same rule the entity walk applied.
   *
   * <p>Selected as scalars, so a ride with a thousand participants costs a thousand cheap rows
   * rather than a thousand managed entities plus their users.
   */
  private Map<Long, List<PublicUserDto>> loadTopParticipants(Collection<Long> rideIds) {
    List<Object[]> rows =
        entityManager
            .createQuery(
                "select g.ride.id, u.id, u.displayName, u.avatarUrl "
                    + "from RideParticipation p join p.rideGroup g join p.user u "
                    + "where g.ride.id in (:rideIds) "
                    + "order by p.registeredAt",
                Object[].class)
            .setParameter("rideIds", rideIds)
            .getResultList();

    Map<Long, List<PublicUserDto>> byRide = new LinkedHashMap<>();
    Map<Long, Set<Long>> seenUserIds = new HashMap<>();
    for (Object[] row : rows) {
      Long rideId = (Long) row[0];
      List<PublicUserDto> top = byRide.computeIfAbsent(rideId, k -> new ArrayList<>());
      if (top.size() >= TOP_PARTICIPANTS) {
        continue;
      }
      Long userId = (Long) row[1];
      // A user registered in two groups of the same ride still appears once.
      if (!seenUserIds.computeIfAbsent(rideId, k -> new HashSet<>()).add(userId)) {
        continue;
      }
      top.add(new PublicUserDto(TsidUtils.toString(userId), (String) row[2], (String) row[3]));
    }
    return byRide;
  }
}
