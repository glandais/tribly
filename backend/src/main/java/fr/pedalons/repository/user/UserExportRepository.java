package fr.pedalons.repository.user;

import fr.pedalons.domain.user.UserExport;
import fr.pedalons.enums.UserExportStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class UserExportRepository implements PanacheRepository<UserExport> {

  /** The user's most recent request, whatever its status — drives both cooldown and UI state. */
  public Optional<UserExport> findLatestByUser(Long userId) {
    return find("user.id = ?1 order by requestedAt desc", userId).firstResultOptional();
  }

  /** A job by id, scoped to its owner: another user's job must 404, not 403. */
  public Optional<UserExport> findByIdAndUser(Long id, Long userId) {
    return find("id = ?1 and user.id = ?2", id, userId).firstResultOptional();
  }

  public boolean hasInFlight(Long userId) {
    return count(
            "user.id = ?1 and status in ?2",
            userId,
            List.of(UserExportStatus.PENDING, UserExportStatus.PROCESSING))
        > 0;
  }

  /**
   * Head of the queue, locked for this transaction.
   *
   * <p>{@code skip locked} is the throughput half of the claim: it stops a second worker from
   * picking a row someone else already holds and then burning a lock wait to discover it lost. The
   * correctness half is the compare-and-set in {@link #claim}.
   */
  public @Nullable Long findNextPendingIdSkipLocked() {
    Object id =
        getEntityManager()
            .createNativeQuery(
                """
                select id from user_exports
                where status = 'PENDING'
                order by requested_at
                limit 1
                for update skip locked
                """)
            .getResultStream()
            .findFirst()
            .orElse(null);
    return id == null ? null : ((Number) id).longValue();
  }

  /**
   * Compare-and-set from PENDING to PROCESSING. Returns true if this caller won the row.
   *
   * <p>Under READ COMMITTED a loser blocks on the row lock, re-evaluates {@code status = PENDING}
   * after the winner commits, and updates 0 rows. This — not the {@code skip locked} above — is what
   * makes the claim safe.
   *
   * <p>A bulk update does not bump {@code @Version} unless written {@code versioned update}. That is
   * deliberate: the CAS on {@code status} <em>is</em> the concurrency control, and the row is
   * reloaded in a fresh transaction afterwards, so no stale managed instance can exist.
   */
  public boolean claim(Long id, Instant now) {
    return update(
            "status = ?1, startedAt = ?2, attempts = attempts + 1 where id = ?3 and status = ?4",
            UserExportStatus.PROCESSING,
            now,
            id,
            UserExportStatus.PENDING)
        == 1;
  }

  /**
   * Resolves a download token, scoped to a domain and to a live user.
   *
   * <p>The value matched is a SHA-256 digest, never the secret, so there is no timing oracle worth
   * having — the same argument that already justifies {@code
   * AuthSessionRepository.findByRefreshTokenHash}. The domain filter is redundant for correctness
   * (the hash is globally unique) and there so a leaked link cannot cross a tenant boundary.
   * Expiry is deliberately <em>not</em> in the predicate: the caller checks it, so an
   * expired-but-not-yet-swept row still 404s.
   */
  public Optional<UserExport> findDownloadableByTokenHash(Long domainId, String tokenHash) {
    return find(
            "domain.id = ?1 and downloadTokenHash = ?2 and status = ?3 and user.deleted = false",
            domainId,
            tokenHash,
            UserExportStatus.READY)
        .firstResultOptional();
  }

  /** READY exports past their expiry, for the retention sweep. */
  public List<UserExport> findExpired(Instant now) {
    return list("status = ?1 and expiresAt < ?2", UserExportStatus.READY, now);
  }

  /**
   * Jobs claimed but never finished — a crash between claim and completion. Left alone they would
   * block the user forever via the partial unique index on in-flight rows.
   */
  public List<UserExport> findStuck(Instant startedBefore) {
    return list("status = ?1 and startedAt < ?2", UserExportStatus.PROCESSING, startedBefore);
  }

  /** READY exports whose "your export is ready" email never went out. */
  public List<UserExport> findWithUnsentEmail() {
    return list("status = ?1 and emailSentAt is null", UserExportStatus.READY);
  }

  /** Hard-deletes long-finished rows; the S3 objects are already gone by then. */
  public long deleteOlderThan(Instant cutoff) {
    return delete(
        "requestedAt < ?1 and status in ?2",
        cutoff,
        List.of(UserExportStatus.EXPIRED, UserExportStatus.FAILED));
  }
}
