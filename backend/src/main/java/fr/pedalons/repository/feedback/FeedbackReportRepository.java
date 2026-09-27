package fr.pedalons.repository.feedback;

import fr.pedalons.domain.feedback.FeedbackReport;
import fr.pedalons.enums.GithubSyncStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;

@ApplicationScoped
public class FeedbackReportRepository implements PanacheRepository<FeedbackReport> {

  /** Reports this member filed since {@code since} — the rate limit. */
  public long countByUserSince(Long userId, Instant since) {
    return count("user.id = ?1 and createdAt >= ?2", userId, since);
  }

  /** The ids of the reports still to publish, oldest first. */
  public List<Long> findPendingIds(int limit) {
    return getEntityManager()
        .createQuery(
            "select f.id from FeedbackReport f where f.githubStatus = :pending"
                + " order by f.createdAt asc, f.id asc",
            Long.class)
        .setParameter("pending", GithubSyncStatus.PENDING)
        .setMaxResults(limit)
        .getResultList();
  }

  /** The reports a member filed, for the GDPR export. */
  public List<FeedbackReport> findByUser(Long domainId, Long userId) {
    return list("user.id = ?1 and domain.id = ?2 order by createdAt", userId, domainId);
  }

  public long deleteCreatedBefore(Instant before) {
    return delete("createdAt < ?1", before);
  }
}
