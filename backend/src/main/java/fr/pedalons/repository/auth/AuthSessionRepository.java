package fr.pedalons.repository.auth;

import fr.pedalons.domain.auth.AuthSession;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AuthSessionRepository implements PanacheRepository<AuthSession> {

  public Optional<AuthSession> findByRefreshTokenHash(String refreshTokenHash) {
    return find("refreshTokenHash = ?1 and revoked = false", refreshTokenHash)
        .firstResultOptional();
  }

  public List<AuthSession> findActiveByUserId(Long userId) {
    return find("user.id = ?1 and revoked = false", userId).list();
  }

  public int revokeAllByUserId(Long userId) {
    return update(
        "revoked = true, revokedAt = CURRENT_TIMESTAMP where user.id = ?1 and revoked = false",
        userId);
  }

  public long deleteExpiredSessions() {
    return delete("expiresAt < CURRENT_TIMESTAMP or revoked = true");
  }
}
