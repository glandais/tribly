package fr.pedalons.repository.trip;

import fr.pedalons.domain.trip.TripParticipation;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TripParticipationRepository implements BaseRepository<TripParticipation> {

  public Optional<TripParticipation> findByUserAndTrip(Long userId, Long tripId) {
    return find("user.id = ?1 and trip.id = ?2", userId, tripId).firstResultOptional();
  }

  /** Every trip a user signed up for, for the GDPR data export. */
  public List<TripParticipation> findByUser(Long domainId, Long userId) {
    return list(
        "user.id = ?2 and trip.team.domain.id = ?1 order by registeredAt", domainId, userId);
  }
}
