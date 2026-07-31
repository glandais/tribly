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

  /**
   * Migrated Strava accounts carry a synthesized {@code strava_<athleteId>@<placeholderDomain>}
   * email. Used by the social-identity backfill. The {@code _} in {@code strava_} is escaped so it
   * is matched literally rather than as a LIKE wildcard.
   */
  public List<User> findPlaceholderStravaUsers(Long domainId, String placeholderDomain) {
    return find(
            "domain.id = ?1 and email like ?2 escape '!' and deleted = false",
            domainId,
            "strava!_%@" + placeholderDomain.toLowerCase())
        .list();
  }
}
