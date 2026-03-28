package fr.pedalons.repository.auth;

import fr.pedalons.domain.auth.Passkey;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PasskeyRepository implements PanacheRepository<Passkey> {

  public Optional<Passkey> findByCredentialId(byte[] credentialId) {
    return find("credentialId = ?1", (Object) credentialId).firstResultOptional();
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
