package fr.pedalons.repository.notification;

import fr.pedalons.domain.notification.NotificationPreference;
import fr.pedalons.enums.NotificationType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class NotificationPreferenceRepository implements PanacheRepository<NotificationPreference> {

  /** Keeps each {@code IN} list well below PostgreSQL's bind-parameter ceiling. */
  private static final int CHUNK = 1000;

  public List<NotificationPreference> findByUser(Long userId) {
    return list("user.id", userId);
  }

  /**
   * The overrides of one type for a whole audience — a 2 000-member team costs two queries, not
   * 2 000.
   */
  public List<NotificationPreference> findByTypeAndUsers(
      NotificationType type, List<Long> userIds) {
    List<NotificationPreference> result = new ArrayList<>();
    for (int from = 0; from < userIds.size(); from += CHUNK) {
      List<Long> chunk = userIds.subList(from, Math.min(from + CHUNK, userIds.size()));
      result.addAll(list("type = ?1 and user.id in ?2", type, chunk));
    }
    return result;
  }

  public long deleteByUser(Long userId) {
    return delete("user.id", userId);
  }
}
