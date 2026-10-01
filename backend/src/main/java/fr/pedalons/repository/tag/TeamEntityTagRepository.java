package fr.pedalons.repository.tag;

import fr.pedalons.domain.tag.TeamEntityTag;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.List;

/**
 * Tags on rides, posts, trips, routes and ads. Callers hand in ids of contents they already read
 * through a domain-filtered query; the lookups below restate the domain anyway, on the tag's team.
 */
@ApplicationScoped
public class TeamEntityTagRepository implements PanacheRepository<TeamEntityTag> {

  /**
   * The (content id, tag) pairs of a page of contents, tags loaded in the same query and sorted by
   * label. One query whatever the page size.
   */
  public List<Object[]> findTagsByEntityIds(Long domainId, Collection<Long> teamEntityIds) {
    if (teamEntityIds.isEmpty()) {
      return List.of();
    }
    return getEntityManager()
        .createQuery(
            "select l.teamEntity.id, t from TeamEntityTag l join l.tag t"
                + " where l.teamEntity.id in (:ids) and t.team.domain.id = :domainId"
                + " order by lower(t.label), t.id",
            Object[].class)
        .setParameter("ids", teamEntityIds)
        .setParameter("domainId", domainId)
        .getResultList();
  }

  public List<TeamEntityTag> findByEntityId(Long teamEntityId) {
    return list("teamEntity.id", teamEntityId);
  }

  /** Every content's link to this tag, trashed contents included. */
  public long deleteByTag(Long tagId) {
    return delete("tag.id", tagId);
  }
}
