package fr.pedalons.repository.notification;

import fr.pedalons.domain.notification.NotificationDelivery;
import fr.pedalons.enums.NotificationChannel;
import fr.pedalons.enums.NotificationDeliveryStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class NotificationDeliveryRepository implements PanacheRepository<NotificationDelivery> {

  /**
   * Ids of up to {@code limit} due deliveries of a channel, locked for this transaction. The caller
   * flips them to {@code SENDING} in the same transaction, so a second worker skips them now and
   * finds them no longer PENDING afterwards.
   */
  @SuppressWarnings("unchecked")
  public List<Long> lockDue(NotificationChannel channel, Instant now, int limit) {
    List<Number> ids =
        getEntityManager()
            .createNativeQuery(
                """
                select id from notification_deliveries
                where channel = :channel and status = 'PENDING' and next_attempt_at <= :now
                order by next_attempt_at
                limit :limit
                for update skip locked
                """)
            .setParameter("channel", channel.name())
            .setParameter("now", java.sql.Timestamp.from(now))
            .setParameter("limit", limit)
            .getResultList();
    return ids.stream().map(Number::longValue).toList();
  }

  public int markSending(List<Long> ids, Instant now) {
    return update(
        "status = ?1, attempts = attempts + 1, lastAttemptAt = ?2 where id in ?3",
        NotificationDeliveryStatus.SENDING,
        now,
        ids);
  }

  /**
   * The deliveries with everything a sender needs: notification, its event snapshot and the
   * recipient, in one query.
   */
  public List<NotificationDelivery> findWithContent(List<Long> ids) {
    return getEntityManager()
        .createQuery(
            "select d from NotificationDelivery d"
                + " join fetch d.notification n"
                + " join fetch n.event"
                + " join fetch n.recipient"
                + " where d.id in :ids",
            NotificationDelivery.class)
        .setParameter("ids", ids)
        .getResultList();
  }

  /**
   * Claimed, then the worker died before marking them: back on the queue, unless they have used
   * every attempt — a delivery that kills its worker each time must end, not be retried forever.
   * Returns how many were requeued.
   */
  public int resetStuck(Instant lastAttemptBefore, int maxAttempts) {
    update(
        "status = ?1, errorMessage = ?2 where status = ?3 and lastAttemptAt < ?4 and attempts >="
            + " ?5",
        NotificationDeliveryStatus.FAILED,
        "Stuck in SENDING after the last attempt",
        NotificationDeliveryStatus.SENDING,
        lastAttemptBefore,
        maxAttempts);
    return update(
        "status = ?1 where status = ?2 and lastAttemptAt < ?3",
        NotificationDeliveryStatus.PENDING,
        NotificationDeliveryStatus.SENDING,
        lastAttemptBefore);
  }

  public long deleteByEventIds(List<Long> eventIds) {
    return delete(
        "notification.id in (select n.id from Notification n where n.event.id in ?1)", eventIds);
  }
}
