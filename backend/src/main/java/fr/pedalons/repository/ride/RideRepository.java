package fr.pedalons.repository.ride;

import fr.pedalons.domain.ride.Ride;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamEntityType;
import fr.pedalons.repository.common.TeamEntityQueryBasic;
import fr.pedalons.repository.common.TeamEntityRepository;
import fr.pedalons.repository.query.PedalonsQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class RideRepository implements TeamEntityRepository<Ride, TeamEntityQueryBasic> {
  @Override
  public TeamEntityType getEntityType() {
    return TeamEntityType.RIDE;
  }

  @Override
  public EntityType getAllEntityType() {
    return EntityType.RIDE;
  }

  @Override
  public TeamEntityQueryBasic getQuerySlug(
      Long domainId,
      Long teamId,
      @Nullable Long userId,
      String slug,
      boolean includeDeleted,
      boolean platformAdmin) {
    return TeamEntityQueryBasic.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .slug(slug)
        .includeDeleted(includeDeleted)
        .platformAdmin(platformAdmin)
        .build();
  }

  @Override
  public TeamEntityQueryBasic getQueryId(
      Long domainId,
      Long teamId,
      @Nullable Long userId,
      Long id,
      boolean includeDeleted,
      boolean platformAdmin) {
    return TeamEntityQueryBasic.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .id(id)
        .includeDeleted(includeDeleted)
        .platformAdmin(platformAdmin)
        .build();
  }

  /**
   * Find the rides that reference a route, either directly ({@code ride.route}) or through one of
   * their groups ({@code group.route}). The {@code query} carries the domain/visibility filters, so
   * only rides the caller may see are returned. Each ride yields at most one row, so the result is
   * already deduplicated.
   */
  public List<Ride> findByRouteId(TeamEntityQueryBasic query, Long routeId) {
    QueryShape shape = new QueryShape("te", getEntityType().getTypeName() + " te", true);
    PedalonsQuery pedalonsQuery = getPedalonsQuery(query, true, shape);
    pedalonsQuery.and(
        "(te.route.id = :routeId OR EXISTS "
            + "(select 1 from RideGroup g where g.ride = te and g.route.id = :routeId))",
        Map.of("routeId", routeId));
    return findAll(pedalonsQuery);
  }
}
