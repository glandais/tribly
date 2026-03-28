package fr.pedalons.repository.ride;

import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class RideGroupRepository implements BaseRepository<RideGroup> {

  public Optional<RideGroup> findByIdAndRide(Long groupId, Long rideId) {
    return find("id = ?1 and ride.id = ?2", groupId, rideId).firstResultOptional();
  }
}
