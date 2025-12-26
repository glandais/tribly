package com.tribly.domain.ride.repository;

import com.tribly.domain.common.repository.EntityWithSlugRepository;
import com.tribly.domain.common.repository.PublicationRepository;
import com.tribly.domain.ride.Ride;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RideRepository implements PublicationRepository<Ride>, EntityWithSlugRepository<Ride> {

  @Override
  public Class<Ride> getEntityClass() {
    return Ride.class;
  }
}
