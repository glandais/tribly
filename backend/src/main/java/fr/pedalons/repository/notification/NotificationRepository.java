package fr.pedalons.repository.notification;

import fr.pedalons.domain.notification.Notification;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class NotificationRepository implements PanacheRepository<Notification> {

  private static final String WHERE_INBOX =
      " where n.recipient.id = :recipientId and n.domainId = :domainId";

  private static final String AND_UNREAD = " and n.readAt is null";

  /**
   * A page of the inbox, newest first. The event is join-fetched: it holds everything a row
   * renders, so the page costs one query whatever its size. Ties on {@code createdAt} — every
   * notification of one fan-out shares it — are broken by id, so paging is stable.
   */
  public List<Notification> page(
      Long recipientId, Long domainId, boolean unreadOnly, int page, int size) {
    return getEntityManager()
        .createQuery(
            "select n from Notification n join fetch n.event"
                + WHERE_INBOX
                + (unreadOnly ? AND_UNREAD : "")
                + " order by n.createdAt desc, n.id desc",
            Notification.class)
        .setParameter("recipientId", recipientId)
        .setParameter("domainId", domainId)
        .setFirstResult(page * size)
        .setMaxResults(size)
        .getResultList();
  }

  public long count(Long recipientId, Long domainId, boolean unreadOnly) {
    return getEntityManager()
        .createQuery(
            "select count(n) from Notification n" + WHERE_INBOX + (unreadOnly ? AND_UNREAD : ""),
            Long.class)
        .setParameter("recipientId", recipientId)
        .setParameter("domainId", domainId)
        .getSingleResult();
  }

  /**
   * Marks one notification read if it belongs to this recipient. Returns false when it does not
   * exist <em>for them</em> — someone else's notification is indistinguishable from a missing one.
   * Already-read is a success and keeps its first {@code readAt}.
   */
  public boolean markRead(Long id, Long recipientId, Long domainId, Instant now) {
    long owned =
        count("id = ?1 and recipient.id = ?2 and domainId = ?3", id, recipientId, domainId);
    if (owned == 0) {
      return false;
    }
    update("readAt = ?1 where id = ?2 and readAt is null", now, id);
    return true;
  }

  public int markAllRead(Long recipientId, Long domainId, Instant now) {
    return update(
        "readAt = ?1 where recipient.id = ?2 and domainId = ?3 and readAt is null",
        now,
        recipientId,
        domainId);
  }

  public long deleteByEventIds(List<Long> eventIds) {
    return delete("event.id in ?1", eventIds);
  }
}
