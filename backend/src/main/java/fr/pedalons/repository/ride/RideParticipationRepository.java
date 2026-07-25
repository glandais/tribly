package fr.pedalons.repository.ride;

import fr.pedalons.domain.ride.RideParticipation;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RideParticipationRepository implements BaseRepository<RideParticipation> {

  public Optional<RideParticipation> findByUserAndGroup(Long userId, Long groupId) {
    return find("user.id = ?1 and rideGroup.id = ?2", userId, groupId).firstResultOptional();
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
