package fr.pedalons.repository.trip;

import fr.pedalons.domain.trip.TripParticipation;
import fr.pedalons.domain.user.User;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class TripParticipationRepository implements BaseRepository<TripParticipation> {

  public Optional<TripParticipation> findByUserAndTrip(Long userId, Long tripId) {
    return find("user.id = ?1 and trip.id = ?2", userId, tripId).firstResultOptional();
  }

  /**
   * Which of these trips the given user is registered for, in a single query — the bulk counterpart
   * of {@link #findByUserAndTrip}, so a list page costs one query instead of one per row. Served by
   * {@code idx_trip_participations_user_trip} ({@code user_id, trip_id}).
   */
  public Set<Long> findRegisteredTripIds(Long userId, Collection<Long> tripIds) {
    if (tripIds.isEmpty()) {
      return Set.of();
    }
    List<Long> rows =
        getEntityManager()
            .createQuery(
                "select p.trip.id from TripParticipation p "
                    + "where p.user.id = :userId and p.trip.id in (:tripIds)",
                Long.class)
            .setParameter("userId", userId)
            .setParameter("tripIds", tripIds)
            .getResultList();
    return new HashSet<>(rows);
  }

  /** Every trip a user signed up for, for the GDPR data export. */
  public List<TripParticipation> findByUser(Long domainId, Long userId) {
    return list(
        "user.id = ?2 and trip.team.domain.id = ?1 order by registeredAt", domainId, userId);
  }

  /** The live users registered to a trip who are still members of its team. */
  public List<User> findRegisteredMemberUsers(Long tripId) {
    return getEntityManager()
        .createQuery(
            "select distinct u from TripParticipation p join p.user u"
                + " where p.trip.id = :tripId and u.deleted = false"
                + " and exists (select 1 from UserTeam ut"
                + " where ut.user = u and ut.team = p.trip.team)",
            User.class)
        .setParameter("tripId", tripId)
        .getResultList();
  }
}
