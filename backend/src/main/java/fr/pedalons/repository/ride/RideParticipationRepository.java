package fr.pedalons.repository.ride;

import fr.pedalons.domain.ride.RideParticipation;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class RideParticipationRepository implements BaseRepository<RideParticipation> {

  public Optional<RideParticipation> findByUserAndGroup(Long userId, Long groupId) {
    return find("user.id = ?1 and rideGroup.id = ?2", userId, groupId).firstResultOptional();
  }

  public Optional<RideParticipation> findByUserAndRide(Long userId, Long rideId) {
    return find("user.id = ?1 and rideGroup.ride.id = ?2", userId, rideId).firstResultOptional();
  }
}
