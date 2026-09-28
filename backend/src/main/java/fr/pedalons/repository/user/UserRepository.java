package fr.pedalons.repository.user;

import fr.pedalons.domain.user.User;
import fr.pedalons.enums.PlatformRole;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements BaseRepository<User> {

  /**
   * Records a login with a bulk update of {@code lastLoginAt} alone. Writing the entity would bump
   * its version: two logins of the same account at once (two tabs, the app and the site) would
   * fail on the optimistic lock. Runs after Hibernate has flushed any pending change to the user,
   * so a login that also edits the account still writes both.
   */
  public void recordLogin(Long userId) {
    update("lastLoginAt = CURRENT_TIMESTAMP where id = ?1", userId);
  }

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

  /** The live platform administrators of a domain: the moderators of last resort. */
  public List<User> findPlatformAdmins(Long domainId) {
    return list(
        "domain.id = ?1 and platformRole = ?2 and deleted = false",
        domainId,
        PlatformRole.PLATFORM_ADMIN);
  }
}
