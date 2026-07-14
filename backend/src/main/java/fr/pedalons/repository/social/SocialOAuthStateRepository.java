package fr.pedalons.repository.social;

import fr.pedalons.domain.social.SocialOAuthState;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class SocialOAuthStateRepository implements PanacheRepository<SocialOAuthState> {

  public Optional<SocialOAuthState> findValidByState(String state) {
    return find("state = ?1 and expiresAt > CURRENT_TIMESTAMP", state).firstResultOptional();
  }

  public long deleteExpiredStates() {
    return delete("expiresAt < CURRENT_TIMESTAMP");
  }
}
