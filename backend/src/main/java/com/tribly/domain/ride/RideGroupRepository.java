package com.tribly.domain.ride;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RideGroupRepository implements PanacheRepository<RideGroup> {

  public Optional<RideGroup> findByIdAndRide(Long groupId, Long rideId) {
    return find("id = ?1 and ride.id = ?2 and deleted = false", groupId, rideId)
        .firstResultOptional();
  }

  public List<RideGroup> findByRide(Long rideId) {
    return find("ride.id = ?1 and deleted = false order by sortOrder", rideId).list();
  }
}
