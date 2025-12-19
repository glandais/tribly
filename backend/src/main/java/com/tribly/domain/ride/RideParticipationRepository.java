package com.tribly.domain.ride;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class RideParticipationRepository implements PanacheRepository<RideParticipation> {

  public Optional<RideParticipation> findByUserAndGroup(Long userId, Long groupId) {
    return find("user.id = ?1 and rideGroup.id = ?2 and deleted = false", userId, groupId)
        .firstResultOptional();
  }

  public Optional<RideParticipation> findByUserAndRide(Long userId, Long rideId) {
    return find("user.id = ?1 and rideGroup.ride.id = ?2 and deleted = false", userId, rideId)
        .firstResultOptional();
  }
}
