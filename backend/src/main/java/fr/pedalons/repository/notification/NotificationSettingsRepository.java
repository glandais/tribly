package fr.pedalons.repository.notification;

import fr.pedalons.domain.notification.NotificationSettings;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class NotificationSettingsRepository implements PanacheRepository<NotificationSettings> {

  private static final int CHUNK = 1000;

  public Optional<NotificationSettings> findByUser(Long userId) {
    return find("user.id", userId).firstResultOptional();
  }

  /** Who, in an audience, reads their e-mails as a daily digest. */
  public Set<Long> findDigestUserIds(List<Long> userIds) {
    Set<Long> digest = new HashSet<>();
    for (int from = 0; from < userIds.size(); from += CHUNK) {
      List<Long> chunk = userIds.subList(from, Math.min(from + CHUNK, userIds.size()));
      digest.addAll(
          getEntityManager()
              .createQuery(
                  "select s.user.id from NotificationSettings s"
                      + " where s.emailDigest = true and s.user.id in :userIds",
                  Long.class)
              .setParameter("userIds", chunk)
              .getResultList());
    }
    return digest;
  }

  public long deleteByUser(Long userId) {
    return delete("user.id", userId);
  }
}
