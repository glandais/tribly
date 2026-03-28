package fr.pedalons.repository.user;

import fr.pedalons.domain.user.User;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements BaseRepository<User> {

  public Optional<User> findByEmailAndDomain(Long domainId, String email) {
    return find("domain.id = ?1 and email = ?2 and deleted = false", domainId, email)
        .firstResultOptional();
  }

  public Optional<User> findActiveByIdAndDomain(Long domainId, Long id) {
    return find("domain.id = ?1 and id = ?2 and deleted = false", domainId, id)
        .firstResultOptional();
  }

  public Optional<User> findActiveById(Long id) {
    return find("id = ?1 and deleted = false", id).firstResultOptional();
  }

  public List<User> searchByDisplayNameAndDomain(Long domainId, String query, int limit) {
    return find(
            "domain.id = ?1 and LOWER(displayName) LIKE LOWER(?2) and deleted = false",
            domainId,
            "%" + query + "%")
        .page(0, limit)
        .list();
  }
}
