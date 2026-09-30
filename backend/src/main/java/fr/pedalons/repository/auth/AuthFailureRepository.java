package fr.pedalons.repository.auth;

import fr.pedalons.domain.auth.AuthFailure;
import fr.pedalons.enums.AuthFailureKind;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;

@ApplicationScoped
public class AuthFailureRepository implements PanacheRepository<AuthFailure> {

  public long countSince(Long domainId, AuthFailureKind kind, String subject, Instant since) {
    return count(
        "domainId = ?1 and kind = ?2 and subject = ?3 and createdAt > ?4",
        domainId,
        kind,
        subject,
        since);
  }

  /** Every subject together: the budget a crowd of accounts shares on one domain. */
  public long countSince(Long domainId, AuthFailureKind kind, Instant since) {
    return count("domainId = ?1 and kind = ?2 and createdAt > ?3", domainId, kind, since);
  }

  public long deleteBySubject(Long domainId, AuthFailureKind kind, String subject) {
    return delete("domainId = ?1 and kind = ?2 and subject = ?3", domainId, kind, subject);
  }

  public long deleteOlderThan(Instant cutoff) {
    return delete("createdAt < ?1", cutoff);
  }
}
