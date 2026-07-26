package fr.pedalons.service.comment;

import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.dto.comments.response.CommentCounts;
import fr.pedalons.repository.comment.CommentRepository;
import fr.pedalons.repository.team.UserTeamRepository;
import fr.pedalons.service.security.PedalonsQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Resolves {@code commentCount} for a set of entities, in bulk and under the comment visibility
 * rules.
 *
 * <p>Two things have to be true of this number, and they pull in opposite directions:
 *
 * <ul>
 *   <li><b>It follows the comments.</b> {@code CommentAccessChecker} grants {@code LIST} only to a
 *       user holding a role in the owning team, so a visitor who cannot open the thread must not be
 *       told how busy it is. Entities the caller may not read are left out of {@link CommentCounts}
 *       entirely and the field disappears from the JSON — never a real count, never a fake zero.
 *   <li><b>It costs a page, not a row.</b> Two queries whatever the page size: one for the teams the
 *       caller belongs to among those on the page, one for the counts of the entities that survives.
 *       A per-row {@code countByTeamEntityId} would put a query behind every card of a feed.
 * </ul>
 *
 * <p>An anonymous caller costs nothing: no membership, no readable entity, no query.
 */
@ApplicationScoped
public class CommentCountLookup {

  @Inject CommentRepository commentRepository;

  @Inject UserTeamRepository userTeamRepository;

  @Inject PedalonsQueryContext pedalonsContext;

  /** Comment counts for a single entity — the detail path. Same rules, same two queries. */
  public CommentCounts forEntity(TeamEntity entity) {
    return forEntities(List.of(entity));
  }

  /** Comment counts for a whole page of entities. */
  public CommentCounts forEntities(Collection<? extends TeamEntity> entities) {
    Long userId = pedalonsContext.getUserIdNullable();
    if (userId == null || entities.isEmpty()) {
      return CommentCounts.NONE;
    }
    // getTeam() is a lazy proxy; getId() on it reads the foreign key already in hand.
    Set<Long> teamIds = new HashSet<>();
    for (TeamEntity entity : entities) {
      teamIds.add(entity.getTeam().getId());
    }
    Long domainId = pedalonsContext.getDomainId();
    Set<Long> memberTeamIds = userTeamRepository.findMemberTeamIds(userId, domainId, teamIds);
    if (memberTeamIds.isEmpty()) {
      return CommentCounts.NONE;
    }

    Set<Long> readableEntityIds = new HashSet<>();
    for (TeamEntity entity : entities) {
      if (memberTeamIds.contains(entity.getTeam().getId())) {
        readableEntityIds.add(entity.getId());
      }
    }
    if (readableEntityIds.isEmpty()) {
      return CommentCounts.NONE;
    }

    Map<Long, Integer> counted =
        commentRepository.countByTeamEntityIds(domainId, readableEntityIds);
    // An entity with no comment yet is readable and worth a 0 — only a non-readable entity is
    // allowed to be absent, and absent is what makes the field vanish.
    Map<Long, Integer> counts = new HashMap<>(readableEntityIds.size());
    for (Long entityId : readableEntityIds) {
      counts.put(entityId, counted.getOrDefault(entityId, 0));
    }
    return new CommentCounts(counts);
  }
}
