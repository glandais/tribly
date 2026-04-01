package fr.pedalons.repository.place;

import fr.pedalons.domain.place.Place;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.repository.common.BaseRepository;
import fr.pedalons.repository.query.OrClause;
import fr.pedalons.repository.query.PedalonsQuery;
import fr.pedalons.repository.query.SimpleClause;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class PlaceRepository implements BaseRepository<Place> {

  public Optional<Place> findByIdAndTeam(Long id, Long teamId) {
    return find("id = ?1 AND team.id = ?2", id, teamId).firstResultOptional();
  }

  public PedalonsPage<Place> findByTeam(
      Long teamId, int page, int size, String search, boolean filterStart, boolean filterEnd) {
    PedalonsQuery pedalonsQuery =
        new PedalonsQuery().and("team.id = :teamId", Map.of("teamId", teamId));
    if (search != null && !search.isBlank()) {
      String searchParam = "%" + search.toLowerCase() + "%";
      OrClause orClause = new OrClause();
      orClause.add(new SimpleClause("LOWER(name) LIKE :search", Map.of("search", searchParam)));
      orClause.add(new SimpleClause("LOWER(address) LIKE :search", Map.of("search", searchParam)));
      pedalonsQuery.and(orClause);
    }
    if (filterStart) {
      pedalonsQuery.and("startPlace = true", Map.of());
    }
    if (filterEnd) {
      pedalonsQuery.and("endPlace = true", Map.of());
    }
    return getPage(pedalonsQuery, page, size);
  }
}
