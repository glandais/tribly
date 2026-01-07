package com.tribly.repository.trip;

import com.tribly.domain.trip.TripStage;
import com.tribly.repository.common.BaseRepository;
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
