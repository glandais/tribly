package fr.pedalons.repository.feedback;

import fr.pedalons.domain.feedback.ErrorOccurrence;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ErrorOccurrenceRepository implements PanacheRepository<ErrorOccurrence> {

  /** What {@code FeedbackGithubWorker} sums up in the daily comment of an issue. */
  public record Summary(long occurrences, long users, List<String> versions) {}

  /** Error reports this member's clients sent since {@code since} — the rate limit. */
  public long countByUserSince(Long userId, Instant since) {
    return count("user.id = ?1 and createdAt >= ?2", userId, since);
  }

  /** The most recent occurrence of a signature: what its issue shows. */
  public Optional<ErrorOccurrence> findLatest(Long signatureId) {
    return find("signature.id = ?1 order by createdAt desc, id desc", signatureId)
        .firstResultOptional();
  }

  /** Occurrences of a signature after {@code since}: three aggregate queries, whatever the count. */
  public Summary summarizeSince(Long signatureId, Instant since) {
    long occurrences = count("signature.id = ?1 and createdAt > ?2", signatureId, since);
    long users =
        getEntityManager()
            .createQuery(
                "select count(distinct o.user.id) from ErrorOccurrence o"
                    + " where o.signature.id = :id and o.createdAt > :since",
                Long.class)
            .setParameter("id", signatureId)
            .setParameter("since", since)
            .getSingleResult();
    List<String> versions =
        getEntityManager()
            .createQuery(
                "select distinct o.appVersion from ErrorOccurrence o"
                    + " where o.signature.id = :id and o.createdAt > :since"
                    + " order by o.appVersion",
                String.class)
            .setParameter("id", signatureId)
            .setParameter("since", since)
            .getResultList();
    return new Summary(occurrences, users, versions);
  }

  /** Distinct versions a signature was reported on, after {@code after} (exclusive) or at/before. */
  public List<String> findVersions(Long signatureId, Instant instant, boolean after) {
    return getEntityManager()
        .createQuery(
            "select distinct o.appVersion from ErrorOccurrence o where o.signature.id = :id and"
                + (after ? " o.createdAt > :instant" : " o.createdAt <= :instant"),
            String.class)
        .setParameter("id", signatureId)
        .setParameter("instant", instant)
        .getResultList();
  }

  public long deleteCreatedBefore(Instant before) {
    return delete("createdAt < ?1", before);
  }
}
