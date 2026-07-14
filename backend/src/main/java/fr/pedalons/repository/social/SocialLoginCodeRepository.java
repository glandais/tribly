package fr.pedalons.repository.social;

import fr.pedalons.domain.social.SocialLoginCode;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Optional;

@ApplicationScoped
public class SocialLoginCodeRepository implements PanacheRepository<SocialLoginCode> {

  public Optional<SocialLoginCode> findValidByCodeHash(String codeHash) {
    return find("codeHash = ?1 and usedAt is null and expiresAt > CURRENT_TIMESTAMP", codeHash)
        .firstResultOptional();
  }

  public long deleteExpiredCodes() {
    return delete("expiresAt < CURRENT_TIMESTAMP or usedAt is not null");
  }
}
