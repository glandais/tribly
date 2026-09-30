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

  /** The session whose token was {@code refreshTokenHash} before its last rotation. */
  public Optional<AuthSession> findByPreviousRefreshTokenHash(String refreshTokenHash) {
    return find("previousRefreshTokenHash = ?1 and revoked = false", refreshTokenHash)
        .firstResultOptional();
  }

  /**
   * Replaces the session's token, keeping the one presented as the previous token. Conditional on
   * the session still holding {@code presentedHash}: of two refreshes racing with the same token,
   * exactly one rotates (0 is returned to the other), and none overwrites a concurrent revocation.
   */
  public int rotate(Long sessionId, String presentedHash, String nextHash) {
    return update(
        "refreshTokenHash = ?3, previousRefreshTokenHash = ?2, rotatedAt = CURRENT_TIMESTAMP,"
            + " lastUsedAt = CURRENT_TIMESTAMP"
            + " where id = ?1 and refreshTokenHash = ?2 and revoked = false",
        sessionId,
        presentedHash,
        nextHash);
  }

  public int revokeById(Long sessionId) {
    return update(
        "revoked = true, revokedAt = CURRENT_TIMESTAMP where id = ?1 and revoked = false",
        sessionId);
  }

  public List<AuthSession> findActiveByUserId(Long userId) {
    return find("user.id = ?1 and revoked = false", userId).list();
  }

  /**
   * Records a use of the session with a bulk update of {@code lastUsedAt} alone, so concurrent
   * refreshes never conflict and never overwrite a concurrent revocation.
   */
  public int markUsed(Long sessionId) {
    return update("lastUsedAt = CURRENT_TIMESTAMP where id = ?1", sessionId);
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
