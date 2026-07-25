package fr.pedalons.repository.social;

import fr.pedalons.domain.social.SocialLoginCode;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SocialLoginCodeRepository implements PanacheRepository<SocialLoginCode> {

  public Optional<SocialLoginCode> findValidByCodeHash(String codeHash) {
    return find("codeHash = ?1 and usedAt is null and expiresAt > CURRENT_TIMESTAMP", codeHash)
        .firstResultOptional();
  }

  /** Social login codes minted for a user on a domain — for the GDPR data export. */
  public List<SocialLoginCode> findByUser(Long domainId, Long userId) {
    return list("user.id = ?2 and domainId = ?1 order by createdAt", domainId, userId);
  }

  public long deleteExpiredCodes() {
    return delete("expiresAt < CURRENT_TIMESTAMP or usedAt is not null");
  }
}
