package com.tribly.repository.team;

import com.tribly.domain.team.TeamSlugRedirect;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class TeamSlugRedirectRepository implements PanacheRepository<TeamSlugRedirect> {

  public Optional<TeamSlugRedirect> findByOldSlug(String oldSlug) {
    return find("oldSlug = ?1", oldSlug).firstResultOptional();
  }

  public void deleteByOldSlug(String oldSlug) {
    delete("oldSlug = ?1", oldSlug);
  }
}
