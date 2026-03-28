package fr.pedalons.repository.team;

import fr.pedalons.domain.team.TeamSlugRedirect;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class TeamSlugRedirectRepository implements PanacheRepository<TeamSlugRedirect> {

  public Optional<TeamSlugRedirect> findByOldSlugAndDomain(Long domainId, String oldSlug) {
    return find("team.domain.id = ?1 and oldSlug = ?2", domainId, oldSlug).firstResultOptional();
  }

  public void deleteByOldSlugAndDomain(Long domainId, String oldSlug) {
    delete("team.domain.id = ?1 and oldSlug = ?2", domainId, oldSlug);
  }
}
