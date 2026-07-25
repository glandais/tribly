package fr.pedalons.repository.auth;

import fr.pedalons.domain.auth.AuthToken;
import fr.pedalons.enums.AuthTokenType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class AuthTokenRepository implements PanacheRepository<AuthToken> {

  public Optional<AuthToken> findValidByTokenHash(String tokenHash) {
    return find("tokenHash = ?1 and usedAt is null and expiresAt > CURRENT_TIMESTAMP", tokenHash)
        .firstResultOptional();
  }

  public Optional<AuthToken> findValidByEmailAndType(
      String email, AuthTokenType tokenType, Long domainId) {
    return find(
            "email = ?1 and tokenType = ?2 and domainId = ?3 and usedAt is null and expiresAt >"
                + " CURRENT_TIMESTAMP",
            email,
            tokenType,
            domainId)
        .firstResultOptional();
  }

  public int invalidateByEmailAndType(String email, AuthTokenType tokenType, Long domainId) {
    return update(
        "usedAt = CURRENT_TIMESTAMP where email = ?1 and tokenType = ?2 and domainId = ?3 and"
            + " usedAt is null",
        email,
        tokenType,
        domainId);
  }

  /** Tokens issued to a user on a domain — for the GDPR data export. */
  public List<AuthToken> findByUser(Long domainId, Long userId) {
    return list("user.id = ?2 and domainId = ?1 order by createdAt", domainId, userId);
  }

  public long deleteExpiredTokens() {
    return delete("expiresAt < CURRENT_TIMESTAMP or usedAt is not null");
  }

  /**
   * Counts recent OTP tokens created for a given email within a time window. Used for rate
   * limiting.
   */
  public long countRecentByEmailAndType(
      String email, AuthTokenType tokenType, int minutes, Long domainId) {
    Instant cutoff = Instant.now().minus(minutes, ChronoUnit.MINUTES);
    return count(
        "email = ?1 and tokenType = ?2 and createdAt > ?3 and domainId = ?4",
        email,
        tokenType,
        cutoff,
        domainId);
  }
}
