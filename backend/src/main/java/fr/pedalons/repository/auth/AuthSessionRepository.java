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

  /** Every session ever recorded for a user, revoked ones included — for the GDPR data export. */
  public List<AuthSession> findAllByUserId(Long userId) {
    return list("user.id = ?1 order by createdAt", userId);
  }

  public long deleteExpiredSessions() {
    return delete("expiresAt < CURRENT_TIMESTAMP or revoked = true");
  }
}
