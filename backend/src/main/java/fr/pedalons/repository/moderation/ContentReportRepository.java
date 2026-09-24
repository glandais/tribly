package fr.pedalons.repository.moderation;

import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.moderation.ContentReport;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.ReportReason;
import fr.pedalons.enums.ReportStatus;
import fr.pedalons.enums.ReportTargetType;
import io.hypersistence.tsid.TSID;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * Reports, and the moderation queues built from them.
 *
 * <p>Every queue read states its {@code domainId}: a report carries its own domain, and a queue
 * must never span two.
 */
@ApplicationScoped
public class ContentReportRepository implements PanacheRepository<ContentReport> {

  /** The statuses a decided report can have. */
  private static final List<ReportStatus> RESOLVED =
      List.of(ReportStatus.REMOVED, ReportStatus.DISMISSED);

  /** What a queue row shows, loaded with the report rather than per row. */
  private static final String FETCHES =
      "select r from ContentReport r join fetch r.team join fetch r.targetUser"
          + " left join fetch r.reporter";

  /**
   * Files a report unless this reporter already reported this target, and returns its id — empty
   * when there was one already.
   *
   * <p>Native on purpose, like {@code NotificationEventRepository.insertIfAbsent}: a check followed
   * by a {@code persist} lets two identical requests at once (a double tap, a client retry) both pass
   * the check, and the second would fail on {@code uk_content_reports_reporter_target} inside the
   * caller's transaction. {@code ON CONFLICT DO NOTHING} makes the duplicate a no-op, race included.
   */
  public Optional<Long> insertIfAbsent(
      Long domainId,
      Long teamId,
      Long reporterId,
      ReportTargetType targetType,
      Long targetId,
      Long targetUserId,
      ReportReason reason,
      @Nullable String message,
      @Nullable String excerpt,
      Instant now) {
    long id = TSID.Factory.getTsid().toLong();
    int inserted =
        getEntityManager()
            .createNativeQuery(
                """
                insert into content_reports
                  (id, domain_id, team_id, reporter_id, target_type, target_id, target_user_id,
                   reason, message, excerpt, status, created_at)
                values
                  (:id, :domainId, :teamId, :reporterId, :targetType, :targetId, :targetUserId,
                   :reason, cast(:message as varchar), cast(:excerpt as varchar), :status, :now)
                on conflict (reporter_id, target_type, target_id) do nothing
                """)
            .setParameter("id", id)
            .setParameter("domainId", domainId)
            .setParameter("teamId", teamId)
            .setParameter("reporterId", reporterId)
            .setParameter("targetType", targetType.name())
            .setParameter("targetId", targetId)
            .setParameter("targetUserId", targetUserId)
            .setParameter("reason", reason.name())
            .setParameter("message", message)
            .setParameter("excerpt", excerpt)
            .setParameter("status", ReportStatus.OPEN.name())
            .setParameter("now", Timestamp.from(now))
            .executeUpdate();
    return inserted == 1 ? Optional.of(id) : Optional.empty();
  }

  /**
   * Whether a moderator removed this publication from the queue. Such a removal is a decision on
   * the reports, not an author's gesture: the {@code undelete*} of the services must not undo it.
   */
  public boolean isRemovedByModeration(Long targetId) {
    return count(
            "targetId = ?1 and targetType in ?2 and status = ?3",
            targetId,
            List.of(
                ReportTargetType.POST,
                ReportTargetType.AD,
                ReportTargetType.RIDE,
                ReportTargetType.TRIP,
                ReportTargetType.ROUTE),
            ReportStatus.REMOVED)
        > 0;
  }

  /** How many distinct members have an open report on this target — the auto-hide threshold. */
  public long countOpenReporters(Long domainId, ReportTargetType targetType, Long targetId) {
    return getEntityManager()
        .createQuery(
            "select count(distinct r.reporter.id) from ContentReport r"
                + " where r.domain.id = :domainId and r.targetType = :type"
                + " and r.targetId = :targetId and r.status = :open",
            Long.class)
        .setParameter("domainId", domainId)
        .setParameter("type", targetType)
        .setParameter("targetId", targetId)
        .setParameter("open", ReportStatus.OPEN)
        .getSingleResult();
  }

  /**
   * Which of these targets this member reported — one query for a whole page, so a reporter stops
   * seeing what they reported.
   */
  public Set<Long> findReportedTargetIds(
      Long reporterId, ReportTargetType targetType, Collection<Long> targetIds) {
    if (targetIds.isEmpty()) {
      return Set.of();
    }
    return new HashSet<>(
        getEntityManager()
            .createQuery(
                "select r.targetId from ContentReport r where r.reporter.id = :reporterId"
                    + " and r.targetType = :type and r.targetId in (:ids)",
                Long.class)
            .setParameter("reporterId", reporterId)
            .setParameter("type", targetType)
            .setParameter("ids", targetIds)
            .getResultList());
  }

  /**
   * The open reports of one target, for a decision.
   *
   * @param teamId the team whose queue decides, or null for the platform queue
   * @param excludedTargetUserId a moderator never decides about themselves; null for none
   */
  public List<ContentReport> findOpenByTarget(
      Long domainId,
      @Nullable Long teamId,
      @Nullable Long excludedTargetUserId,
      ReportTargetType targetType,
      Long targetId) {
    return query(
            " and r.status = :open and r.targetType = :type and r.targetId = :targetId",
            " order by r.createdAt asc, r.id asc",
            domainId,
            teamId,
            excludedTargetUserId)
        .setParameter("open", ReportStatus.OPEN)
        .setParameter("type", targetType)
        .setParameter("targetId", targetId)
        .getResultList();
  }

