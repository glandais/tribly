package com.tribly.domain.ride.repository;

import com.tribly.domain.common.repository.BaseRepository;
import com.tribly.domain.ride.RideGroup;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RideGroupRepository implements BaseRepository<RideGroup> {

  public Optional<RideGroup> findByIdAndRide(Long groupId, Long rideId) {
    return find("id = ?1 and ride.id = ?2 and deleted = false", groupId, rideId)
        .firstResultOptional();
  }

  public List<RideGroup> findByRide(Long rideId) {
    return find("ride.id = ?1 and deleted = false order by sortOrder", rideId).list();
  }
}
