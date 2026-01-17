package com.tribly.repository.common;

import com.tribly.domain.common.Publication;
import com.tribly.dto.publications.response.PublicationType;
import com.tribly.enums.EntityType;
import com.tribly.enums.Status;
import com.tribly.enums.TeamEntityType;
import com.tribly.repository.query.TriblyQuery;
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
  public TriblyQuery andSpecific(TriblyQuery triblyQuery, PublicationQuery query) {
    PublicationType publicationType = query.type();
    if (publicationType != null) {
      triblyQuery = triblyQuery.and("TYPE(te) = :type", Map.of("type", publicationType.getType()));
    }
    return triblyQuery;
  }

  @Override
  public PublicationQuery getQuerySlug(
      Long domainId, Long teamId, @Nullable Long userId, String slug) {
    return PublicationQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .slug(slug)
        .build();
  }

  @Override
  public PublicationQuery getQueryId(Long domainId, Long teamId, @Nullable Long userId, Long id) {
    return PublicationQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .id(id)
        .build();
  }
}
