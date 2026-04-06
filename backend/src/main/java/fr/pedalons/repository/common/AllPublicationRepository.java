package fr.pedalons.repository.common;

import fr.pedalons.domain.common.Publication;
import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.TeamEntityType;
import fr.pedalons.repository.query.PedalonsQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AllPublicationRepository
    implements TeamEntityRepository<Publication, PublicationQuery> {
  @Override
  public TeamEntityType getEntityType() {
    return TeamEntityType.PUBLICATION;
  }

  @Override
  public EntityType getAllEntityType() {
    return EntityType.PUBLICATION;
  }

  /**
   * Find publications that should be auto-published (DRAFT status with publishAt in the past).
   */
  public List<Publication> findPublicationsToAutoPublish() {
    return find(
            "status = ?1 and publishAt is not null and publishAt <= ?2 and deleted = false",
            Status.DRAFT,
            Instant.now())
        .list();
  }

  @Override
  public PedalonsQuery andSpecific(PedalonsQuery pedalonsQuery, PublicationQuery query) {
    PublicationType publicationType = query.type();
    if (publicationType != null) {
      pedalonsQuery =
          pedalonsQuery.and("TYPE(te) = :type", Map.of("type", publicationType.getType()));
    }
    return pedalonsQuery;
  }

  @Override
  public PublicationQuery getQuerySlug(
      Long domainId, Long teamId, @Nullable Long userId, String slug, boolean includeDeleted) {
    return PublicationQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .slug(slug)
        .includeDeleted(includeDeleted)
        .build();
  }

  @Override
  public PublicationQuery getQueryId(
      Long domainId, Long teamId, @Nullable Long userId, Long id, boolean includeDeleted) {
    return PublicationQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .id(id)
        .includeDeleted(includeDeleted)
        .build();
  }
}
