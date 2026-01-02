package com.tribly.domain.common.repository;

import com.tribly.domain.common.Publication;
import com.tribly.domain.common.query.TriblyQuery;
import com.tribly.dto.publications.response.PublicationType;
import com.tribly.enums.Status;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class AllPublicationRepository
    implements TeamEntityRepository<Publication, PublicationQuery> {
  @Override
  public String getTypeName() {
    return "Publication";
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
    if (query.types() != null && !query.types().isEmpty()) {
      triblyQuery =
          triblyQuery.and(
              "TYPE(te) IN (:types)",
              Map.of("types", query.types().stream().map(PublicationType::getTypeName).toList()));
    }
    return triblyQuery;
  }
}
