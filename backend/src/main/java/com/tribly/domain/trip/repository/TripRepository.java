package com.tribly.domain.trip.repository;

import com.tribly.domain.common.repository.EntityWithPublishAtRepository;
import com.tribly.domain.common.repository.TeamEntityQueryBasic;
import com.tribly.domain.common.repository.TeamEntityRepository;
import com.tribly.domain.trip.Trip;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TripRepository
    implements TeamEntityRepository<Trip, TeamEntityQueryBasic>,
        EntityWithPublishAtRepository<Trip> {
  @Override
  public String getTypeName() {
    return "Trip";
  }
}
