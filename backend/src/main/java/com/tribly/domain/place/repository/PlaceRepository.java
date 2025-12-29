package com.tribly.domain.place.repository;

import com.tribly.domain.common.query.TriblyQuery;
import com.tribly.domain.common.repository.BaseRepository;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.place.Place;
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
