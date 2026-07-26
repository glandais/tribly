package fr.pedalons.repository.ride;

import fr.pedalons.domain.ride.RideParticipation;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class RideParticipationRepository implements BaseRepository<RideParticipation> {

  public Optional<RideParticipation> findByUserAndGroup(Long userId, Long groupId) {
    return find("user.id = ?1 and rideGroup.id = ?2", userId, groupId).firstResultOptional();
  }

  /**
   * The group each of these rides has the given user registered in, in a single query.
   *
   * <p>This is the bulk form the list path needs. Resolving "am I registered?" row by row with
   * {@link #findByUserAndRide} costs one query per ride, so a page of 20 rides costs 20 queries;
   * here the whole page costs one, and it is served by {@code idx_ride_participations_user_group}
   * ({@code user_id, ride_group_id}).
   *
   * <p>Rides the user is not registered in are simply absent from the map. Scalars are selected
   * rather than entities so nothing enters the persistence context.
   *
   * @return ride id → group id; only for the ride ids that were asked for
   */
  public Map<Long, Long> findRegisteredGroupIdsByRideIds(Long userId, Collection<Long> rideIds) {
    if (rideIds.isEmpty()) {
      return Map.of();
    }
    List<Object[]> rows =
        getEntityManager()
            .createQuery(
                "select p.rideGroup.ride.id, p.rideGroup.id from RideParticipation p "
                    + "where p.user.id = :userId and p.rideGroup.ride.id in (:rideIds)",
                Object[].class)
            .setParameter("userId", userId)
            .setParameter("rideIds", rideIds)
            .getResultList();
    Map<Long, Long> byRide = new HashMap<>();
    for (Object[] row : rows) {
      // joinGroup forbids a second group on the same ride, but a stale row must not decide which
      // group wins arbitrarily on every request: the first one seen stays.
      byRide.putIfAbsent((Long) row[0], (Long) row[1]);
    }
    return byRide;
  }

  public Optional<RideParticipation> findByUserAndRide(Long userId, Long rideId) {
    return find("user.id = ?1 and rideGroup.ride.id = ?2", userId, rideId).firstResultOptional();
  }

  /** Every ride a user signed up for, for the GDPR data export. */
  public List<RideParticipation> findByUser(Long domainId, Long userId) {
    return list(
        "user.id = ?2 and rideGroup.ride.team.domain.id = ?1 order by registeredAt",
        domainId,
        userId);
  }
}
