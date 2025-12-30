package com.tribly.domain.trip.repository;

import com.tribly.domain.common.repository.BaseRepository;
import com.tribly.domain.trip.TripStage;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class TripStageRepository implements BaseRepository<TripStage> {

  public Optional<TripStage> findByIdAndTrip(Long stageId, Long tripId) {
    return find("id = ?1 and trip.id = ?2 and deleted = false", stageId, tripId)
        .firstResultOptional();
  }

  public Optional<TripStage> findBySlugAndTeam(String slug, Long teamId) {
    return find("slug = ?1 and team.id = ?2 and deleted = false", slug, teamId)
        .firstResultOptional();
  }
}
