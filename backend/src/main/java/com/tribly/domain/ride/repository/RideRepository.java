package com.tribly.domain.ride.repository;

import com.tribly.domain.common.repository.TeamPublicationRepository;
import com.tribly.domain.ride.Ride;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RideRepository extends TeamPublicationRepository<Ride> {

  @Override
  public Class<Ride> getEntityClass() {
    return Ride.class;
  }
}
