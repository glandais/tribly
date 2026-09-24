package fr.pedalons.repository.comment;

import fr.pedalons.domain.comment.Comment;
import fr.pedalons.enums.SortDirection;
import fr.pedalons.repository.common.BaseRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Comments are reached through their owning {@code TeamEntity}, never on their own.
 *
 * <p><b>Tenancy.</b> This repository is not a {@code TeamEntityRepository} and carries no implicit
 * {@code domainId} clause. It does not need one on the entity-scoped reads: every caller resolves
 * the owning entity first, through {@code TeamService.getTeam(teamSlug)} (filtered by {@code
 * domainId}) and then {@code TeamEntityService.findBySlug} (which goes through {@code PedalonsQuery}
 * with that same {@code domainId}), so the id handed here has already been proven to belong to the
 * current domain. The methods that take a raw id from the caller — {@link #findByTeamIdAndId} — are
 * scoped by that already-resolved team.
 *
 * <p>The <b>bulk</b> reads are different: they take a set of ids rather than one proven id, so they
 * re-state the domain clause themselves ({@link #countByTeamEntityIds}). A count query is cheap; a
 * count that silently spans domains is not.
 */
@ApplicationScoped
public class CommentRepository implements BaseRepository<Comment> {

  public List<Comment> findByTeamEntityId(Long teamEntityId) {
    return find(
            // The id tie-breaks equal timestamps: two comments posted in the same microsecond used
            // to come back in whatever order the database felt like, which made a page boundary
            // non-deterministic. TSIDs are time-sorted, so it orders exactly as createdAt intends.
            "select c from Comment c join fetch c.createdBy"
                + " where c.teamEntity.id = ?1 order by c.createdAt asc, c.id asc",
            teamEntityId)
        .list();
  }

  public Optional<Comment> findByTeamIdAndId(Long teamId, Long id) {
    return find(
            "teamEntity.team.id = ?1 AND teamEntity.team.deleted = false AND id = ?2", teamId, id)
        .firstResultOptional();
  }

  public List<Comment> findReplies(Long parentId) {
    return find("parent.id = ?1", parentId).list();
  }

  /**
   * Comments by id with the entity they are on, in one query — the comments a moderation queue is
   * about. Re-states the domain, the ids coming as a set.
   */
  public List<Comment> findByIdsWithEntity(Long domainId, Collection<Long> ids) {
    if (ids.isEmpty()) {
      return List.of();
    }
    return getEntityManager()
        .createQuery(
            "select c from Comment c join fetch c.teamEntity te"
                + " where c.id in (:ids) and te.team.domain.id = :domainId",
            Comment.class)
        .setParameter("ids", ids)
        .setParameter("domainId", domainId)
        .getResultList();
  }

  /** Comments a user wrote, for the GDPR data export. */
  public List<Comment> findByCreator(Long domainId, Long userId) {
    return list(
        "createdBy.id = ?2 and teamEntity.team.domain.id = ?1 order by createdAt",
        domainId,
        userId);
  }

  public long countByTeamEntityId(Long teamEntityId) {
    return count("teamEntity.id = ?1", teamEntityId);
  }

  /**
   * Comment count per entity, for a whole page at once — one query, whatever the page size.
   *
   * <p>Entities with no comment are absent from the map; the caller decides whether that means zero
   * or "not readable", because only the caller knows which entities it asked about.
   *
   * @param domainId re-stated here on purpose: unlike the entity-scoped reads, the ids come as a set
   *     rather than as a single already-resolved id
   */
  public Map<Long, Integer> countByTeamEntityIds(Long domainId, Collection<Long> teamEntityIds) {
    if (teamEntityIds.isEmpty()) {
      return Map.of();
    }
    List<Object[]> rows =
        getEntityManager()
            .createQuery(
                "select c.teamEntity.id, count(c.id) from Comment c"
                    + " where c.teamEntity.id in (:ids)"
                    + " and c.teamEntity.team.domain.id = :domainId"
                    + " group by c.teamEntity.id",
                Object[].class)
            .setParameter("ids", teamEntityIds)
            .setParameter("domainId", domainId)
            .getResultList();
    Map<Long, Integer> counts = new HashMap<>();
    for (Object[] row : rows) {
      counts.put((Long) row[0], ((Number) row[1]).intValue());
    }
    return counts;
  }

  /** How many top-level comments this entity has. */
  public long countRoots(Long teamEntityId) {
    return count("teamEntity.id = ?1 and parent is null", teamEntityId);
  }

  /** How many replies this comment has. One level of threading, so replies have none of their own. */
  public long countReplies(Long teamEntityId, Long parentId) {
    return count("teamEntity.id = ?1 and parent.id = ?2", teamEntityId, parentId);
  }

  /**
   * One page of top-level comments, oldest or newest first.
   *
   * <p>Paginating the <em>roots</em> rather than the flat comment list is what makes a page of the
   * tree well-defined: a root and its replies always travel together.
   */
  public List<Comment> pageRoots(Long teamEntityId, int page, int size, SortDirection sort) {
    return rootQuery(teamEntityId, sort).page(page, size).list();
  }

  private PanacheQuery<Comment> rootQuery(Long teamEntityId, SortDirection sort) {
    return find(
        "select c from Comment c join fetch c.createdBy"
            + " where c.teamEntity.id = :teamEntityId and c.parent is null"
            + " order by c.createdAt "
            + direction(sort)
            + ", c.id "
            + direction(sort),
        Parameters.with("teamEntityId", teamEntityId));
  }

  /**
   * Every reply of the given roots, in one query.
   *
   * <p>The roots are already bounded by the page size, so this stays one query per page rather than
   * one per row — the shape the ride and trip list summaries settled on.
   */
  public List<Comment> findRepliesByParentIds(Collection<Long> parentIds, SortDirection sort) {
    if (parentIds.isEmpty()) {
      return List.of();
    }
    return find(
            "select c from Comment c join fetch c.createdBy"
                + " where c.parent.id in (:parentIds)"
                + " order by c.createdAt "
                + direction(sort)
                + ", c.id "
                + direction(sort),
            Parameters.with("parentIds", parentIds))
        .list();
  }

  /**
   * One page of the replies of a single comment.
   *
   * @param teamEntityId the entity the thread must belong to — a comment id from another entity (or
   *     another team, hence another domain) yields an empty page rather than someone else's thread
   */
  public List<Comment> pageReplies(
      Long teamEntityId, Long parentId, int page, int size, SortDirection sort) {
    return find(
            "select c from Comment c join fetch c.createdBy"
                + " where c.teamEntity.id = :teamEntityId and c.parent.id = :parentId"
                + " order by c.createdAt "
                + direction(sort)
                + ", c.id "
                + direction(sort),
            Parameters.with("teamEntityId", teamEntityId).and("parentId", parentId))
        .page(page, size)
        .list();
  }

  // ------------------------------------------------------------------ account erasure
  //
  // An erased account's comments go, except a top-level comment others have answered: deleting it
  // would take their replies with it, so it stays as a tombstone — its content blanked, its author
  // the anonymized account. A tombstone is recognised by that author alone (see CommentDto); these
  // bulk statements are the only writes that produce or remove one.

  /** The threads a user replied to: the tombstones their erasure may leave with no reply. */
  public List<Long> findParentIdsOfRepliesBy(Long userId) {
    return getEntityManager()
        .createQuery(
            "select distinct c.parent.id from Comment c"
                + " where c.createdBy.id = :userId and c.parent is not null",
            Long.class)
        .setParameter("userId", userId)
        .getResultList();
  }

  /** Deletes a user's replies. Threading is one level deep, so a reply has nothing below it. */
  public long deleteRepliesBy(Long userId) {
    return delete("createdBy.id = ?1 and parent is not null", userId);
  }

  /** Deletes a user's top-level comments that nobody answered. */
  public long deleteUnansweredRootsBy(Long userId) {
    return getEntityManager()
        .createQuery(
            "delete from Comment c where c.createdBy.id = :userId and c.parent is null"
                + " and not exists (select r.id from Comment r where r.parent.id = c.id)")
        .setParameter("userId", userId)
        .executeUpdate();
  }

  /** Blanks what is left of a user's comments: the answered top-level ones, now tombstones. */
  public long blankContentBy(Long userId) {
    return update("content = '' where createdBy.id = ?1", userId);
  }

  /**
   * Deletes, among the given comments, the tombstones that no longer have any reply — the last one
   * was just deleted, by its author or by another erasure. A live comment is never touched.
   */
  public long deleteEmptyTombstones(Collection<Long> ids) {
    if (ids.isEmpty()) {
      return 0;
    }
    return getEntityManager()
        .createQuery(
            "delete from Comment c where c.id in (:ids) and c.parent is null"
                + " and c.createdBy.id in (select u.id from User u where u.deleted = true)"
                + " and not exists (select r.id from Comment r where r.parent.id = c.id)")
        .setParameter("ids", ids)
        .executeUpdate();
  }

  /** Never interpolates anything but one of the two enum constants. */
  private static String direction(SortDirection sort) {
    return sort == SortDirection.DESC ? "desc" : "asc";
  }
}
