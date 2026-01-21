package com.tribly.repository.auth;

import com.tribly.domain.auth.AuthToken;
import com.tribly.enums.AuthTokenType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@ApplicationScoped
public class AuthTokenRepository implements PanacheRepository<AuthToken> {

  public Optional<AuthToken> findValidByTokenHash(String tokenHash) {
    return find("tokenHash = ?1 and usedAt is null and expiresAt > CURRENT_TIMESTAMP", tokenHash)
        .firstResultOptional();
  }

  public Optional<AuthToken> findValidByEmailAndType(String email, AuthTokenType tokenType) {
    return find(
            "email = ?1 and tokenType = ?2 and usedAt is null and expiresAt > CURRENT_TIMESTAMP",
            email,
            tokenType)
        .firstResultOptional();
  }

  public int invalidateByEmailAndType(String email, AuthTokenType tokenType) {
    return update(
        "usedAt = CURRENT_TIMESTAMP where email = ?1 and tokenType = ?2 and usedAt is null",
        email,
        tokenType);
  }

  public long deleteExpiredTokens() {
    return delete("expiresAt < CURRENT_TIMESTAMP or usedAt is not null");
  }

  /**
   * Counts recent OTP tokens created for a given email within a time window. Used for rate
   * limiting.
   */
  public long countRecentByEmailAndType(String email, AuthTokenType tokenType, int minutes) {
    Instant cutoff = Instant.now().minus(minutes, ChronoUnit.MINUTES);
    return count("email = ?1 and tokenType = ?2 and createdAt > ?3", email, tokenType, cutoff);
  }
}
