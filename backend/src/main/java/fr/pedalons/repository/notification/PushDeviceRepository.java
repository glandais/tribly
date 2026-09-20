package fr.pedalons.repository.notification;

import fr.pedalons.domain.notification.PushDevice;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PushDeviceRepository implements PanacheRepository<PushDevice> {

  public Optional<PushDevice> findByToken(String token) {
    return find("token", token).firstResultOptional();
  }

  /** The devices to push to, oldest first so the order of a member's devices is stable. */
  public List<PushDevice> findByUser(Long userId) {
    return list("user.id = ?1 order by createdAt", userId);
  }

  /** Unregistering is scoped to the caller: someone else's token is not theirs to remove. */
  public long deleteByUserAndToken(Long userId, String token) {
    return delete("user.id = ?1 and token = ?2", userId, token);
  }

  /**
   * Drops the tokens FCM has told us are dead. Not scoped to a user on purpose: the answer is about
   * the token, and by then it may already have moved to another account on the same phone —
   * a token FCM rejects is unreachable whoever holds it.
   */
  public long deleteByTokens(List<String> tokens) {
    return tokens.isEmpty() ? 0 : delete("token in ?1", tokens);
  }
}
