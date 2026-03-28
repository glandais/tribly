package fr.pedalons.repository.common;

import fr.pedalons.domain.common.TeamEntitySlugRedirect;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class TeamEntitySlugRedirectRepository implements PanacheRepository<TeamEntitySlugRedirect> {

  public Optional<TeamEntitySlugRedirect> findByTeamAndEntityTypeAndOldSlug(
      Long teamId, Integer entityType, String oldSlug) {
    return find("team.id = ?1 and entityType = ?2 and oldSlug = ?3", teamId, entityType, oldSlug)
        .firstResultOptional();
  }

  public void deleteByTeamAndEntityTypeAndOldSlug(Long teamId, Integer entityType, String oldSlug) {
    delete("team.id = ?1 and entityType = ?2 and oldSlug = ?3", teamId, entityType, oldSlug);
  }
}
