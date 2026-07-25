package fr.pedalons.repository.ride;

import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RideGroupRepository implements BaseRepository<RideGroup> {

  public Optional<RideGroup> findByIdAndRide(Long groupId, Long rideId) {
    return find("id = ?1 and ride.id = ?2", groupId, rideId).firstResultOptional();
  }

  /** Ride groups a user created, for the GDPR data export. */
  public List<RideGroup> findByCreator(Long domainId, Long userId) {
    return list(
        "createdBy.id = ?2 and ride.team.domain.id = ?1 order by createdAt", domainId, userId);
  }
}
