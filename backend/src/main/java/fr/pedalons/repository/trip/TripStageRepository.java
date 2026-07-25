package fr.pedalons.repository.trip;

import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
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

  /** Trip stages a user created, for the GDPR data export. */
  public List<TripStage> findByCreator(Long domainId, Long userId) {
    return list("createdBy.id = ?2 and team.domain.id = ?1 order by createdAt", domainId, userId);
  }
}
