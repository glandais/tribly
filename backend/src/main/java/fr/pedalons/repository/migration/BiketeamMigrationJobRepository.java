package fr.pedalons.repository.migration;

import fr.pedalons.domain.migration.BiketeamMigrationJob;
import fr.pedalons.enums.BiketeamMigrationStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

/**
 * Grants and jobs of the biketeam live migration. Not scoped by domain on purpose: the M2M side has
 * no {@code Host} to resolve, the domain comes from the row, and the "one active job per biketeam
 * team" rule spans every domain of the database.
 */
@ApplicationScoped
public class BiketeamMigrationJobRepository implements PanacheRepository<BiketeamMigrationJob> {

  private static final List<BiketeamMigrationStatus> ACTIVE =
      List.of(BiketeamMigrationStatus.QUEUED, BiketeamMigrationStatus.RUNNING);

  public Optional<BiketeamMigrationJob> findByRequestId(String requestId) {
    return find("requestId", requestId).firstResultOptional();
  }

  /**
   * The value matched is a SHA-256 digest, never the grant itself, so there is no timing oracle
   * worth having — the argument {@code UserExportRepository.findDownloadableByTokenHash} makes.
   */
  public Optional<BiketeamMigrationJob> findByGrantHash(String grantHash) {
    return find("grantHash", grantHash).firstResultOptional();
  }

  /** The QUEUED or RUNNING job of a biketeam team, whatever its domain. */
  public Optional<BiketeamMigrationJob> findActiveByBiketeamTeamId(String biketeamTeamId) {
    return find("biketeamTeamId = ?1 and status in ?2", biketeamTeamId, ACTIVE)
        .firstResultOptional();
  }

  /**
   * Head of the queue, locked for this transaction. {@code skip locked} keeps a second worker off a
   * row someone already holds; the compare-and-set in {@link #claim} is what makes the claim safe.
   */
  public @Nullable Long findNextQueuedIdSkipLocked(Instant now) {
    Object id =
        getEntityManager()
            .createNativeQuery(
                """
                select id from biketeam_migrations
                where status = 'QUEUED' and (next_attempt_at is null or next_attempt_at <= :now)
                order by queued_at
                limit 1
                for update skip locked
                """)
            .setParameter("now", now)
            .getResultStream()
            .findFirst()
            .orElse(null);
    return id == null ? null : ((Number) id).longValue();
  }

  /**
   * Compare-and-set QUEUED → RUNNING. Returns true if this caller won the row. Like {@code
   * UserExportRepository.claim}, a bulk update that leaves {@code @Version} alone: the CAS on
   * {@code status} is the concurrency control, and the row is reloaded in a fresh transaction.
   */
  public boolean claim(Long id, Instant now) {
    return update(
            "status = ?1, attempts = attempts + 1, startedAt = coalesce(startedAt, ?2),"
                + " heartbeatAt = ?2, nextAttemptAt = null where id = ?3 and status = ?4",
            BiketeamMigrationStatus.RUNNING,
            now,
            id,
            BiketeamMigrationStatus.QUEUED)
        == 1;
  }

  /**
   * Compare-and-set RUNNING → QUEUED of the jobs silent since before {@code heartbeatBefore} that
   * have attempts left. Bulk, like {@link #claim}: no entity is loaded, so a concurrent write
   * cannot roll the whole recovery back. The condition is evaluated again on each row once a
   * concurrent writer releases it, so a heartbeat written meanwhile keeps its job RUNNING. {@code
   * version} is bumped so that a stale copy of the row cannot be written back over the recovery.
   * {@code errorCode}/{@code errorMessage} record why the attempt was lost — the status's {@code
   * lastAttemptError}, the job being QUEUED.
   */
  public int requeueStuck(
      Instant heartbeatBefore, int maxAttempts, Instant now, String errorCode, String message) {
    return update(
        "status = ?1, nextAttemptAt = ?2, updatedAt = ?2, errorCode = ?6, errorMessage = ?7,"
            + " version = version + 1"
            + " where status = ?3 and (heartbeatAt is null or heartbeatAt < ?4) and attempts < ?5",
        BiketeamMigrationStatus.QUEUED,
        now,
        BiketeamMigrationStatus.RUNNING,
        heartbeatBefore,
        maxAttempts,
        errorCode,
        message);
  }

  /** Same, RUNNING → FAILED {@code errorCode} for the stuck jobs that have no attempt left. */
  public int failStuck(
      Instant heartbeatBefore, int maxAttempts, Instant now, String errorCode, String message) {
    return update(
        "status = ?1, errorCode = ?2, errorMessage = ?3, finishedAt = ?4, updatedAt = ?4,"
            + " version = version + 1"
            + " where status = ?5 and (heartbeatAt is null or heartbeatAt < ?6) and attempts >= ?7",
        BiketeamMigrationStatus.FAILED,
        errorCode,
        message,
        now,
        BiketeamMigrationStatus.RUNNING,
        heartbeatBefore,
        maxAttempts);
  }

  /** GRANTED rows whose grant lapsed: they become EXPIRED. Nothing is ever deleted. */
  public int expireGrants(Instant now) {
    return update(
        "status = ?1 where status = ?2 and grantExpiresAt < ?3",
        BiketeamMigrationStatus.EXPIRED,
        BiketeamMigrationStatus.GRANTED,
        now);
  }
}
