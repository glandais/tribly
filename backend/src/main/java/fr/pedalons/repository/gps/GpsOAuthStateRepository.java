package fr.pedalons.repository.gps;

import fr.pedalons.domain.gps.GpsOAuthState;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class GpsOAuthStateRepository implements PanacheRepository<GpsOAuthState> {

  public Optional<GpsOAuthState> findValidByState(String state) {
    return find("state = ?1 and expiresAt > CURRENT_TIMESTAMP", state).firstResultOptional();
  }

  public long deleteExpiredStates() {
    return delete("expiresAt < CURRENT_TIMESTAMP");
  }
}
