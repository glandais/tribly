package fr.pedalons.repository.platform;

import fr.pedalons.domain.platform.Domain;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class DomainRepository implements BaseRepository<Domain> {

  public Optional<Domain> findByDomain(String domain) {
    return find("domain = ?1 and active = true and deleted = false", domain).firstResultOptional();
  }

  public Optional<Domain> findByIdOptional(Long id) {
    return find("id = ?1 and deleted = false", id).firstResultOptional();
  }
}
