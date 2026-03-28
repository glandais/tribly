package fr.pedalons.repository.place;

import fr.pedalons.domain.place.Place;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.repository.common.BaseRepository;
import fr.pedalons.repository.query.PedalonsQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class PlaceRepository implements BaseRepository<Place> {

  public Optional<Place> findByIdAndTeam(Long id, Long teamId) {
    return find("id = ?1 AND team.id = ?2", id, teamId).firstResultOptional();
  }

  public PedalonsPage<Place> findByTeam(Long teamId, int page, int size) {
    PedalonsQuery pedalonsQuery =
        new PedalonsQuery().and("team.id = :teamId", Map.of("teamId", teamId));
    return getPage(pedalonsQuery, page, size);
  }
}
