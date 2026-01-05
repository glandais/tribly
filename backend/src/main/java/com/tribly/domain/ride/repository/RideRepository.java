package com.tribly.domain.ride.repository;

import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TeamEntityRepository;
import com.tribly.domain.ride.Ride;
import com.tribly.enums.EntityType;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RideRepository implements TeamEntityRepository<Ride, TeamEntityQueryBasic> {
  @Override
  public EntityType getEntityType() {
    return EntityType.RIDE;
  }
}
