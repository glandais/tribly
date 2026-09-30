package fr.pedalons.repository.auth;

import fr.pedalons.domain.auth.Passkey;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PasskeyRepository implements PanacheRepository<Passkey> {

  /**
   * The passkey a sign-in on this site presents. A credential registered by an account of another
   * site is not found at all (audit M8, docs/LEDGER_*.md SEC-25).
   */
  public Optional<Passkey> findByCredentialIdAndDomain(byte[] credentialId, Long domainId) {
    return find("credentialId = ?1 and user.domain.id = ?2", credentialId, domainId)
        .firstResultOptional();
  }

  /**
   * Whether any account, on any site, already holds this credential: the column is unique across
   * the whole table, so a registration must look everywhere before inserting.
   */
  public boolean existsByCredentialId(byte[] credentialId) {
    return count("credentialId = ?1", (Object) credentialId) > 0;
  }

  public List<Passkey> findByUserId(Long userId) {
    return find("user.id = ?1", userId).list();
  }

  public Optional<Passkey> findByIdAndUserId(Long id, Long userId) {
    return find("id = ?1 and user.id = ?2", id, userId).firstResultOptional();
  }

  public int countByUserId(Long userId) {
    return (int) count("user.id = ?1", userId);
  }
}
