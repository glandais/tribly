package fr.pedalons.repository.moderation;

import fr.pedalons.domain.moderation.UserBlock;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Who blocked whom. No domain column: a block is between two accounts of the same domain, so every
 * read keyed by the blocker is already bound to the blocker's domain.
 */
@ApplicationScoped
public class UserBlockRepository implements PanacheRepository<UserBlock> {

  public Optional<UserBlock> findByBlockerAndBlocked(Long blockerId, Long blockedId) {
    return find("blocker.id = ?1 and blocked.id = ?2", blockerId, blockedId).firstResultOptional();
  }

  public boolean isBlocked(Long blockerId, Long blockedId) {
    return count("blocker.id = ?1 and blocked.id = ?2", blockerId, blockedId) > 0;
  }

  /** Everyone this member blocked — one query, for masking a whole page of comments. */
  public Set<Long> findBlockedIds(Long blockerId) {
    return new HashSet<>(
        getEntityManager()
            .createQuery(
                "select b.blocked.id from UserBlock b where b.blocker.id = :blockerId", Long.class)
            .setParameter("blockerId", blockerId)
            .getResultList());
  }

  /** The live accounts this member blocked, most recent first, users loaded in the same query. */
  public List<UserBlock> findByBlocker(Long blockerId) {
    return getEntityManager()
        .createQuery(
            "select b from UserBlock b join fetch b.blocked u"
                + " where b.blocker.id = :blockerId and u.deleted = false"
                + " order by b.createdAt desc, b.id desc",
            UserBlock.class)
        .setParameter("blockerId", blockerId)
        .getResultList();
  }

  /** Every block this member made or received, for account erasure. */
  public long deleteByUser(Long userId) {
    return delete("blocker.id = ?1 or blocked.id = ?1", userId);
  }
}
