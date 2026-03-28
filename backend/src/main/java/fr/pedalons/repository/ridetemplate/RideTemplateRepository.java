package fr.pedalons.repository.ridetemplate;

import fr.pedalons.domain.ridetemplate.RideTemplate;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.repository.common.BaseRepository;
import fr.pedalons.repository.query.PedalonsQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class RideTemplateRepository implements BaseRepository<RideTemplate> {

  public Optional<RideTemplate> findByTeamAndSlug(Long teamId, String slug) {
    return find("team.id = ?1 AND slug = ?2", teamId, slug).firstResultOptional();
  }

  public boolean existsByTeamAndSlug(Long teamId, String slug) {
    return count("team.id = ?1 AND slug = ?2", teamId, slug) > 0;
  }

  public PedalonsPage<RideTemplate> findByTeam(
      Long teamId, @Nullable String search, int page, int size) {
    PedalonsQuery pedalonsQuery =
        new PedalonsQuery().and("team.id = :teamId", Map.of("teamId", teamId)).order("name ASC");

    if (search != null && !search.isBlank()) {
      pedalonsQuery.and(
          "LOWER(name) LIKE :search", Map.of("search", "%" + search.toLowerCase() + "%"));
    }

    return getPage(pedalonsQuery, page, size);
  }
}
