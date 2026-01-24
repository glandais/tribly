package com.tribly.repository.place;

import com.tribly.domain.place.Place;
import com.tribly.dto.common.TriblyPage;
import com.tribly.repository.common.BaseRepository;
import com.tribly.repository.query.TriblyQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class PlaceRepository implements BaseRepository<Place> {

  public Optional<Place> findByIdAndTeam(Long id, Long teamId) {
    return find("id = ?1 AND team.id = ?2 AND deleted = false", id, teamId).firstResultOptional();
  }

  public TriblyPage<Place> findByTeam(Long teamId, int page, int size) {
    TriblyQuery triblyQuery =
        new TriblyQuery()
            .and("team.id = :teamId", Map.of("teamId", teamId))
            .and("deleted = false", Map.of());
    return getPage(triblyQuery, page, size);
  }
}
