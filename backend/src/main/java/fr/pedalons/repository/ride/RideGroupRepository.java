package fr.pedalons.repository.ride;

import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class RideGroupRepository implements BaseRepository<RideGroup> {

  public Optional<RideGroup> findByIdAndRide(Long groupId, Long rideId) {
    return find("id = ?1 and ride.id = ?2", groupId, rideId).firstResultOptional();
  }

  /**
   * The names of a set of groups, in one query.
   *
   * <p>Naming the group a user joined would otherwise mean reaching for {@code ride.getGroups()},
   * which loads every group and — through {@code getCurrentParticipants} — every participation of
   * every ride on the page. This projects the two scalars needed and hydrates nothing.
   *
   * <p>Group ids handed in here always come from the caller's own participations, themselves
   * restricted to rides a {@code PedalonsQuery} already returned, so this answers about nothing the
   * caller could not already see.
   *
   * @return group id → name, only for the ids that exist
   */
  public Map<Long, String> findNamesByIds(Collection<Long> groupIds) {
    if (groupIds.isEmpty()) {
      return Map.of();
    }
    List<Object[]> rows =
        getEntityManager()
            .createQuery(
                "select g.id, g.name from RideGroup g where g.id in (:ids)", Object[].class)
            .setParameter("ids", groupIds)
            .getResultList();
    Map<Long, String> names = new HashMap<>(rows.size());
    for (Object[] row : rows) {
      names.put((Long) row[0], (String) row[1]);
    }
    return names;
  }

  /** Ride groups a user created, for the GDPR data export. */
  public List<RideGroup> findByCreator(Long domainId, Long userId) {
    return list(
        "createdBy.id = ?2 and ride.team.domain.id = ?1 order by createdAt", domainId, userId);
  }
}
