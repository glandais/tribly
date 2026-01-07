package com.tribly.repository.ride;

import com.tribly.domain.ride.RideParticipation;
import com.tribly.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class RideParticipationRepository implements BaseRepository<RideParticipation> {

  public Optional<RideParticipation> findByUserAndGroup(Long userId, Long groupId) {
    return find("user.id = ?1 and rideGroup.id = ?2 and deleted = false", userId, groupId)
        .firstResultOptional();
  }

  public Optional<RideParticipation> findByUserAndRide(Long userId, Long rideId) {
    return find("user.id = ?1 and rideGroup.ride.id = ?2 and deleted = false", userId, rideId)
        .firstResultOptional();
  }

  public Optional<RideParticipation> findByUserAndRideIncludingDeleted(Long userId, Long rideId) {
    return find("user.id = ?1 and rideGroup.ride.id = ?2", userId, rideId).firstResultOptional();
  }
}
