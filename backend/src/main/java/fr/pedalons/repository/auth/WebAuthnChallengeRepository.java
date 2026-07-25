package fr.pedalons.repository.auth;

import fr.pedalons.domain.auth.WebAuthnChallenge;
import fr.pedalons.enums.WebAuthnChallengeType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WebAuthnChallengeRepository implements PanacheRepository<WebAuthnChallenge> {

  public Optional<WebAuthnChallenge> findValidByChallenge(String challenge) {
    return find("challenge = ?1 and expiresAt > CURRENT_TIMESTAMP", challenge)
        .firstResultOptional();
  }

  public Optional<WebAuthnChallenge> findValidByUserIdAndType(
      Long userId, WebAuthnChallengeType challengeType) {
    return find(
            "user.id = ?1 and challengeType = ?2 and expiresAt > CURRENT_TIMESTAMP",
            userId,
            challengeType)
        .firstResultOptional();
  }

  public Optional<WebAuthnChallenge> findValidByEmailAndType(
      String email, WebAuthnChallengeType challengeType) {
    return find(
            "email = ?1 and challengeType = ?2 and expiresAt > CURRENT_TIMESTAMP",
            email,
            challengeType)
        .firstResultOptional();
  }

  /** Challenges issued to a user — for the GDPR data export. */
  public List<WebAuthnChallenge> findByUserId(Long userId) {
    return list("user.id = ?1 order by createdAt", userId);
  }

  public long deleteExpiredChallenges() {
    return delete("expiresAt < CURRENT_TIMESTAMP");
  }

  public long deleteByUserId(Long userId) {
    return delete("user.id = ?1", userId);
  }

  public long deleteByEmail(String email) {
    return delete("email = ?1", email);
  }
}
