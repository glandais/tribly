package fr.pedalons.repository.notification;

import fr.pedalons.domain.notification.TeamWebhookDelivery;
import fr.pedalons.enums.NotificationDeliveryStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/** The team webhooks' send queue — the same claim, mark and recovery as {@code NotificationDeliveryRepository}. */
@ApplicationScoped
public class TeamWebhookDeliveryRepository implements PanacheRepository<TeamWebhookDelivery> {

  @SuppressWarnings("unchecked")
  public List<Long> lockDue(Instant now, int limit) {
    List<Number> ids =
        getEntityManager()
            .createNativeQuery(
                """
                select id from team_webhook_deliveries
                where status = 'PENDING' and next_attempt_at <= :now
                order by next_attempt_at
                limit :limit
                for update skip locked
                """)
            .setParameter("now", Timestamp.from(now))
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

  /** With the event snapshot and the webhook, in one query. */
  public List<TeamWebhookDelivery> findWithContent(List<Long> ids) {
    return getEntityManager()
        .createQuery(
            "select d from TeamWebhookDelivery d"
                + " join fetch d.event"
                + " join fetch d.webhook w"
                + " join fetch w.team"
                + " where d.id in :ids",
            TeamWebhookDelivery.class)
        .setParameter("ids", ids)
        .getResultList();
  }

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
    return delete("event.id in ?1", eventIds);
  }
}
