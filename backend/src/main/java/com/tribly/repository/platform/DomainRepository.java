package com.tribly.repository.platform;

import com.tribly.domain.platform.Domain;
import com.tribly.repository.common.BaseRepository;
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
