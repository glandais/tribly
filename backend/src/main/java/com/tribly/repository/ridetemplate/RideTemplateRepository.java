package com.tribly.repository.ridetemplate;

import com.tribly.domain.ridetemplate.RideTemplate;
import com.tribly.dto.common.TriblyPage;
import com.tribly.repository.common.BaseRepository;
import com.tribly.repository.query.TriblyQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class RideTemplateRepository implements BaseRepository<RideTemplate> {

  public Optional<RideTemplate> findByTeamAndSlug(Long teamId, String slug) {
    return find("team.id = ?1 AND slug = ?2 AND deleted = false", teamId, slug)
        .firstResultOptional();
  }

  public boolean existsByTeamAndSlug(Long teamId, String slug) {
    return count("team.id = ?1 AND slug = ?2", teamId, slug) > 0;
  }

  public TriblyPage<RideTemplate> findByTeam(
      Long teamId, @Nullable String search, int page, int size) {
    TriblyQuery triblyQuery =
        new TriblyQuery()
            .and("team.id = :teamId", Map.of("teamId", teamId))
            .and("deleted = false", Map.of())
            .order("name ASC");

    if (search != null && !search.isBlank()) {
      triblyQuery.and(
          "LOWER(name) LIKE :search", Map.of("search", "%" + search.toLowerCase() + "%"));
    }

    return getPage(triblyQuery, page, size);
  }
}
