package fr.pedalons.repository.tag;

import fr.pedalons.domain.tag.RideTemplateTag;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.List;

/** Tags on ride templates — same shape as {@link TeamEntityTagRepository}. */
@ApplicationScoped
public class RideTemplateTagRepository implements PanacheRepository<RideTemplateTag> {

  /** The (template id, tag) pairs of a page of templates, sorted by label. One query. */
  public List<Object[]> findTagsByTemplateIds(Long domainId, Collection<Long> templateIds) {
    if (templateIds.isEmpty()) {
      return List.of();
    }
    return getEntityManager()
        .createQuery(
            "select l.rideTemplate.id, t from RideTemplateTag l join l.tag t"
                + " where l.rideTemplate.id in (:ids) and t.team.domain.id = :domainId"
                + " order by lower(t.label), t.id",
            Object[].class)
        .setParameter("ids", templateIds)
        .setParameter("domainId", domainId)
        .getResultList();
  }

  public List<RideTemplateTag> findByTemplateId(Long templateId) {
    return list("rideTemplate.id", templateId);
  }

  public long deleteByTag(Long tagId) {
    return delete("tag.id", tagId);
  }
}
