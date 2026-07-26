package fr.pedalons.repository.team;

import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.repository.common.TeamEntityQueryBasic;
import fr.pedalons.repository.common.TeamEntityQueryInterface;
import fr.pedalons.repository.common.TeamEntityRepository;
import fr.pedalons.repository.query.PedalonsQuery;
import fr.pedalons.repository.ride.RideRepository;
import fr.pedalons.repository.route.RouteQuery;
import fr.pedalons.repository.route.RouteRepository;
import fr.pedalons.service.team.response.TeamStats;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * Bulk-loads the counters a team card shows — upcoming rides, routes.
 *
 * <p>The clickable statistics row of the team screen needs three numbers per team. Members already
 * come free with the team listing itself (a correlated sub-select in {@code TeamRepository.find}),
 * so this covers the other two: <b>two queries for a whole page of teams</b>, not two per team. A
 * directory of thirty teams costs the same as one team.
 *
 * <p>Both counts go through {@link TeamEntityRepository#getPedalonsQuery} with an aggregate {@code
 * QueryShape}, so they inherit — unchanged and un-duplicated — the domain filter, the visibility
 * rules, the enabled-module filters and the deleted filter. A counter that announced rides the
 * caller cannot open would be worse than no counter, and hand-writing the HQL is precisely how that
 * happens.
 */
@ApplicationScoped
public class TeamStatsRepository {

  @Inject EntityManager entityManager;

  @Inject RideRepository rideRepository;

  @Inject RouteRepository routeRepository;

  /**
   * Counters per team id, for every team of the page at once.
   *
   * <p>Teams with nothing to count are absent from the map; callers fall back to {@link
   * TeamStats#EMPTY}.
   */
  public Map<Long, TeamStats> load(
      Collection<Long> teamIds,
      Long domainId,
      @Nullable Long userId,
      @Nullable Long pinnedTeamId,
      boolean platformAdmin) {
    if (teamIds.isEmpty()) {
      return Map.of();
    }
    Set<Long> ids = Set.copyOf(teamIds);

    Map<Long, Long> upcomingRides =
        countByTeam(
            rideRepository,
            TeamEntityQueryBasic.builder()
                .domainId(domainId)
                .userId(userId)
                .pinnedTeamId(pinnedTeamId)
                .teamIds(ids)
                // "Upcoming" is a window, not a status: the visibility rules already decide which
                // rides exist for this caller.
                .from(Instant.now())
                .platformAdmin(platformAdmin)
                .build());

    Map<Long, Long> routes =
        countByTeam(
            routeRepository,
            RouteQuery.builder()
                .domainId(domainId)
                .userId(userId)
                .pinnedTeamId(pinnedTeamId)
                .teamIds(ids)
                .platformAdmin(platformAdmin)
                .build());

    Map<Long, TeamStats> stats = new HashMap<>();
    for (Long teamId : ids) {
      long rides = upcomingRides.getOrDefault(teamId, 0L);
      long routeCount = routes.getOrDefault(teamId, 0L);
      if (rides != 0 || routeCount != 0) {
        stats.put(teamId, new TeamStats(rides, routeCount));
      }
    }
    return stats;
  }

  /** One row per team that has at least one match: {@code (teamId, count)}. */
  private <T extends TeamEntity, Q extends TeamEntityQueryInterface> Map<Long, Long> countByTeam(
      TeamEntityRepository<T, Q> repository, Q query) {
    PedalonsQuery pedalonsQuery =
        repository.getPedalonsQuery(
            query,
            true,
            new TeamEntityRepository.QueryShape(
                "te.team.id, count(te.id)",
                repository.getEntityType().getTypeName() + " te",
                false));
    // andSpecific may have installed an ordering; an aggregate must not carry one on a column it
    // does not select.
    pedalonsQuery.noOrder().groupBy("te.team.id");

    TypedQuery<Object[]> typedQuery =
        entityManager.createQuery(pedalonsQuery.getStringQuery(), Object[].class);
    pedalonsQuery.getParams().forEach(typedQuery::setParameter);

    List<Object[]> rows = typedQuery.getResultList();
    Map<Long, Long> counts = new HashMap<>();
    for (Object[] row : rows) {
      counts.put((Long) row[0], ((Number) row[1]).longValue());
    }
    return counts;
  }
}
