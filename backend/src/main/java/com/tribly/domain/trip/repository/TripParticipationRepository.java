package com.tribly.domain.trip.repository;

import com.tribly.domain.common.repository.BaseRepository;
import com.tribly.domain.trip.TripParticipation;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class TripParticipationRepository implements BaseRepository<TripParticipation> {

  public Optional<TripParticipation> findByUserAndTrip(Long userId, Long tripId) {
    return find("user.id = ?1 and trip.id = ?2 and deleted = false", userId, tripId)
        .firstResultOptional();
  }

  public Optional<TripParticipation> findByUserAndTripIncludingDeleted(Long userId, Long tripId) {
    return find("user.id = ?1 and trip.id = ?2", userId, tripId).firstResultOptional();
  }
}