  /** Every open report of a queue, most recent first. A queue is short: no pagination. */
  public List<ContentReport> findOpen(
      Long domainId, @Nullable Long teamId, @Nullable Long excludedTargetUserId) {
    return query(
            " and r.status = :open",
            " order by r.createdAt desc, r.id desc",
            domainId,
            teamId,
            excludedTargetUserId)
        .setParameter("open", ReportStatus.OPEN)
        .getResultList();
  }

  /**
   * The {@code limit} most recently decided targets of a queue, then every decided report of those
   * targets: two queries, however many reports each target gathered.
   */
  public List<ContentReport> findResolved(
      Long domainId, @Nullable Long teamId, @Nullable Long excludedTargetUserId, int limit) {
    StringBuilder targets =
        new StringBuilder(
            "select r.targetId from ContentReport r where r.domain.id = :domainId"
                + " and r.status in (:resolved)");
    if (teamId != null) {
      targets.append(" and r.team.id = :teamId");
    }
    if (excludedTargetUserId != null) {
      targets.append(" and r.targetUser.id <> :excluded");
    }
    targets.append(" group by r.targetId order by max(r.resolvedAt) desc, r.targetId desc");
    TypedQuery<Long> targetQuery =
        getEntityManager()
            .createQuery(targets.toString(), Long.class)
            .setParameter("domainId", domainId)
            .setParameter("resolved", RESOLVED)
            .setMaxResults(limit);
    if (teamId != null) {
      targetQuery.setParameter("teamId", teamId);
    }
    if (excludedTargetUserId != null) {
      targetQuery.setParameter("excluded", excludedTargetUserId);
    }
    List<Long> targetIds = targetQuery.getResultList();
    if (targetIds.isEmpty()) {
      return List.of();
    }
    return query(
            " and r.status in (:resolved) and r.targetId in (:targetIds)",
            " order by r.resolvedAt desc, r.id desc",
            domainId,
            teamId,
            excludedTargetUserId)
        .setParameter("resolved", RESOLVED)
        .setParameter("targetIds", targetIds)
        .getResultList();
  }

  private TypedQuery<ContentReport> query(
      String where,
      String order,
      Long domainId,
      @Nullable Long teamId,
      @Nullable Long excludedTargetUserId) {
    StringBuilder hql = new StringBuilder(FETCHES).append(" where r.domain.id = :domainId");
    if (teamId != null) {
      hql.append(" and r.team.id = :teamId");
    }
    if (excludedTargetUserId != null) {
      hql.append(" and r.targetUser.id <> :excluded");
    }
    hql.append(where).append(order);
    TypedQuery<ContentReport> query =
        getEntityManager()
            .createQuery(hql.toString(), ContentReport.class)
            .setParameter("domainId", domainId);
    if (teamId != null) {
      query.setParameter("teamId", teamId);
    }
    if (excludedTargetUserId != null) {
      query.setParameter("excluded", excludedTargetUserId);
    }
    return query;
  }

  /** The publications a queue is about, in one query, deleted or not. */
  public List<TeamEntity> findTeamEntities(Long domainId, Collection<Long> ids) {
    if (ids.isEmpty()) {
      return List.of();
    }
    return getEntityManager()
        .createQuery(
            "select te from TeamEntity te where te.id in (:ids) and te.team.domain.id = :domainId",
            TeamEntity.class)
        .setParameter("ids", ids)
        .setParameter("domainId", domainId)
        .getResultList();
  }

  /** One reported publication of a team, deleted or not. */
  public Optional<TeamEntity> findTeamEntity(Long teamId, Long id) {
    return getEntityManager()
        .createQuery(
            "select te from TeamEntity te where te.id = :id and te.team.id = :teamId",
            TeamEntity.class)
        .setParameter("id", id)
        .setParameter("teamId", teamId)
        .getResultStream()
        .findFirst();
  }

  /**
   * Closes the open reports of several targets of a team at once — the replies a removed comment
   * took with it.
   */
  public int resolveOpen(
      Long teamId,
      ReportTargetType targetType,
      Collection<Long> targetIds,
      ReportStatus status,
      User resolvedBy,
      Instant resolvedAt) {
    if (targetIds.isEmpty()) {
      return 0;
    }
    return getEntityManager()
        .createQuery(
            "update ContentReport r set r.status = :status, r.resolvedBy = :resolvedBy,"
                + " r.resolvedAt = :resolvedAt"
                + " where r.team.id = :teamId and r.status = :open and r.targetType = :type"
                + " and r.targetId in (:ids)")
        .setParameter("status", status)
        .setParameter("resolvedBy", resolvedBy)
        .setParameter("resolvedAt", resolvedAt)
        .setParameter("teamId", teamId)
        .setParameter("open", ReportStatus.OPEN)
        .setParameter("type", targetType)
        .setParameter("ids", targetIds)
        .executeUpdate();
  }

  /** The reports a member filed, for the GDPR export. */
  public List<ContentReport> findByReporter(Long domainId, Long reporterId) {
    return list("reporter.id = ?1 and domain.id = ?2 order by createdAt", reporterId, domainId);
  }

  // ------------------------------------------------------------------ account erasure

  /** The reports about a member: their excerpt is the member's own content. */
  public long deleteTargeting(Long userId) {
    return delete("targetUser.id = ?1", userId);
  }

  /** Keeps the member's reports, and the decisions they led to, without the member. */
  public void forgetReporterAndResolver(Long userId) {
    update("reporter = null where reporter.id = ?1", userId);
    update("resolvedBy = null where resolvedBy.id = ?1", userId);
  }
}
