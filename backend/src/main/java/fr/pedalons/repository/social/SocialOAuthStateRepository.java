package fr.pedalons.repository.social;

import fr.pedalons.domain.social.SocialOAuthState;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SocialOAuthStateRepository implements PanacheRepository<SocialOAuthState> {

  public Optional<SocialOAuthState> findValidByState(String state) {
    return find("state = ?1 and expiresAt > CURRENT_TIMESTAMP", state).firstResultOptional();
  }

  /** In-flight social OAuth handshakes for a user on a domain — for the GDPR data export. */
  public List<SocialOAuthState> findByUser(Long domainId, Long userId) {
    return list("user.id = ?2 and domainId = ?1 order by createdAt", domainId, userId);
  }

  public long deleteExpiredStates() {
    return delete("expiresAt < CURRENT_TIMESTAMP");
  }
}
